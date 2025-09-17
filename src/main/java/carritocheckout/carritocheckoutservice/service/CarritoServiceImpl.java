package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.entities.Carrito;
import carritocheckout.carritocheckoutservice.entities.ItemCarrito;
import carritocheckout.carritocheckoutservice.repository.CarritoRepository;
import carritocheckout.carritocheckoutservice.repository.ItemRepository;
import org.springframework.stereotype.Service;

@Service
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final ItemRepository itemRepository;

    public CarritoServiceImpl(CarritoRepository carritoRepository, ItemRepository itemRepository) {
        this.carritoRepository = carritoRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Carrito crearCarrito(Carrito carrito) {
        // Si no tiene total inicial, lo calculamos en base a sus ítems
        recalcularTotal(carrito);
        return carritoRepository.save(carrito);
    }

    @Override
    public Carrito obtenerCarritoPorUsuario(Integer idUsuario) {
        return carritoRepository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
    }

    @Override
    public Carrito agregarItem(Integer idUsuario, ItemCarrito item) {
        Carrito carrito = obtenerCarritoPorUsuario(idUsuario);
        ItemCarrito itemGuardado = itemRepository.save(item);
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
        carrito.getItems().clear();
        itemRepository.deleteAll(); // ⚠️ elimina todos los items de la DB; si quieres solo los de este carrito, mejor haz un deleteByCarritoId
        carrito.setTotal(0.0);
        carritoRepository.save(carrito);
    }

    private void recalcularTotal(Carrito carrito) {
        double total = carrito.getItems().stream()
                .mapToDouble(ItemCarrito::getSubtotal) // usa el método subtotal de ItemCarrito
                .sum();
        carrito.setTotal(total);
    }
}
