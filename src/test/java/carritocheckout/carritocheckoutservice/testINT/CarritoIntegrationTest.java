package carritocheckout.carritocheckoutservice.testINT;

import carritocheckout.carritocheckoutservice.repository.ItemRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CarritoIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @Order(1)
    void agregarProductoDebePersistirEnBD() {

        given()
                .baseUri("http://localhost:" + port)
                .contentType("application/json")
                .body("""
                {
                    "productId": 10,
                    "cantidad": 2
                }
            """)
                .when()
                .post("/carrito/1/items")
                .then()
                .statusCode(200)
                .body("productId", equalTo(10))
                .body("cantidad", equalTo(2));

        // Verificar en BD
        var items = itemRepository.findByCarrito_Id(1L);

        assertFalse(items.isEmpty());
        assertEquals(10, items.get(0).getProductoId());
        assertEquals(2, items.get(0).getCantidad());
    }
}
