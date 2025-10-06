package carritocheckout.carritocheckoutservice.service;
import carritocheckout.carritocheckoutservice.dtos.ProductoDTOResponse;
import carritocheckout.carritocheckoutservice.entities.Carrito;
import carritocheckout.carritocheckoutservice.entities.ItemCarrito;
import carritocheckout.carritocheckoutservice.repository.CarritoRepository;
import carritocheckout.carritocheckoutservice.repository.ItemRepository;
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

    private Carrito crearNuevoCarrito(Integer idUsuario){
        Carrito nuevoCarrito = new Carrito();
        nuevoCarrito.setIdUsuario(idUsuario);
        nuevoCarrito.setTotal(0.0);
        return carritoRepository.save(nuevoCarrito);
    }


    @Override
    public Carrito agregarCarrito(Integer idUsuario) {
        if (idUsuario != null) {
            return carritoRepository.findCarritoByIdUsuario(idUsuario)
                    .orElseGet(() -> {
                        return crearNuevoCarrito(idUsuario);
                    });
        } else {
            return crearNuevoCarrito(null);
        }
    }

    @Override
    public Carrito asignarCarritoAUsuario(Integer carritoId, Integer idUsuario) {
        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
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
        return carritoRepository.findCarritoByIdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
    }

    @Override
    public void agregarItemAlCarrito(ProductoDTOResponse productoDTO) {
        Carrito existente = agregarCarrito(productoDTO.getIdUsuario());
        ItemCarrito itemExistente = itemRepository.findByCarritoAndProductoId(existente,productoDTO.getId())
                .orElse(null);
        if(itemExistente != null){
            itemExistente.setCantidad(itemExistente.getCantidad()+ productoDTO.getCantidad());
            itemRepository.save(itemExistente);
        }else {
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setCarrito(existente);
            nuevoItem.setProductoId(productoDTO.getId());
            nuevoItem.setCantidad(productoDTO.getCantidad());
            nuevoItem.setPrecioUnitario(productoDTO.getPrecio());
            itemRepository.save(nuevoItem);
        }
        recalcularTotal(existente);
        carritoRepository.save(existente);
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
