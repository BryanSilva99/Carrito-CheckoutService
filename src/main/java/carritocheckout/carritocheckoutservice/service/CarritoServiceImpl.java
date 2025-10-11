package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.dtos.CarritoDTO;
import carritocheckout.carritocheckoutservice.dtos.ProductoDTOResponse;
import carritocheckout.carritocheckoutservice.entities.Carrito;
import carritocheckout.carritocheckoutservice.entities.ItemCarrito;
import carritocheckout.carritocheckoutservice.mapper.CarritoMapper;
import carritocheckout.carritocheckoutservice.repository.CarritoRepository;
import carritocheckout.carritocheckoutservice.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Map;


import java.util.Optional;

@Service
@Transactional
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private RestTemplate restTemplate;


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

        // Convertimos la entidad a DTO
        CarritoDTO dto = carritoMapper.toDTO(carrito);

        // 🔹 Completar datos de cada producto desde el mock
        dto.getItems().forEach(p -> {
            try {
                String mockUrl = "http://localhost:8080/api/mock/catalogo/" + p.getIdProducto();
                ResponseEntity<Map> response = restTemplate.getForEntity(mockUrl, Map.class);
                Map<String, Object> data = response.getBody();

                if (data != null) {
                    p.setNombre((String) data.get("nombre"));
                    p.setImagenUrl((String) data.get("imagenUrl"));
                    // Convertir el precio a Double (puede venir como Integer o Double)
                    Object precio = data.get("precio");
                    p.setPrecio(precio instanceof Integer ? ((Integer) precio).doubleValue() : (Double) precio);
                }
            } catch (Exception e) {
                System.out.println("⚠️ No se pudo obtener info del producto " + p.getIdProducto() + ": " + e.getMessage());
            }
        });

        return dto;
    }


    @Override
    public CarritoDTO obtenerCarritoPorId(Integer idCarrito) {
        Carrito carrito = obtenerCarritoEntidadPorId(idCarrito);
        return carritoMapper.toDTO(carrito);
    }

    @Override
    public CarritoDTO agregarItemAlCarrito(Integer idUsuario, ProductoDTOResponse productoDTO) {
        Carrito carrito = carritoRepository.findByIdUsuario(idUsuario)
                .orElseGet(() -> crearNuevoCarritoEntidad(idUsuario));

        if (carrito.getItems() == null) {
            carrito.setItems(new ArrayList<>());
        }

        // 🔹 Consultar el mock ANTES de crear el item
        Double precioMock = 0.0;
        try {
            String mockUrl = "http://localhost:8080/api/mock/catalogo/" + productoDTO.getIdProducto();
            ResponseEntity<Map> response = restTemplate.getForEntity(mockUrl, Map.class);
            Map<String, Object> data = response.getBody();

            if (data != null) {
                productoDTO.setNombre((String) data.get("nombre"));
                productoDTO.setImagenUrl((String) data.get("imagenUrl"));
                Object precioObj = data.get("precio");
                if (precioObj instanceof Double) {
                    precioMock = (Double) precioObj;
                } else if (precioObj instanceof Integer) {
                    precioMock = ((Integer) precioObj).doubleValue();
                }
                productoDTO.setPrecio(precioMock);
            }
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo obtener info del producto: " + e.getMessage());
        }

        // 🔹 Buscar si el producto ya está en el carrito
        Optional<ItemCarrito> maybe = itemRepository.findByCarritoAndProductoId(carrito, productoDTO.getIdProducto());

        if (maybe.isPresent()) {
            ItemCarrito itemExistente = maybe.get();
            itemExistente.setCantidad(itemExistente.getCantidad() + productoDTO.getCantidad());
            itemRepository.save(itemExistente);
        } else {
            ItemCarrito nuevo = new ItemCarrito();
            nuevo.setCarrito(carrito);
            nuevo.setProductoId(productoDTO.getIdProducto());
            nuevo.setCantidad(productoDTO.getCantidad());
            nuevo.setPrecioUnitario(precioMock); // 👈 AQUÍ lo seteas siempre
            itemRepository.save(nuevo);
            carrito.getItems().add(nuevo);
        }

        recalcularTotal(carrito);
        Carrito actualizado = carritoRepository.save(carrito);

        // 🔹 Enriquecer los datos para la respuesta
        CarritoDTO dto = carritoMapper.toDTO(actualizado);
        dto.getItems().forEach(p -> {
            try {
                String mockUrl = "http://localhost:8080/api/mock/catalogo/" + p.getIdProducto();
                ResponseEntity<Map> response = restTemplate.getForEntity(mockUrl, Map.class);
                Map<String, Object> data = response.getBody();

                if (data != null) {
                    p.setNombre((String) data.get("nombre"));
                    p.setImagenUrl((String) data.get("imagenUrl"));
                    Object precioObj = data.get("precio");
                    if (precioObj instanceof Double) {
                        p.setPrecio((Double) precioObj);
                    } else if (precioObj instanceof Integer) {
                        p.setPrecio(((Integer) precioObj).doubleValue());
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠️ No se pudo obtener info del producto: " + e.getMessage());
            }
        });

        return dto;
    }





    @Override
    public CarritoDTO actualizarCantidad(Integer idUsuario, Integer itemId, int nuevaCantidad) {
        Carrito carrito = carritoRepository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        ItemCarrito item = itemRepository.findByCarritoAndProductoId(carrito, itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado: " + itemId));

        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new RuntimeException("El item no pertenece al carrito del usuario");
        }

        item.setCantidad(nuevaCantidad);
        itemRepository.save(item);

        recalcularTotal(carrito);
        Carrito actualizado = carritoRepository.save(carrito);
        return carritoMapper.toDTO(actualizado);
    }

    @Override
    public CarritoDTO eliminarItem(Integer idUsuario, Integer itemId) {
        Carrito carrito = carritoRepository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        carrito.getItems().removeIf(item -> item.getProductoId().equals(itemId));
        recalcularTotal(carrito);
        Carrito actualizado = carritoRepository.save(carrito);

        return carritoMapper.toDTO(actualizado);
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
}
