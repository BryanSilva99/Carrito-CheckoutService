package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.entities.Carrito;
import carritocheckout.carritocheckoutservice.repository.CarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarritoServiceImpl implements CarritoService{

    @Autowired
    private CarritoRepository carritoRepository;

    @Override
    public Carrito agregarCarrito(Integer idUsuario) {
        if(idUsuario!=null){
            return carritoRepository.findCarritoByIdUsuario(idUsuario)
                    .orElseGet(()->{
                        Carrito nuevoCarrito = new Carrito();
                        nuevoCarrito.setIdUsuario(idUsuario);
                        return carritoRepository.save(nuevoCarrito);
                    });
        }
        else {
            Carrito nuevoCarrito = new Carrito();
            return carritoRepository.save(nuevoCarrito);
        }
    }

    @Override
    public Carrito asignarCarritoAUsuario(Integer id, Integer idUsuario) {
        Carrito carrito = carritoRepository.findById(id).
                orElseThrow(()-> new RuntimeException("Carrito no encontrado"));
        carrito.setIdUsuario(idUsuario);
        return carritoRepository.save(carrito);
    }

    @Override
    public Carrito obtenerCarritoPorId(Integer id) {
        return carritoRepository.findById(id).orElseThrow();
    }

    @Override
    public void actualizarCarrito(Carrito carrito) {
        carritoRepository.save(carrito);
    }

    @Override
    public void borrarCarrito(Integer id) {
        carritoRepository.deleteById(id);
    }
}
