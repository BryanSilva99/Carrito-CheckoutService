package carritocheckout.carritocheckoutservice.controller;

import carritocheckout.carritocheckoutservice.entities.Carrito;
import carritocheckout.carritocheckoutservice.entities.ItemCarrito;
import carritocheckout.carritocheckoutservice.service.CarritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    // Crear carrito para un usuario
    @PostMapping
    public ResponseEntity<Carrito> crearCarrito(@RequestBody Carrito carrito) {
        Carrito nuevo = carritoService.crearCarrito(carrito);
        return ResponseEntity.ok(nuevo);
    }

    // Obtener carrito por idUsuario
    @GetMapping("/{idUsuario}")
    public ResponseEntity<Carrito> obtenerCarrito(@PathVariable Integer idUsuario) {
        Carrito carrito = carritoService.obtenerCarritoPorUsuario(idUsuario);
        return ResponseEntity.ok(carrito);
    }

    // Agregar un item
    @PostMapping("/{idUsuario}/items")
    public ResponseEntity<Carrito> agregarItem(@PathVariable Integer idUsuario, @RequestBody ItemCarrito item) {
        Carrito carritoActualizado = carritoService.agregarItem(idUsuario, item);
        return ResponseEntity.ok(carritoActualizado);
    }

    // Actualizar cantidad de un item
    @PatchMapping("/{idUsuario}/items/{itemId}")
    public ResponseEntity<Carrito> actualizarItem(@PathVariable Integer idUsuario,
                                                  @PathVariable Integer itemId,
                                                  @RequestParam int nuevaCantidad) {
        Carrito carritoActualizado = carritoService.actualizarCantidad(idUsuario, itemId, nuevaCantidad);
        return ResponseEntity.ok(carritoActualizado);
    }

    // Eliminar un item
    @DeleteMapping("/{idUsuario}/items/{itemId}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Integer idUsuario, @PathVariable Integer itemId) {
        carritoService.eliminarItem(idUsuario, itemId);
        return ResponseEntity.noContent().build();
    }

    // Vaciar carrito
    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Void> vaciarCarrito(@PathVariable Integer idUsuario) {
        carritoService.vaciarCarrito(idUsuario);
        return ResponseEntity.noContent().build();
    }
}
