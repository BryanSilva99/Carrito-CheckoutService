package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.dtos.CarritoDTO;
import carritocheckout.carritocheckoutservice.dtos.ProductoDTOResponse;
import carritocheckout.carritocheckoutservice.entities.Carrito;
import carritocheckout.carritocheckoutservice.entities.ItemCarrito;
import carritocheckout.carritocheckoutservice.mapper.CarritoMapper;
import carritocheckout.carritocheckoutservice.repository.CarritoRepository;
import carritocheckout.carritocheckoutservice.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${catalogo.service.url:https://catalogo-service-dcc3a7dgbja8b6dd.canadacentral-01.azurewebsites.net}")
    private String catalogoServiceUrl;

    private final CarritoRepository carritoRepository;
    private final ItemRepository itemRepository;
    private final CarritoMapper carritoMapper;

    public CarritoServiceImpl(CarritoRepository carritoRepository, ItemRepository itemRepository, CarritoMapper carritoMapper) {
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
                ? carritoRepository.findByIdUsuario(idUsuario).orElseGet(() -> crearNuevoCarritoEntidad(idUsuario))
                : crearNuevoCarritoEntidad(null);

        return carritoMapper.toDTO(carrito);
    }

    @Override
    public CarritoDTO asignarCarritoAUsuario(Integer idCarrito, Integer idUsuario) {
        Carrito carrito = obtenerCarritoEntidadPorId(idCarrito);
        carrito.setIdUsuario(idUsuario);
        Carrito actualizado = carritoRepository.save(carrito);
        return carritoMapper.toDTO(actualizado);
    }

    @Override
    public CarritoDTO crearCarrito(Carrito carrito) {
        recalcularTotal(carrito);
        Carrito guardado = carritoRepository.save(carrito);
        return carritoMapper.toDTO(guardado);
    }

    @Override
    public CarritoDTO obtenerCarritoPorUsuario(Integer idUsuario) {
        Carrito carrito = carritoRepository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para el usuario: " + idUsuario));

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
    public CarritoDTO agregarItemAlCarrito(Integer idUsuario, ProductoDTOResponse productoDTO) {
        Carrito carrito = carritoRepository.findByIdUsuario(idUsuario)
                .orElseGet(() -> crearNuevoCarritoEntidad(idUsuario));

        if (carrito.getItems() == null) {
            carrito.setItems(new ArrayList<>());
        }

        // Obtener información del producto desde el servicio de catálogo
        Map<String, Object> productoInfo = obtenerProductoDesdeAPI(productoDTO.getIdProducto());
        Double precioActual = extraerPrecioDesdeVariantes(productoInfo);
        
        System.out.println("💰 Precio obtenido para producto " + productoDTO.getIdProducto() + ": " + precioActual);

        // Buscar si el producto ya está en el carrito
        Optional<ItemCarrito> itemExistente = carrito.getItems().stream()
                .filter(item -> item.getProductoId().equals(productoDTO.getIdProducto()))
                .findFirst();

        if (itemExistente.isPresent()) {
            ItemCarrito item = itemExistente.get();
            item.setCantidad(item.getCantidad() + productoDTO.getCantidad());
            itemRepository.save(item);
            System.out.println("📦 Cantidad actualizada para producto " + productoDTO.getIdProducto());
        } else {
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setProductoId(productoDTO.getIdProducto());
            nuevoItem.setCantidad(productoDTO.getCantidad());
            nuevoItem.setPrecioUnitario(precioActual);
            itemRepository.save(nuevoItem);
            carrito.getItems().add(nuevoItem);
            System.out.println("✅ Nuevo producto agregado: " + productoDTO.getIdProducto());
        }

        recalcularTotal(carrito);
        Carrito actualizado = carritoRepository.save(carrito);

        CarritoDTO dto = carritoMapper.toDTO(actualizado);
        enriquecerProductosDesdeAPI(dto);
        return dto;
    }

    @Override
    public CarritoDTO actualizarCantidad(Integer idUsuario, Integer productoId, int nuevaCantidad) {
        Carrito carrito = carritoRepository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        ItemCarrito item = carrito.getItems().stream()
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item no encontrado: " + productoId));

        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
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
    public CarritoDTO eliminarItem(Integer idUsuario, Integer productoId) {
        Carrito carrito = carritoRepository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        ItemCarrito itemAEliminar = carrito.getItems().stream()
                .filter(item -> item.getProductoId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item no encontrado: " + productoId));

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
        Carrito carrito = carritoRepository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        itemRepository.deleteByCarrito(carrito);
        carrito.getItems().clear();
        carrito.setTotal(0.0);
        carritoRepository.save(carrito);
    }

    // ========== MÉTODOS AUXILIARES ==========

    private Carrito obtenerCarritoEntidadPorId(Integer idCarrito) {
        return carritoRepository.findById(idCarrito)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado con ID: " + idCarrito));
    }

    private void recalcularTotal(Carrito carrito) {
        double total = 0.0;
        if (carrito.getItems() != null) {
            total = carrito.getItems().stream()
                    .mapToDouble(ItemCarrito::getSubtotal)
                    .sum();
        }
        carrito.setTotal(total);
    }

    /**
     * Obtiene la información de un producto desde el API de catálogo
     */
    private Map<String, Object> obtenerProductoDesdeAPI(Integer idProducto) {
        try {
            String url = catalogoServiceUrl + "/api/productos/" + idProducto;
            System.out.println("🔍 Consultando: " + url);
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getBody() == null) {
                throw new RuntimeException("Producto no encontrado: " + idProducto);
            }
            
            System.out.println("✅ Producto obtenido: " + response.getBody().get("nombre"));
            return response.getBody();
        } catch (Exception e) {
            System.err.println("❌ Error al obtener producto " + idProducto + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("No se pudo obtener información del producto " + idProducto, e);
        }
    }

    /**
     * Enriquece todos los productos del carrito con datos del API
     */
    private void enriquecerProductosDesdeAPI(CarritoDTO dto) {
        if (dto.getItems() == null) return;

        dto.getItems().forEach(item -> {
            try {
                Map<String, Object> productoInfo = obtenerProductoDesdeAPI(item.getIdProducto());
                
                // Extraer datos básicos del producto
                item.setNombre((String) productoInfo.get("nombre"));
                
                // Obtener imagen principal desde productoImagenes
                String imagenUrl = extraerImagenPrincipal(productoInfo);
                item.setImagenUrl(imagenUrl);
                
                // Obtener precio desde la primera variante
                Double precio = extraerPrecioDesdeVariantes(productoInfo);
                item.setPrecio(precio);
                
            } catch (Exception e) {
                System.err.println("⚠️ No se pudo enriquecer producto " + item.getIdProducto() + ": " + e.getMessage());
                // Mantener los datos que ya tiene el item
            }
        });
    }

    /**
     * Extrae la imagen principal del producto desde productoImagenes
     */
    private String extraerImagenPrincipal(Map<String, Object> productoInfo) {
        try {
            Object imagenesObj = productoInfo.get("productoImagenes");
            if (imagenesObj instanceof List) {
                List<Map<String, Object>> imagenes = (List<Map<String, Object>>) imagenesObj;
                
                // Buscar la imagen marcada como principal
                for (Map<String, Object> img : imagenes) {
                    Boolean esPrincipal = (Boolean) img.get("principal");
                    if (esPrincipal != null && esPrincipal) {
                        return (String) img.get("imagen");
                    }
                }
                
                // Si no hay imagen principal, tomar la primera
                if (!imagenes.isEmpty()) {
                    return (String) imagenes.get(0).get("imagen");
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error extrayendo imagen: " + e.getMessage());
        }
        return null;
    }

    /**
     * Extrae el precio desde la primera variante disponible
     */
    private Double extraerPrecioDesdeVariantes(Map<String, Object> productoInfo) {
        try {
            Object variantesObj = productoInfo.get("variantes");
            if (variantesObj instanceof List) {
                List<Map<String, Object>> variantes = (List<Map<String, Object>>) variantesObj;
                
                if (!variantes.isEmpty()) {
                    Map<String, Object> primeraVariante = variantes.get(0);
                    Object precioObj = primeraVariante.get("precio");
                    
                    if (precioObj instanceof Double) {
                        return (Double) precioObj;
                    } else if (precioObj instanceof Integer) {
                        return ((Integer) precioObj).doubleValue();
                    } else if (precioObj instanceof Number) {
                        return ((Number) precioObj).doubleValue();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error extrayendo precio: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Extrae el precio del objeto Map, manejando Integer o Double
     * @deprecated Usar extraerPrecioDesdeVariantes para productos con variantes
     */
    private Double extraerPrecio(Map<String, Object> productoInfo) {
        // Primero intentar obtener desde variantes
        Double precioVariante = extraerPrecioDesdeVariantes(productoInfo);
        if (precioVariante != null && precioVariante > 0) {
            return precioVariante;
        }
        
        // Fallback: buscar precio directo (por compatibilidad)
        Object precio = productoInfo.get("precio");
        if (precio instanceof Double) {
            return (Double) precio;
        } else if (precio instanceof Integer) {
            return ((Integer) precio).doubleValue();
        } else if (precio instanceof Number) {
            return ((Number) precio).doubleValue();
        }
        return 0.0;
    }
}