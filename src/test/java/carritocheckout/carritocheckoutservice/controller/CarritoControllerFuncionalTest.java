package carritocheckout.carritocheckoutservice.controller;


import carritocheckout.carritocheckoutservice.dtos.CarritoDTO;
import carritocheckout.carritocheckoutservice.dtos.ProductoDTOResponse;
import carritocheckout.carritocheckoutservice.service.CarritoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarritoController.class)
@Import(CarritoControllerFuncionalTest.TestConfig.class)

class CarritoControllerFuncionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarritoService carritoService;

    // ==== CONFIG PARA EVITAR @MockBean DEPRECATED ====
    static class TestConfig {
        @Bean
        public CarritoService carritoService() {
            return Mockito.mock(CarritoService.class);
        }
    }

    // ===============================
    //  FUNCIONALES GESTIÓN DE CARRITOS
    // ===============================

    @Test
    void crearCarrito() throws Exception {
        Mockito.when(carritoService.agregarCarrito(any())).thenReturn(new CarritoDTO());

        mockMvc.perform(post("/api/carritos?idUsuario=1"))
                .andExpect(status().isOk());
    }

    @Test
    void asignarCarritoAUsuario() throws Exception {
        Mockito.when(carritoService.asignarCarritoAUsuario(anyInt(), anyInt()))
                .thenReturn(new CarritoDTO());

        mockMvc.perform(put("/api/carritos/5/asignar-usuario?idUsuario=8"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerCarritoPorUsuario() throws Exception {
        Mockito.when(carritoService.obtenerCarritoPorUsuario(anyInt()))
                .thenReturn(new CarritoDTO());

        mockMvc.perform(get("/api/carritos/usuario/3"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerCarritoPorId() throws Exception {
        Mockito.when(carritoService.obtenerCarritoPorId(anyInt()))
                .thenReturn(new CarritoDTO());

        mockMvc.perform(get("/api/carritos/10"))
                .andExpect(status().isOk());
    }

    // ===============================
    //  FUNCIONALES POR USUARIO
    // ===============================

    @Test
    void agregarItemUsuario() throws Exception {
        Mockito.when(carritoService.agregarItemAlCarrito(anyInt(), any()))
                .thenReturn(new CarritoDTO());

        String body = """
        {
            "idProducto": 1,
            "idVariante": 2,
            "cantidad": 3
        }
        """;

        mockMvc.perform(post("/api/carritos/4/items")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarItemUsuario() throws Exception {
        Mockito.when(carritoService.actualizarCantidad(anyInt(), anyInt(), any(), anyInt()))
                .thenReturn(new CarritoDTO());

        mockMvc.perform(patch("/api/carritos/4/items/1/2?nuevaCantidad=5"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarItemUsuario() throws Exception {
        Mockito.when(carritoService.eliminarItem(anyInt(), anyInt(), any()))
                .thenReturn(new CarritoDTO());

        mockMvc.perform(delete("/api/carritos/4/items/1/2"))
                .andExpect(status().isOk());
    }

    @Test
    void vaciarCarritoUsuario() throws Exception {
        mockMvc.perform(delete("/api/carritos/4/items"))
                .andExpect(status().isNoContent());
    }

    // ===============================
    //  FUNCIONALES ANÓNIMOS POR ID CARRITO
    // ===============================

    @Test
    void agregarItemAnonimo() throws Exception {
        Mockito.when(carritoService.agregarItemAlCarritoPorId(anyInt(), any()))
                .thenReturn(new CarritoDTO());

        String body = """
        {
            "idProducto": 1,
            "idVariante": 2,
            "cantidad": 3
        }
        """;

        mockMvc.perform(post("/api/carritos/9/anonimo/items")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarItemAnonimo() throws Exception {
        Mockito.when(carritoService.actualizarCantidadPorId(anyInt(), anyInt(), any(), anyInt()))
                .thenReturn(new CarritoDTO());

        mockMvc.perform(patch("/api/carritos/9/anonimo/items/1/2?nuevaCantidad=7"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarItemAnonimo() throws Exception {
        Mockito.when(carritoService.eliminarItemPorId(anyInt(), anyInt(), any()))
                .thenReturn(new CarritoDTO());

        mockMvc.perform(delete("/api/carritos/9/anonimo/items/1/2"))
                .andExpect(status().isOk());
    }

    @Test
    void vaciarCarritoAnonimo() throws Exception {
        mockMvc.perform(delete("/api/carritos/9/anonimo/items"))
                .andExpect(status().isNoContent());
    }
}
