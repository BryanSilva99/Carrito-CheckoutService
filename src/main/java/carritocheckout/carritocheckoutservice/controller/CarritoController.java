package carritocheckout.carritocheckoutservice.controller;

import carritocheckout.carritocheckoutservice.dtos.ProductoDTOResponse;
import carritocheckout.carritocheckoutservice.entities.Carrito;
import carritocheckout.carritocheckoutservice.entities.ItemCarrito;
import carritocheckout.carritocheckoutservice.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carritos")
@CrossOrigin(origins = "http://localhost:5173")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }


    @PostMapping
    public ResponseEntity<Carrito> crearCarrito(@RequestParam(required = false) Integer idUsuario){
        Carrito carrito = carritoService.agregarCarrito(idUsuario);
        return ResponseEntity.ok(carrito);
    }

    @PutMapping("/{idCarrito}/asignar-usuario")
    public ResponseEntity<Carrito> asignarCarritoAUsuario(@PathVariable Integer idCarrito,@RequestParam Integer idUsuario) {
        Carrito carritoActualizado = carritoService.asignarCarritoAUsuario(idCarrito, idUsuario);
        return ResponseEntity.ok(carritoActualizado);

    }

    // Obtener carrito por idUsuario
    @GetMapping("/{idUsuario}")
    public ResponseEntity<Carrito> obtenerCarrito(@PathVariable Integer idUsuario) {
        Carrito carrito = carritoService.obtenerCarritoPorUsuario(idUsuario);
        return ResponseEntity.ok(carrito);
    }

    // Agregar un item
    @PostMapping("items")
    public ResponseEntity<Carrito> agregarItem(@RequestBody ProductoDTOResponse productoDTOResponse) {
        carritoService.agregarItemAlCarrito(productoDTOResponse);
        return ResponseEntity.ok().build();
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
