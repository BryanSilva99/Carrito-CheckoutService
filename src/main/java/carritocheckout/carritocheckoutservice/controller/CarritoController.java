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

    // ========== GESTIÓN DE CARRITOS ==========

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

    // ========== OPERACIONES POR USUARIO ==========

    /**
     * Agregar un item al carrito de un usuario
     * POST /api/carritos/{idUsuario}/items
     * Body: { "idProducto": 1, "idVariante": 4, "cantidad": 2 }
     */
    @PostMapping("/{idUsuario}/items")
    public ResponseEntity<CarritoDTO> agregarItem(
            @PathVariable Integer idUsuario,
            @RequestBody ProductoDTOResponse productoDTO) {
        CarritoDTO carritoActualizado = carritoService.agregarItemAlCarrito(idUsuario, productoDTO);
        return ResponseEntity.ok(carritoActualizado);
    }

    /**
     * Actualizar cantidad de un producto (y su variante) en el carrito de usuario
     * PATCH /api/carritos/{idUsuario}/items/{productoId}/{idVariante}?nuevaCantidad=5
     */
    @PatchMapping("/{idUsuario}/items/{productoId}/{idVariante}")
    public ResponseEntity<CarritoDTO> actualizarItem(
            @PathVariable Integer idUsuario,
            @PathVariable Integer productoId,
            @PathVariable(required = false) Integer idVariante,
            @RequestParam int nuevaCantidad) {
        CarritoDTO carritoActualizado = carritoService.actualizarCantidad(idUsuario, productoId, idVariante, nuevaCantidad);
        return ResponseEntity.ok(carritoActualizado);
    }

    /**
     * Eliminar un producto (y su variante) del carrito de usuario
     * DELETE /api/carritos/{idUsuario}/items/{productoId}/{idVariante}
     */
    @DeleteMapping("/{idUsuario}/items/{productoId}/{idVariante}")
    public ResponseEntity<CarritoDTO> eliminarItem(
            @PathVariable Integer idUsuario,
            @PathVariable Integer productoId,
            @PathVariable(required = false) Integer idVariante) {
        CarritoDTO carritoActualizado = carritoService.eliminarItem(idUsuario, productoId, idVariante);
        return ResponseEntity.ok(carritoActualizado);
    }

    /**
     * Vaciar todo el carrito de un usuario
     * DELETE /api/carritos/{idUsuario}/items
     */
    @DeleteMapping("/{idUsuario}/items")
    public ResponseEntity<Void> vaciarCarrito(@PathVariable Integer idUsuario) {
        carritoService.vaciarCarrito(idUsuario);
        return ResponseEntity.noContent().build();
    }

    // ========== OPERACIONES POR ID CARRITO (ANÓNIMOS) ==========

    /**
     * Agregar un item al carrito anónimo
     * POST /api/carritos/{idCarrito}/anonimo/items
     * Body: { "idProducto": 1, "idVariante": 4, "cantidad": 2 }
     */
    @PostMapping("/{idCarrito}/anonimo/items")
    public ResponseEntity<CarritoDTO> agregarItemPorId(
            @PathVariable Integer idCarrito,
            @RequestBody ProductoDTOResponse productoDTO) {
        CarritoDTO carritoActualizado = carritoService.agregarItemAlCarritoPorId(idCarrito, productoDTO);
        return ResponseEntity.ok(carritoActualizado);
    }

    /**
     * Actualizar cantidad en carrito anónimo
     * PATCH /api/carritos/{idCarrito}/anonimo/items/{productoId}/{idVariante}?nuevaCantidad=3
     */
    @PatchMapping("/{idCarrito}/anonimo/items/{productoId}/{idVariante}")
    public ResponseEntity<CarritoDTO> actualizarItemPorId(
            @PathVariable Integer idCarrito,
            @PathVariable Integer productoId,
            @PathVariable(required = false) Integer idVariante,
            @RequestParam int nuevaCantidad) {
        CarritoDTO carritoActualizado = carritoService.actualizarCantidadPorId(idCarrito, productoId, idVariante, nuevaCantidad);
        return ResponseEntity.ok(carritoActualizado);
    }

    /**
     * Eliminar un producto (y su variante) del carrito anónimo
     * DELETE /api/carritos/{idCarrito}/anonimo/items/{productoId}/{idVariante}
     */
    @DeleteMapping("/{idCarrito}/anonimo/items/{productoId}/{idVariante}")
    public ResponseEntity<CarritoDTO> eliminarItemPorId(
            @PathVariable Integer idCarrito,
            @PathVariable Integer productoId,
            @PathVariable(required = false) Integer idVariante) {
        CarritoDTO carritoActualizado = carritoService.eliminarItemPorId(idCarrito, productoId, idVariante);
        return ResponseEntity.ok(carritoActualizado);
    }

    /**
     * Vaciar todo el carrito anónimo
     * DELETE /api/carritos/{idCarrito}/anonimo/items
     */
    @DeleteMapping("/{idCarrito}/anonimo/items")
    public ResponseEntity<Void> vaciarCarritoPorId(@PathVariable Integer idCarrito) {
        carritoService.vaciarCarritoPorId(idCarrito);
        return ResponseEntity.noContent().build();
    }
}
