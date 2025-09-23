package carritocheckout.carritocheckoutservice.controller;

import carritocheckout.carritocheckoutservice.entities.Carrito;
import carritocheckout.carritocheckoutservice.service.CarritoService;
import carritocheckout.carritocheckoutservice.service.CarritoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CarritoController {
    @Autowired
    private CarritoServiceImpl carritoService;

    @PostMapping("/crearCarrito")
    public ResponseEntity<Carrito> crearCarrito(@RequestParam(required = false) Integer idUsuario){
        Carrito carrito = carritoService.agregarCarrito(idUsuario);
        return ResponseEntity.ok(carrito);
    }

    @PutMapping("/{idCarrito}/asignar-usuario")
    public ResponseEntity<Carrito> asignarCarritoAUsuario(@PathVariable Integer idCarrito,@RequestParam Integer idUsuario){
        Carrito carritoActualizado = carritoService.asignarCarritoAUsuario(idCarrito,idUsuario);
        return ResponseEntity.ok(carritoActualizado);
    }

}
