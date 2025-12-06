package carritocheckout.carritocheckoutservice.testINT;

import carritocheckout.carritocheckoutservice.dtos.CarritoDTO;
import carritocheckout.carritocheckoutservice.dtos.ProductoDTOResponse;
import carritocheckout.carritocheckoutservice.service.CarritoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarritoIntegrationE2ETest2 {

    @Autowired
    private CarritoService carritoService;

    @Test
    void testAgregarMismoProductoDosVeces() {

        Integer idUsuario = 10001 + new Random().nextInt(999);


        // ----- Primer agregado -----
        ProductoDTOResponse p1 = new ProductoDTOResponse();
        p1.setIdProducto(3);
        p1.setIdVariante(8);  // variante válida real
        p1.setCantidad(1);

        CarritoDTO carrito1 = carritoService.agregarItemAlCarrito(idUsuario, p1);

        // Validación inicial
        Assertions.assertEquals(1, carrito1.getItems().get(0).getCantidad());

        // ----- Segundo agregado (mismo producto) -----
        ProductoDTOResponse p2 = new ProductoDTOResponse();
        p2.setIdProducto(3);
        p2.setIdVariante(8);
        p2.setCantidad(2);

        CarritoDTO carrito2 = carritoService.agregarItemAlCarrito(idUsuario, p2);

        // ----- Validaciones -----
        var item = carrito2.getItems().get(0);

        Assertions.assertEquals(3, item.getCantidad()); // 1 + 2
        Assertions.assertNotNull(item.getPrecio());
        Assertions.assertNotNull(item.getNombre());

        System.out.println("✔ E2E OK — Cantidades sumadas correctamente: " + item.getCantidad());
    }
}
