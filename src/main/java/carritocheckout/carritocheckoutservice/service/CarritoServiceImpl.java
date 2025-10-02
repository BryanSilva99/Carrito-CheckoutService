package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.entities.Carrito;
import carritocheckout.carritocheckoutservice.entities.ItemCarrito;
import carritocheckout.carritocheckoutservice.repository.CarritoRepository;
import carritocheckout.carritocheckoutservice.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final ItemRepository itemRepository;

    public CarritoServiceImpl(CarritoRepository carritoRepository, ItemRepository itemRepository) {
        this.carritoRepository = carritoRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Carrito agregarCarrito(Integer idUsuario) {
        if (idUsuario != null) {
            return carritoRepository.findCarritoByIdUsuario(idUsuario)
                    .orElseGet(() -> {
                        Carrito nuevoCarrito = new Carrito();
                        nuevoCarrito.setIdUsuario(idUsuario);
                        nuevoCarrito.setTotal(0.0);
                        return carritoRepository.save(nuevoCarrito);
                    });
        } else {
            Carrito nuevoCarrito = new Carrito();
            nuevoCarrito.setTotal(0.0);
            return carritoRepository.save(nuevoCarrito);
        }
    }

    @Override
    public Carrito asignarCarritoAUsuario(Integer id, Integer idUsuario) {
        Carrito carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        carrito.setIdUsuario(idUsuario);
        return carritoRepository.save(carrito);
    }

    @Override
    public Carrito obtenerCarritoPorId(Integer id) {
        return null;
    }

    @Override
    public void actualizarCarrito(Carrito carrito) {

    }

    @Override
    public Carrito crearCarrito(Carrito carrito) {
        recalcularTotal(carrito);
        return carritoRepository.save(carrito);
    }

    @Override
    public Carrito obtenerCarritoPorUsuario(Integer idUsuario) {
        return carritoRepository.findCarritoByIdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
    }

    @Override
    public Carrito agregarItem(Integer idUsuario, ItemCarrito item) {
        Carrito carrito = obtenerCarritoPorUsuario(idUsuario);
        ItemCarrito itemGuardado = itemRepository.save(item);

        if (carrito.getItems() == null) {
            carrito.setItems(new ArrayList<>());
        }

        carrito.getItems().add(itemGuardado);
        recalcularTotal(carrito);

        return carritoRepository.save(carrito);
    }

    @Override
    public Carrito actualizarCantidad(Integer idUsuario, Integer itemId, int nuevaCantidad) {
        Carrito carrito = obtenerCarritoPorUsuario(idUsuario);
        carrito.getItems().forEach(i -> {
            if (i.getId().equals(itemId)) {
                i.setCantidad(nuevaCantidad);
                itemRepository.save(i);
            }
        });

        recalcularTotal(carrito);
        return carritoRepository.save(carrito);
    }

    @Override
    public void eliminarItem(Integer idUsuario, Integer itemId) {
        Carrito carrito = obtenerCarritoPorUsuario(idUsuario);
        carrito.getItems().removeIf(i -> i.getId().equals(itemId));
        itemRepository.deleteById(itemId);

        recalcularTotal(carrito);
        carritoRepository.save(carrito);
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
        double total = carrito.getItems().stream()
                .mapToDouble(ItemCarrito::getSubtotal)
                .sum();
        carrito.setTotal(total);
    }
}
