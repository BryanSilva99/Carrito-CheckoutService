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
class CarritoIntegrationE2ETest3 {

    @Autowired
    private CarritoService carritoService;

    @Test
    void testAgregarVariosProductosAlCarrito() {

        Integer idUsuario = 10001 + new Random().nextInt(999);


        ProductoDTOResponse p1 = new ProductoDTOResponse();
        p1.setIdProducto(5);   // EXISTENTE
        p1.setIdVariante(18);  // EXISTENTE
        p1.setCantidad(1);

        ProductoDTOResponse p2 = new ProductoDTOResponse();
        p2.setIdProducto(6);   // EXISTENTE
        p2.setIdVariante(22);  // EXISTENTE
        p2.setCantidad(3);

        // ---- agregar primero ----
        CarritoDTO carrito = carritoService.agregarItemAlCarrito(idUsuario, p1);

        // ---- agregar segundo ----
        carrito = carritoService.agregarItemAlCarrito(idUsuario, p2);

        Assertions.assertNotNull(carrito);
        Assertions.assertEquals(2, carrito.getItems().size());

        // Buscar cada producto por idProducto
        var item1 = carrito.getItems().stream()
                .filter(i -> i.getIdProducto().equals(5))
                .findFirst()
                .orElseThrow();

        var item2 = carrito.getItems().stream()
                .filter(i -> i.getIdProducto().equals(6))
                .findFirst()
                .orElseThrow();

        Assertions.assertEquals(1, item1.getCantidad());
        Assertions.assertEquals(3, item2.getCantidad());
    }
}
