package carritocheckout.carritocheckoutservice.controller;

import carritocheckout.carritocheckoutservice.dtos.UsuarioEnvioDTO;
import carritocheckout.carritocheckoutservice.entities.DireccionEnvio;
import carritocheckout.carritocheckoutservice.entities.UsuarioEnvio;
import carritocheckout.carritocheckoutservice.service.DireccionEnvioService;
import carritocheckout.carritocheckoutservice.service.UsuarioEnvioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/envio")
public class DireccionEnvioController {

    private final UsuarioEnvioService usuarioEnvioService;
    private final DireccionEnvioService direccionEnvioService;

    public DireccionEnvioController(UsuarioEnvioService usuarioEnvioService, DireccionEnvioService direccionEnvioService) {
        this.usuarioEnvioService = usuarioEnvioService;
        this.direccionEnvioService = direccionEnvioService;
    }

    // Crear o registrar usuario de envío
    @PostMapping("/usuario")
    public ResponseEntity<UsuarioEnvio> crearUsuarioEnvio(@RequestBody UsuarioEnvio usuarioEnvio) {
        return ResponseEntity.ok(usuarioEnvioService.crearUsuarioEnvio(usuarioEnvio));
    }

    // Obtener usuario de envío por idUsuario
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<UsuarioEnvioDTO> obtenerUsuarioEnvio(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(usuarioEnvioService.obtenerPorIdUsuario(idUsuario));
    }

    // Agregar dirección al usuario
    @PostMapping("/usuario/{idUsuario}/direcciones")
    public ResponseEntity<DireccionEnvio> agregarDireccion(@PathVariable Integer idUsuario, @RequestBody DireccionEnvio direccion) {
        return ResponseEntity.ok(usuarioEnvioService.agregarDireccion(idUsuario, direccion));
    }

    // Listar direcciones
    @GetMapping("/usuario/{idUsuario}/direcciones")
    public ResponseEntity<List<DireccionEnvio>> listarDirecciones(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(direccionEnvioService.obtenerPorUsuario(idUsuario));
    }

    // Actualizar una dirección
    @PutMapping("/direcciones/{idDireccion}")
    public ResponseEntity<DireccionEnvio> actualizarDireccion(@PathVariable Integer idDireccion, @RequestBody DireccionEnvio direccion) {
        return ResponseEntity.ok(direccionEnvioService.actualizarDireccion(idDireccion, direccion));
    }

    // Eliminar dirección
    @DeleteMapping("/direcciones/{idDireccion}")
    public ResponseEntity<Void> eliminarDireccion(@PathVariable Integer idDireccion) {
        direccionEnvioService.eliminarDireccion(idDireccion);
        return ResponseEntity.noContent().build();
    }

    // Establecer dirección principal
    @PatchMapping("/usuario/{idUsuario}/direcciones/{idDireccion}/principal")
    public ResponseEntity<Void> marcarPrincipal(@PathVariable Integer idUsuario, @PathVariable Integer idDireccion) {
        direccionEnvioService.marcarComoPrincipal(idUsuario, idDireccion);
        return ResponseEntity.noContent().build();
    }
}
