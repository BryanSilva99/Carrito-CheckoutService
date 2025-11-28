package carritocheckout.carritocheckoutservice.controller;

import carritocheckout.carritocheckoutservice.dtos.CarritoDTO;
import carritocheckout.carritocheckoutservice.dtos.ProductoDTOResponse;
import carritocheckout.carritocheckoutservice.service.CarritoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CarritoControllerTest {

    @Mock
    private CarritoService carritoService;

    @InjectMocks
    private CarritoController carritoController;

    private CarritoDTO carrito;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        carrito = new CarritoDTO();
    }

    // =================== GESTIÓN DE CARRITO ====================

    @Test
    void crearCarrito_ok() {
        when(carritoService.agregarCarrito(1)).thenReturn(carrito);

        ResponseEntity<CarritoDTO> resp = carritoController.crearCarrito(1);

        assertEquals(carrito, resp.getBody());
    }

    @Test
    void asignarCarritoAUsuario_ok() {
        when(carritoService.asignarCarritoAUsuario(10, 5)).thenReturn(carrito);

        ResponseEntity<CarritoDTO> resp = carritoController.asignarCarritoAUsuario(10, 5);

        assertEquals(carrito, resp.getBody());
    }

    @Test
    void obtenerCarritoPorUsuario_ok() {
        when(carritoService.obtenerCarritoPorUsuario(3)).thenReturn(carrito);

        ResponseEntity<CarritoDTO> resp = carritoController.obtenerCarritoPorUsuario(3);

        assertEquals(carrito, resp.getBody());
    }

    @Test
    void obtenerCarritoPorId_ok() {
        when(carritoService.obtenerCarritoPorId(8)).thenReturn(carrito);

        ResponseEntity<CarritoDTO> resp = carritoController.obtenerCarritoPorId(8);

        assertEquals(carrito, resp.getBody());
    }

    // ================= OPERACIONES POR USUARIO ==================

    @Test
    void agregarItem_ok() {
        ProductoDTOResponse producto = new ProductoDTOResponse();
        when(carritoService.agregarItemAlCarrito(4, producto)).thenReturn(carrito);

        ResponseEntity<CarritoDTO> resp = carritoController.agregarItem(4, producto);

        assertEquals(carrito, resp.getBody());
    }

    @Test
    void actualizarItem_ok() {
        when(carritoService.actualizarCantidad(2, 20, 3, 6))
                .thenReturn(carrito);

        ResponseEntity<CarritoDTO> resp =
                carritoController.actualizarItem(2, 20, 3, 6);

        assertEquals(carrito, resp.getBody());
    }

    @Test
    void eliminarItem_ok() {
        when(carritoService.eliminarItem(1, 9, 2))
                .thenReturn(carrito);

        ResponseEntity<CarritoDTO> resp =
                carritoController.eliminarItem(1, 9, 2);

        assertEquals(carrito, resp.getBody());
    }

    @Test
    void vaciarCarrito_ok() {
        doNothing().when(carritoService).vaciarCarrito(2);

        ResponseEntity<Void> resp = carritoController.vaciarCarrito(2);

        assertEquals(204, resp.getStatusCode().value());
    }

    // ========== OPERACIONES ANÓNIMOS (POR ID CARRITO) ==========

    @Test
    void agregarItemPorId_ok() {
        ProductoDTOResponse producto = new ProductoDTOResponse();
        when(carritoService.agregarItemAlCarritoPorId(7, producto))
                .thenReturn(carrito);

        ResponseEntity<CarritoDTO> resp =
                carritoController.agregarItemPorId(7, producto);

        assertEquals(carrito, resp.getBody());
    }

    @Test
    void actualizarItemPorId_ok() {
        when(carritoService.actualizarCantidadPorId(9, 3, 10, 4))
                .thenReturn(carrito);

        ResponseEntity<CarritoDTO> resp =
                carritoController.actualizarItemPorId(9, 3, 10, 4);

        assertEquals(carrito, resp.getBody());
    }

    @Test
    void eliminarItemPorId_ok() {
        when(carritoService.eliminarItemPorId(4, 5, 2))
                .thenReturn(carrito);

        ResponseEntity<CarritoDTO> resp =
                carritoController.eliminarItemPorId(4, 5, 2);

        assertEquals(carrito, resp.getBody());
    }

    @Test
    void vaciarCarritoPorId_ok() {
        doNothing().when(carritoService).vaciarCarritoPorId(11);

        ResponseEntity<Void> resp = carritoController.vaciarCarritoPorId(11);

        assertEquals(204, resp.getStatusCode().value());
    }
}