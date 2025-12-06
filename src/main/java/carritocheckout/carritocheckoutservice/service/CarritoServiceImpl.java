package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.dtos.CarritoDTO;
import carritocheckout.carritocheckoutservice.dtos.ProductoDTOResponse;
import carritocheckout.carritocheckoutservice.entities.Carrito;
import carritocheckout.carritocheckoutservice.entities.ItemCarrito;
import carritocheckout.carritocheckoutservice.mapper.CarritoMapper;
import carritocheckout.carritocheckoutservice.repository.CarritoRepository;
import carritocheckout.carritocheckoutservice.repository.ItemRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private RestTemplate restTemplate;

    @Value(
        "${catalogo.service.url:https://catalogo-service-dcc3a7dgbja8b6dd.canadacentral-01.azurewebsites.net}"
    )
    private String catalogoServiceUrl;

    private final CarritoRepository carritoRepository;
    private final ItemRepository itemRepository;
    private final CarritoMapper carritoMapper;

    public CarritoServiceImpl(
        CarritoRepository carritoRepository,
        ItemRepository itemRepository,
        CarritoMapper carritoMapper
    ) {
        this.carritoRepository = carritoRepository;
        this.itemRepository = itemRepository;
        this.carritoMapper = carritoMapper;
    }

    private Carrito crearNuevoCarritoEntidad(Integer idUsuario) {
        Carrito nuevoCarrito = new Carrito();
        nuevoCarrito.setIdUsuario(idUsuario);
        nuevoCarrito.setTotal(0.0);
        nuevoCarrito.setItems(new ArrayList<>());
        return carritoRepository.save(nuevoCarrito);
    }

    @Override
    public CarritoDTO agregarCarrito(Integer idUsuario) {
        Carrito carrito = (idUsuario != null)
            ? carritoRepository
                  .findByIdUsuario(idUsuario)
                  .orElseGet(() -> crearNuevoCarritoEntidad(idUsuario))
            : crearNuevoCarritoEntidad(null);

        return carritoMapper.toDTO(carrito);
    }

    @Override
    public CarritoDTO asignarCarritoAUsuario(
        Integer idCarritoAnonimo,
        Integer idUsuario
    ) {
        // Carrito anónimo traído desde el cliente
        Carrito carritoAnonimo = obtenerCarritoEntidadPorId(idCarritoAnonimo);

        // Carrito del usuario si existía previamente
        Optional<Carrito> carritoPrevioOpt = carritoRepository.findByIdUsuario(
            idUsuario
        );

        // Si NO tiene carrito previo → solo asignar el carrito anónimo al usuario
        if (carritoPrevioOpt.isEmpty()) {
            carritoAnonimo.setIdUsuario(idUsuario);
            Carrito actualizado = carritoRepository.save(carritoAnonimo);
            return carritoMapper.toDTO(actualizado);
        }

        // Si tiene carrito previo → MERGE
        Carrito carritoPrevio = carritoPrevioOpt.get();

        if (carritoAnonimo.getItems() != null) {
            for (ItemCarrito itemAnonimo : carritoAnonimo.getItems()) {
                // Buscar si ese producto+variante ya existe en el carrito previo
                Optional<ItemCarrito> itemExistenteOpt = carritoPrevio
                    .getItems()
                    .stream()
                    .filter(
                        i ->
                            i
                                .getProductoId()
                                .equals(itemAnonimo.getProductoId()) &&
                            i
                                .getIdVariante()
                                .equals(itemAnonimo.getIdVariante())
                    )
                    .findFirst();

                if (itemExistenteOpt.isPresent()) {
                    // Si existe → sumar cantidades
                    ItemCarrito itemExistente = itemExistenteOpt.get();
                    itemExistente.setCantidad(
                        itemExistente.getCantidad() + itemAnonimo.getCantidad()
                    );
                    itemRepository.save(itemExistente);
                } else {
                    // No existe → migrar item del carrito anónimo al carrito previo
                    ItemCarrito nuevoItem = new ItemCarrito();
                    nuevoItem.setCarrito(carritoPrevio);
                    nuevoItem.setProductoId(itemAnonimo.getProductoId());
                    nuevoItem.setIdVariante(itemAnonimo.getIdVariante());
                    nuevoItem.setCantidad(itemAnonimo.getCantidad());
                    nuevoItem.setPrecioUnitario(
                        itemAnonimo.getPrecioUnitario()
                    );
                    itemRepository.save(nuevoItem);
                    carritoPrevio.getItems().add(nuevoItem);
                }
            }
        }

        // Recalcular total del carrito previo
        recalcularTotal(carritoPrevio);
        carritoRepository.save(carritoPrevio);

        itemRepository.deleteByCarrito(carritoAnonimo);
        carritoRepository.delete(carritoAnonimo);

        // Devolver carrito final del usuario
        CarritoDTO dto = carritoMapper.toDTO(carritoPrevio);
        enriquecerProductosDesdeAPI(dto);
        return dto;
    }

    @Override
    public CarritoDTO crearCarrito(Carrito carrito) {
        recalcularTotal(carrito);
        Carrito guardado = carritoRepository.save(carrito);
        return carritoMapper.toDTO(guardado);
    }

    @Override
    public CarritoDTO obtenerCarritoPorUsuario(Integer idUsuario) {
        Carrito carrito = carritoRepository
            .findByIdUsuario(idUsuario)
            .orElseThrow(() ->
                new RuntimeException(
                    "Carrito no encontrado para el usuario: " + idUsuario
                )
            );

        CarritoDTO dto = carritoMapper.toDTO(carrito);
        enriquecerProductosDesdeAPI(dto);
        return dto;
    }

    @Override
    public CarritoDTO obtenerCarritoPorId(Integer idCarrito) {
        Carrito carrito = obtenerCarritoEntidadPorId(idCarrito);
        CarritoDTO dto = carritoMapper.toDTO(carrito);
        enriquecerProductosDesdeAPI(dto);
        return dto;
    }

    @Override
    public CarritoDTO agregarItemAlCarrito(
        Integer idUsuario,
        ProductoDTOResponse productoDTO
    ) {
        // Validar que se envíe la variante
        if (productoDTO.getIdVariante() == null) {
            throw new IllegalArgumentException(
                "Debe especificar una variante del producto"
            );
        }

        Carrito carrito = carritoRepository
            .findByIdUsuario(idUsuario)
            .orElseGet(() -> crearNuevoCarritoEntidad(idUsuario));

        if (carrito.getItems() == null) {
            carrito.setItems(new ArrayList<>());
        }

        // Obtener información del producto desde el catálogo
        Map<String, Object> productoInfo = obtenerProductoDesdeAPI(
            productoDTO.getIdProducto()
        );

        // Obtener la variante específica
        Map<String, Object> varianteInfo = obtenerVariantePorId(
            productoInfo,
            productoDTO.getIdVariante()
        );
        Double precioActual = ((Number) varianteInfo.get(
                "precio"
            )).doubleValue();

        // Buscar si ya existe esta variante en el carrito
        Optional<ItemCarrito> itemExistente = carrito
            .getItems()
            .stream()
            .filter(
                item ->
                    item.getProductoId().equals(productoDTO.getIdProducto()) &&
                    item.getIdVariante().equals(productoDTO.getIdVariante())
            )
            .findFirst();

        if (itemExistente.isPresent()) {
            // Incrementar cantidad si ya existe
            ItemCarrito item = itemExistente.get();
            item.setCantidad(item.getCantidad() + productoDTO.getCantidad());
            itemRepository.save(item);
        } else {
            // Crear nuevo item en el carrito
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setProductoId(productoDTO.getIdProducto());
            nuevoItem.setIdVariante(productoDTO.getIdVariante());
            nuevoItem.setCantidad(productoDTO.getCantidad());
            nuevoItem.setPrecioUnitario(precioActual);
            itemRepository.save(nuevoItem);
            carrito.getItems().add(nuevoItem);
        }

        recalcularTotal(carrito);
        Carrito actualizado = carritoRepository.save(carrito);

        CarritoDTO dto = carritoMapper.toDTO(actualizado);
        enriquecerProductosDesdeAPI(dto);
        return dto;
    }

    @Override
    public CarritoDTO actualizarCantidad(
        Integer idUsuario,
        Integer productoId,
        Integer idVariante,
        int nuevaCantidad
    ) {
        if (idVariante == null) {
            throw new IllegalArgumentException(
                "Debe especificar la variante a actualizar"
            );
        }

        Carrito carrito = carritoRepository
            .findByIdUsuario(idUsuario)
            .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        ItemCarrito item = carrito
            .getItems()
            .stream()
            .filter(
                i ->
                    i.getProductoId().equals(productoId) &&
                    i.getIdVariante().equals(idVariante)
            )
            .findFirst()
            .orElseThrow(() ->
                new RuntimeException("Item no encontrado en el carrito")
            );

        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException(
                "La cantidad debe ser mayor a 0"
            );
        }

        item.setCantidad(nuevaCantidad);
        itemRepository.save(item);

        recalcularTotal(carrito);
        Carrito actualizado = carritoRepository.save(carrito);

        CarritoDTO dto = carritoMapper.toDTO(actualizado);
        enriquecerProductosDesdeAPI(dto);
        return dto;
    }

    @Override
    public CarritoDTO eliminarItem(
        Integer idUsuario,
        Integer productoId,
        Integer idVariante
    ) {
        if (idVariante == null) {
            throw new IllegalArgumentException(
                "Debe especificar la variante a eliminar"
            );
        }

        Carrito carrito = carritoRepository
            .findByIdUsuario(idUsuario)
            .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        ItemCarrito itemAEliminar = carrito
            .getItems()
            .stream()
            .filter(
                item ->
                    item.getProductoId().equals(productoId) &&
                    item.getIdVariante().equals(idVariante)
            )
            .findFirst()
            .orElseThrow(() ->
                new RuntimeException("Item no encontrado en el carrito")
            );

        carrito.getItems().remove(itemAEliminar);
        itemRepository.delete(itemAEliminar);

        recalcularTotal(carrito);
        Carrito actualizado = carritoRepository.save(carrito);

        CarritoDTO dto = carritoMapper.toDTO(actualizado);
        enriquecerProductosDesdeAPI(dto);
        return dto;
    }

    @Override
    public void vaciarCarrito(Integer idUsuario) {
        Carrito carrito = carritoRepository
            .findByIdUsuario(idUsuario)
            .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        itemRepository.deleteByCarrito(carrito);
        carrito.getItems().clear();
        carrito.setTotal(0.0);
        carritoRepository.save(carrito);
    }

    // ========== MÉTODOS AUXILIARES ==========

    private Carrito obtenerCarritoEntidadPorId(Integer idCarrito) {
        return carritoRepository
            .findById(idCarrito)
            .orElseThrow(() ->
                new RuntimeException(
                    "Carrito no encontrado con ID: " + idCarrito
                )
            );
    }

    private void recalcularTotal(Carrito carrito) {
        double total = 0.0;
        if (carrito.getItems() != null) {
            total = carrito
                .getItems()
                .stream()
                .mapToDouble(ItemCarrito::getSubtotal)
                .sum();
        }
        carrito.setTotal(total);
    }

    /**
     * Obtiene la información completa de un producto desde el API de catálogo
     */
    private Map<String, Object> obtenerProductoDesdeAPI(Integer idProducto) {
        try {
            String url = catalogoServiceUrl + "/api/productos/" + idProducto;
            ResponseEntity<Map> response = restTemplate.getForEntity(
                url,
                Map.class
            );

            if (response.getBody() == null) {
                throw new RuntimeException(
                    "Producto no encontrado: " + idProducto
                );
            }

            return response.getBody();
        } catch (Exception e) {
            System.err.println(
                "Error al obtener producto " +
                    idProducto +
                    ": " +
                    e.getMessage()
            );
            throw new RuntimeException(
                "No se pudo obtener información del producto " + idProducto,
                e
            );
        }
    }

    /**
     * Enriquece todos los items del carrito con datos actualizados del API
     */
    private void enriquecerProductosDesdeAPI(CarritoDTO dto) {
        if (dto.getItems() == null) return;

        dto
            .getItems()
            .forEach(item -> {
                try {
                    Map<String, Object> productoInfo = obtenerProductoDesdeAPI(
                        item.getIdProducto()
                    );
                    item.setNombre((String) productoInfo.get("nombre"));

                    if (item.getIdVariante() != null) {
                        Map<String, Object> varianteInfo = obtenerVariantePorId(
                            productoInfo,
                            item.getIdVariante()
                        );
                        item.setImagenUrl(extraerImagenVariante(varianteInfo));

                        item.setSku((String) varianteInfo.get("sku"));

                        // Actualizar precio desde el catálogo (por si cambió)
                        Double precioActual = ((Number) varianteInfo.get(
                                "precio"
                            )).doubleValue();
                        item.setPrecio(precioActual);
                    }
                } catch (Exception e) {
                    System.err.println(
                        "No se pudo enriquecer item del carrito: " +
                            e.getMessage()
                    );
                }
            });
    }

    /**
     * Busca una variante específica dentro de las variantes del producto
     */
    private Map<String, Object> obtenerVariantePorId(
        Map<String, Object> productoInfo,
        Integer idVariante
    ) {
        Object variantesObj = productoInfo.get("variantes");
        if (variantesObj instanceof List) {
            List<Map<String, Object>> variantes = (List<
                Map<String, Object>
            >) variantesObj;
            for (Map<String, Object> variante : variantes) {
                Integer varianteId = (Integer) variante.get("id");
                if (varianteId != null && varianteId.equals(idVariante)) {
                    return variante;
                }
            }
        }
        throw new RuntimeException(
            "Variante no encontrada con ID: " + idVariante
        );
    }

    /**
     * Extrae la primera imagen de una variante
     */
    private String extraerImagenVariante(Map<String, Object> varianteInfo) {
        try {
            Object imagenesObj = varianteInfo.get("varianteImagenes");
            if (imagenesObj instanceof List) {
                List<Map<String, Object>> imagenes = (List<
                    Map<String, Object>
                >) imagenesObj;
                if (!imagenes.isEmpty()) {
                    return (String) imagenes.get(0).get("imagen");
                }
            }
        } catch (Exception e) {
            System.err.println(
                "Error extrayendo imagen de variante: " + e.getMessage()
            );
        }
        return null;
    }

    // ========== IMPLEMENTACIONES CON CARRITO ID ==========

    @Override
    public CarritoDTO agregarItemAlCarritoPorId(
        Integer idCarrito,
        ProductoDTOResponse productoDTO
    ) {
        // Validar que se envíe la variante
        if (productoDTO.getIdVariante() == null) {
            throw new IllegalArgumentException(
                "Debe especificar una variante del producto"
            );
        }

        Carrito carrito = carritoRepository
            .findById(idCarrito)
            .orElseThrow(() ->
                new RuntimeException(
                    "Carrito no encontrado con ID: " + idCarrito
                )
            );

        if (carrito.getItems() == null) {
            carrito.setItems(new ArrayList<>());
        }

        // Obtener información del producto y variante desde el catálogo
        Map<String, Object> productoInfo = obtenerProductoDesdeAPI(
            productoDTO.getIdProducto()
        );
        Map<String, Object> varianteInfo = obtenerVariantePorId(
            productoInfo,
            productoDTO.getIdVariante()
        );
        Double precioActual = ((Number) varianteInfo.get(
                "precio"
            )).doubleValue();

        // Buscar si ya existe esta variante en el carrito
        Optional<ItemCarrito> itemExistente = carrito
            .getItems()
            .stream()
            .filter(
                item ->
                    item.getProductoId().equals(productoDTO.getIdProducto()) &&
                    item.getIdVariante().equals(productoDTO.getIdVariante())
            )
            .findFirst();

        if (itemExistente.isPresent()) {
            ItemCarrito item = itemExistente.get();
            item.setCantidad(item.getCantidad() + productoDTO.getCantidad());
            itemRepository.save(item);
        } else {
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setProductoId(productoDTO.getIdProducto());
            nuevoItem.setIdVariante(productoDTO.getIdVariante());
            nuevoItem.setCantidad(productoDTO.getCantidad());
            nuevoItem.setPrecioUnitario(precioActual);
            itemRepository.save(nuevoItem);
            carrito.getItems().add(nuevoItem);
        }

        recalcularTotal(carrito);
        Carrito actualizado = carritoRepository.save(carrito);

        CarritoDTO dto = carritoMapper.toDTO(actualizado);
        enriquecerProductosDesdeAPI(dto);
        return dto;
    }

    @Override
    public CarritoDTO actualizarCantidadPorId(
        Integer idCarrito,
        Integer productoId,
        Integer idVariante,
        int nuevaCantidad
    ) {
        if (idVariante == null) {
            throw new IllegalArgumentException(
                "Debe especificar la variante a actualizar"
            );
        }

        Carrito carrito = carritoRepository
            .findById(idCarrito)
            .orElseThrow(() ->
                new RuntimeException(
                    "Carrito no encontrado con ID: " + idCarrito
                )
            );

        ItemCarrito item = carrito
            .getItems()
            .stream()
            .filter(
                i ->
                    i.getProductoId().equals(productoId) &&
                    i.getIdVariante().equals(idVariante)
            )
            .findFirst()
            .orElseThrow(() ->
                new RuntimeException("Item no encontrado en el carrito")
            );

        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException(
                "La cantidad debe ser mayor a 0"
            );
        }

        item.setCantidad(nuevaCantidad);
        itemRepository.save(item);

        recalcularTotal(carrito);
        Carrito actualizado = carritoRepository.save(carrito);

        CarritoDTO dto = carritoMapper.toDTO(actualizado);
        enriquecerProductosDesdeAPI(dto);
        return dto;
    }

    @Override
    public CarritoDTO eliminarItemPorId(
        Integer idCarrito,
        Integer productoId,
        Integer idVariante
    ) {
        if (idVariante == null) {
            throw new IllegalArgumentException(
                "Debe especificar la variante a eliminar"
            );
        }

        Carrito carrito = carritoRepository
            .findById(idCarrito)
            .orElseThrow(() ->
                new RuntimeException(
                    "Carrito no encontrado con ID: " + idCarrito
                )
            );

        ItemCarrito itemAEliminar = carrito
            .getItems()
            .stream()
            .filter(
                item ->
                    item.getProductoId().equals(productoId) &&
                    item.getIdVariante().equals(idVariante)
            )
            .findFirst()
            .orElseThrow(() ->
                new RuntimeException("Item no encontrado en el carrito")
            );

        carrito.getItems().remove(itemAEliminar);
        itemRepository.delete(itemAEliminar);

        recalcularTotal(carrito);
        Carrito actualizado = carritoRepository.save(carrito);

        CarritoDTO dto = carritoMapper.toDTO(actualizado);
        enriquecerProductosDesdeAPI(dto);
        return dto;
    }

    @Override
    public void vaciarCarritoPorId(Integer idCarrito) {
        Carrito carrito = carritoRepository
            .findById(idCarrito)
            .orElseThrow(() ->
                new RuntimeException(
                    "Carrito no encontrado con ID: " + idCarrito
                )
            );

        itemRepository.deleteByCarrito(carrito);
        carrito.getItems().clear();
        carrito.setTotal(0.0);
        carritoRepository.save(carrito);
    }
}
