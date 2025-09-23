package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.entities.Carrito;

public interface CarritoService {

    Carrito agregarCarrito(Integer idUsuario);
    Carrito asignarCarritoAUsuario(Integer id,Integer idUsuario);
    Carrito obtenerCarritoPorId(Integer id);
    void actualizarCarrito(Carrito carrito);
    void borrarCarrito(Integer id);
}
