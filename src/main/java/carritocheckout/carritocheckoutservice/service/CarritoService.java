package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.dtos.ProductoDTOResponse;
import carritocheckout.carritocheckoutservice.entities.Carrito;

public interface CarritoService {

    Carrito agregarCarrito(Integer idUsuario);
    Carrito asignarCarritoAUsuario(Integer id,Integer idUsuario);
    Carrito crearCarrito(Carrito carrito);
    Carrito obtenerCarritoPorUsuario(Integer idUsuario);
    void agregarItemAlCarrito(ProductoDTOResponse productoDTO); //endpoint expuesto para catalogo
    Carrito actualizarCantidad(Integer idUsuario, Integer itemId, int nuevaCantidad);
    void eliminarItem(Integer idUsuario, Integer itemId);
    void vaciarCarrito(Integer idUsuario);
}
