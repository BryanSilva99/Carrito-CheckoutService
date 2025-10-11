package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.dtos.ProductoDTOResponse;
import carritocheckout.carritocheckoutservice.entities.Carrito;

public interface CarritoService {

    Carrito agregarCarrito(Integer idUsuario);
    Carrito asignarCarritoAUsuario(Integer idCarrito, Integer idUsuario);
    Carrito crearCarrito(Carrito carrito);
    Carrito obtenerCarritoPorUsuario(Integer idUsuario);
    Carrito obtenerCarritoPorId(Integer idCarrito);

    Carrito agregarItemAlCarrito(Integer idUsuario, ProductoDTOResponse productoDTO);
    Carrito actualizarCantidad(Integer idUsuario, Integer itemId, int nuevaCantidad);
    Carrito eliminarItem(Integer idUsuario, Integer itemId);
    void vaciarCarrito(Integer idUsuario);
}
