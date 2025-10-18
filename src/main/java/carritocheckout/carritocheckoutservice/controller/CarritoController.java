package carritocheckout.carritocheckoutservice.controller;

import carritocheckout.carritocheckoutservice.dtos.CarritoDTO;
import carritocheckout.carritocheckoutservice.dtos.ProductoDTOResponse;
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

    @PostMapping
    public ResponseEntity<CarritoDTO> crearCarrito(@RequestParam(required = false) Integer idUsuario) {
        CarritoDTO carrito = carritoService.agregarCarrito(idUsuario);
        return ResponseEntity.ok(carrito);
    }

    @PutMapping("/{idCarrito}/asignar-usuario")
    public ResponseEntity<CarritoDTO> asignarCarritoAUsuario(
            @PathVariable Integer idCarrito,
            @RequestParam Integer idUsuario) {
        CarritoDTO carritoActualizado = carritoService.asignarCarritoAUsuario(idCarrito, idUsuario);
        return ResponseEntity.ok(carritoActualizado);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<CarritoDTO> obtenerCarritoPorUsuario(@PathVariable Integer idUsuario) {
        CarritoDTO carrito = carritoService.obtenerCarritoPorUsuario(idUsuario);
        return ResponseEntity.ok(carrito);
    }

    @GetMapping("/{idCarrito}")
    public ResponseEntity<CarritoDTO> obtenerCarritoPorId(@PathVariable Integer idCarrito) {
        CarritoDTO carrito = carritoService.obtenerCarritoPorId(idCarrito);
        return ResponseEntity.ok(carrito);
    }

    @PostMapping("/{idUsuario}/items")
    public ResponseEntity<CarritoDTO> agregarItem(
            @PathVariable Integer idUsuario,
            @RequestBody ProductoDTOResponse productoDTO) {
        CarritoDTO carritoActualizado = carritoService.agregarItemAlCarrito(idUsuario, productoDTO);
        return ResponseEntity.ok(carritoActualizado);
    }

    @PatchMapping("/{idUsuario}/items/{itemId}")
    public ResponseEntity<CarritoDTO> actualizarItem(
            @PathVariable Integer idUsuario,
            @PathVariable Integer itemId,
            @RequestParam int nuevaCantidad) {
        CarritoDTO carritoActualizado = carritoService.actualizarCantidad(idUsuario, itemId, nuevaCantidad);
        return ResponseEntity.ok(carritoActualizado);
    }

    @DeleteMapping("/{idUsuario}/items/{itemId}")
    public ResponseEntity<CarritoDTO> eliminarItem(
            @PathVariable Integer idUsuario,
            @PathVariable Integer itemId) {
        CarritoDTO carritoActualizado = carritoService.eliminarItem(idUsuario, itemId);
        return ResponseEntity.ok(carritoActualizado);
    }

    @DeleteMapping("/{idUsuario}/items")
    public ResponseEntity<Void> vaciarCarrito(@PathVariable Integer idUsuario) {
        carritoService.vaciarCarrito(idUsuario);
        return ResponseEntity.noContent().build();
    }
}
