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
    public void agregarCarrito(Carrito carrito) {
        carritoRepository.save(carrito);
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
