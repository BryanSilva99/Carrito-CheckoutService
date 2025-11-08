package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.dtos.CarritoDTO;
import carritocheckout.carritocheckoutservice.dtos.ProductoDTOResponse;
import carritocheckout.carritocheckoutservice.entities.Carrito;

public interface CarritoService {

    CarritoDTO agregarCarrito(Integer idUsuario);

    CarritoDTO asignarCarritoAUsuario(Integer idCarrito, Integer idUsuario);

    CarritoDTO crearCarrito(Carrito carrito);

    CarritoDTO obtenerCarritoPorUsuario(Integer idUsuario);
    CarritoDTO obtenerCarritoPorId(Integer idCarrito);

    CarritoDTO agregarItemAlCarrito(Integer idUsuario, ProductoDTOResponse productoDTO);
    CarritoDTO agregarItemAlCarritoPorId(Integer idUsuario, ProductoDTOResponse productoDTO);

    CarritoDTO actualizarCantidad(Integer idUsuario, Integer productoId, Integer idVariante, int nuevaCantidad);
    CarritoDTO actualizarCantidadPorId(Integer idCarrito, Integer productoId, Integer idVariante, int nuevaCantidad);
    
    CarritoDTO eliminarItem(Integer idUsuario, Integer productoId, Integer idVariante);
    CarritoDTO eliminarItemPorId(Integer idCarrito, Integer productoId, Integer idVariante);

    void vaciarCarrito(Integer idUsuario);
    void vaciarCarritoPorId(Integer idCarrito);
}
