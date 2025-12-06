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
class CarritoIntegrationE2ETest {

    @Autowired
    private CarritoService carritoService;

    @Test
    void testCrearCarritoYAgregarProductoReal() {

        Integer idUsuario = 10001 + new Random().nextInt(999);

        // SE USA ProductoDTOResponse porque tu backend así funciona
        ProductoDTOResponse req = new ProductoDTOResponse();
        req.setIdProducto(3);      // Debe existir en tu API real
        req.setIdVariante(10);     // Debe existir en tu API real
        req.setCantidad(1);

        // ---- E2E REAL ----
        CarritoDTO carrito = carritoService.agregarItemAlCarrito(idUsuario, req);

        // ---- Validaciones ----
        Assertions.assertNotNull(carrito);
        Assertions.assertFalse(carrito.getItems().isEmpty());

        var item = carrito.getItems().get(0);

        Assertions.assertEquals(1, item.getCantidad());
        Assertions.assertNotNull(item.getNombre());
        Assertions.assertNotNull(item.getPrecio());
        Assertions.assertNotNull(item.getImagenUrl());
        Assertions.assertNotNull(item.getSku());

        System.out.println("✔ E2E OK — Producto real cargado: "
                + item.getNombre() + " / S/ " + item.getPrecio());
    }
}
