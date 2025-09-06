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

    @PostMapping("/agregarCarrito")
    public String agregarCarrito(@RequestParam Carrito carrito){
        carritoService.agregarCarrito(carrito);
        return "Hola";
    }

    @GetMapping("/hola")
    public String holaMundo(){
        return "Hola";
    }
}
