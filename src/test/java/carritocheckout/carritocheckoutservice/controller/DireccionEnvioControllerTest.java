package carritocheckout.carritocheckoutservice.controller;

import carritocheckout.carritocheckoutservice.dtos.UsuarioEnvioDTO;
import carritocheckout.carritocheckoutservice.entities.DireccionEnvio;
import carritocheckout.carritocheckoutservice.entities.UsuarioEnvio;
import carritocheckout.carritocheckoutservice.service.DireccionEnvioService;
import carritocheckout.carritocheckoutservice.service.UsuarioEnvioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DireccionEnvioControllerTest {

    @Mock
    private UsuarioEnvioService usuarioEnvioService;

    @Mock
    private DireccionEnvioService direccionEnvioService;

    @InjectMocks
    private DireccionEnvioController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ===================== TEST: crear usuario envío =====================
    @Test
    void crearUsuarioEnvio_ok() {
        UsuarioEnvio usuario = new UsuarioEnvio();
        when(usuarioEnvioService.crearUsuarioEnvio(usuario)).thenReturn(usuario);

        ResponseEntity<UsuarioEnvio> response = controller.crearUsuarioEnvio(usuario);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(usuario, response.getBody());
        verify(usuarioEnvioService).crearUsuarioEnvio(usuario);
    }

    // ===================== TEST: obtener usuario por idUsuario =====================
    @Test
    void obtenerUsuarioEnvio_ok() {
        UsuarioEnvioDTO dto = new UsuarioEnvioDTO(
                1,
                10,
                "correo@test.com",
                "Juan Pérez",
                "987654321"
        );

        when(usuarioEnvioService.obtenerPorIdUsuario(10)).thenReturn(dto);

        ResponseEntity<UsuarioEnvioDTO> response = controller.obtenerUsuarioEnvio(10);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
        verify(usuarioEnvioService).obtenerPorIdUsuario(10);
    }


    // ===================== TEST: agregar dirección =====================
    @Test
    void agregarDireccion_ok() {
        DireccionEnvio dir = new DireccionEnvio();
        when(usuarioEnvioService.agregarDireccion(1, dir)).thenReturn(dir);

        ResponseEntity<DireccionEnvio> response = controller.agregarDireccion(1, dir);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dir, response.getBody());
        verify(usuarioEnvioService).agregarDireccion(1, dir);
    }

    // ===================== TEST: listar direcciones =====================
    @Test
    void listarDirecciones_ok() {
        List<DireccionEnvio> lista = List.of(new DireccionEnvio());
        when(direccionEnvioService.obtenerPorUsuario(1)).thenReturn(lista);

        ResponseEntity<List<DireccionEnvio>> response = controller.listarDirecciones(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(lista, response.getBody());
        verify(direccionEnvioService).obtenerPorUsuario(1);
    }

    // ===================== TEST: actualizar dirección =====================
    @Test
    void actualizarDireccion_ok() {
        DireccionEnvio direccion = new DireccionEnvio();
        when(direccionEnvioService.actualizarDireccion(10, direccion)).thenReturn(direccion);

        ResponseEntity<DireccionEnvio> response = controller.actualizarDireccion(10, direccion);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(direccion, response.getBody());
        verify(direccionEnvioService).actualizarDireccion(10, direccion);
    }

    // ===================== TEST: eliminar dirección =====================
    @Test
    void eliminarDireccion_ok() {
        ResponseEntity<Void> response = controller.eliminarDireccion(5);

        assertEquals(204, response.getStatusCodeValue());
        verify(direccionEnvioService).eliminarDireccion(5);
    }

    // ===================== TEST: marcar dirección como principal =====================
    @Test
    void marcarPrincipal_ok() {
        ResponseEntity<Void> response = controller.marcarPrincipal(1, 44);

        assertEquals(204, response.getStatusCodeValue());
        verify(direccionEnvioService).marcarComoPrincipal(1, 44);
    }
}
