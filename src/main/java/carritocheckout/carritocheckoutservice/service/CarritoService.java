package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.entities.Carrito;

public interface CarritoService {
    void agregarCarrito(Carrito carrito);
    Carrito obtenerCarritoPorId(Integer id);
    void actualizarCarrito(Carrito carrito);
    void borrarCarrito(Integer id);
}
