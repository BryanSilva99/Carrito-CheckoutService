package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.dtos.ProductoDTOResponse;
import carritocheckout.carritocheckoutservice.entities.Carrito;
import carritocheckout.carritocheckoutservice.entities.ItemCarrito;
import carritocheckout.carritocheckoutservice.repository.CarritoRepository;
import carritocheckout.carritocheckoutservice.repository.ItemRepository;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final ItemRepository itemRepository;

    public CarritoServiceImpl(CarritoRepository carritoRepository, ItemRepository itemRepository) {
        this.carritoRepository = carritoRepository;
        this.itemRepository = itemRepository;
    }

    private Carrito crearNuevoCarrito(Integer idUsuario) {
        Carrito nuevoCarrito = new Carrito();
        nuevoCarrito.setIdUsuario(idUsuario);
        nuevoCarrito.setTotal(0.0);
        return carritoRepository.save(nuevoCarrito);
    }

    @Override
    public Carrito agregarCarrito(Integer idUsuario) {
        if (idUsuario != null) {
            return carritoRepository.findByIdUsuario(idUsuario)
                    .orElseGet(() -> crearNuevoCarrito(idUsuario));
        } else {
            return crearNuevoCarrito(null);
        }
    }

    @Override
    public Carrito asignarCarritoAUsuario(Integer idCarrito, Integer idUsuario) {
        Carrito carrito = obtenerCarritoPorId(idCarrito);
        carrito.setIdUsuario(idUsuario);
        return carritoRepository.save(carrito);
    }

    @Override
    public Carrito crearCarrito(Carrito carrito) {
        recalcularTotal(carrito);
        return carritoRepository.save(carrito);
    }

    @Override
    public Carrito obtenerCarritoPorUsuario(Integer idUsuario) {
        return carritoRepository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para el usuario: " + idUsuario));
    }

    @Override
    public Carrito obtenerCarritoPorId(Integer idCarrito) {
        return carritoRepository.findById(idCarrito)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado con ID: " + idCarrito));
    }

    @Override
    public Carrito agregarItemAlCarrito(Integer idUsuario, ProductoDTOResponse productoDTO) {
        Carrito carrito = agregarCarrito(idUsuario);

        if (carrito.getItems() == null) {
            carrito.setItems(new java.util.ArrayList<>());
        }

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
            nuevo.setPrecioUnitario(productoDTO.getPrecio());
            ItemCarrito saved = itemRepository.save(nuevo);
            carrito.getItems().add(saved);
        }

        recalcularTotal(carrito);
        return carritoRepository.save(carrito);
    }

    @Override
    public Carrito actualizarCantidad(Integer idUsuario, Integer itemId, int nuevaCantidad) {
        Carrito carrito = obtenerCarritoPorUsuario(idUsuario);

        ItemCarrito item = itemRepository.findByCarritoAndProductoId(carrito, itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado: " + itemId));

        if (item.getCarrito() == null || !item.getCarrito().getId().equals(carrito.getId())) {
            throw new RuntimeException("El item no pertenece al carrito del usuario");
        }

        item.setCantidad(nuevaCantidad);

        recalcularTotal(carrito);
        return carritoRepository.save(carrito);
    }

    @Override
    public Carrito eliminarItem(Integer idUsuario, Integer itemId) {
        Carrito carrito = obtenerCarritoPorUsuario(idUsuario);

        carrito.getItems().removeIf(item -> item.getProductoId().equals(itemId));

        recalcularTotal(carrito);
        return carritoRepository.save(carrito);
    }

    @Override
    public void vaciarCarrito(Integer idUsuario) {
        Carrito carrito = obtenerCarritoPorUsuario(idUsuario);

        itemRepository.deleteByCarrito(carrito);

        carrito.getItems().clear();
        carrito.setTotal(0.0);
        carritoRepository.save(carrito);
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
