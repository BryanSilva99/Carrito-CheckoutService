package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.entities.Carrito;
import carritocheckout.carritocheckoutservice.entities.ItemCarrito;

public interface CarritoService {
    Carrito crearCarrito(Carrito carrito);
    Carrito obtenerCarritoPorUsuario(Integer idUsuario);
    Carrito agregarItem(Integer idUsuario, ItemCarrito item);
    Carrito actualizarCantidad(Integer idUsuario, Integer itemId, int nuevaCantidad);
    void eliminarItem(Integer idUsuario, Integer itemId);
    void vaciarCarrito(Integer idUsuario);
}
