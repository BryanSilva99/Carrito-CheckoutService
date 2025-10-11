package carritocheckout.carritocheckoutservice.controller;

import carritocheckout.carritocheckoutservice.dtos.ProductoDTOResponse;
import carritocheckout.carritocheckoutservice.entities.Carrito;
import carritocheckout.carritocheckoutservice.service.CarritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @PostMapping
    public ResponseEntity<Carrito> crearCarrito(@RequestParam(required = false) Integer idUsuario) {
        Carrito carrito = carritoService.agregarCarrito(idUsuario);
        return ResponseEntity.ok(carrito);
    }

    @PutMapping("/{idCarrito}/asignar-usuario")
    public ResponseEntity<Carrito> asignarCarritoAUsuario(
            @PathVariable Integer idCarrito,
            @RequestParam Integer idUsuario) {

        Carrito carritoActualizado = carritoService.asignarCarritoAUsuario(idCarrito, idUsuario);
        return ResponseEntity.ok(carritoActualizado);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<Carrito> obtenerCarritoPorUsuario(@PathVariable Integer idUsuario) {
        Carrito carrito = carritoService.obtenerCarritoPorUsuario(idUsuario);
        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(carrito);
    }

    @GetMapping("/{idCarrito}")
    public ResponseEntity<Carrito> obtenerCarritoPorId(@PathVariable Integer idCarrito) {
        Carrito carrito = carritoService.obtenerCarritoPorId(idCarrito);
        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(carrito);
    }

    @PostMapping("/{idUsuario}/items")
    public ResponseEntity<Carrito> agregarItem(
            @PathVariable Integer idUsuario,
            @RequestBody ProductoDTOResponse productoDTOResponse) {

        Carrito carritoActualizado = carritoService.agregarItemAlCarrito(idUsuario, productoDTOResponse);
        return ResponseEntity.ok(carritoActualizado);
    }

    @PatchMapping("/{idUsuario}/items/{itemId}")
    public ResponseEntity<Carrito> actualizarItem(
            @PathVariable Integer idUsuario,
            @PathVariable Integer itemId,
            @RequestParam int nuevaCantidad) {

        Carrito carritoActualizado = carritoService.actualizarCantidad(idUsuario, itemId, nuevaCantidad);
        return ResponseEntity.ok(carritoActualizado);
    }

    @DeleteMapping("/{idUsuario}/items/{itemId}")
    public ResponseEntity<Carrito> eliminarItem(
            @PathVariable Integer idUsuario,
            @PathVariable Integer itemId) {

        Carrito carritoActualizado = carritoService.eliminarItem(idUsuario, itemId);
        return ResponseEntity.ok(carritoActualizado);
    }

    @DeleteMapping("/{idUsuario}/items")
    public ResponseEntity<Void> vaciarCarrito(@PathVariable Integer idUsuario) {
        carritoService.vaciarCarrito(idUsuario);
        return ResponseEntity.noContent().build();
    }
}
