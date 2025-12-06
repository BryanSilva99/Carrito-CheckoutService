package carritocheckout.carritocheckoutservice.testINT;

import carritocheckout.carritocheckoutservice.dtos.ProductoDTOResponse;
import carritocheckout.carritocheckoutservice.service.CatalogoServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

public class CarritoIntegrationTest {

    @Test
    void testObtenerProducto() {
        // Mock del RestTemplate
        RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);

        // Instanciamos el servicio con el mock
        CatalogoServiceImpl catalogoService = new CatalogoServiceImpl(restTemplateMock);

        // Preparamos una respuesta simulada
        ProductoDTOResponse mockResponse = new ProductoDTOResponse();
        mockResponse.setIdProducto(10);
        mockResponse.setIdVariante(1);
        mockResponse.setNombre("Producto de prueba");
        mockResponse.setSku("ABC123");
        mockResponse.setPrecio(50.0);
        mockResponse.setCantidad(5);
        mockResponse.setImagenUrl("http://img.com/a.png");

        // URL que debe construirse
        String urlEsperada = "http://apiCatalogo10";

        // Definir comportamiento del mock
        Mockito.when(restTemplateMock.getForObject(eq(urlEsperada), eq(ProductoDTOResponse.class)))
                .thenReturn(mockResponse);

        // Ejecutamos el método real
        ProductoDTOResponse resultado = catalogoService.obtenerProducto(10);

        // Validaciones
        assertNotNull(resultado);
        assertEquals(10, resultado.getIdProducto());
        assertEquals("Producto de prueba", resultado.getNombre());
        assertEquals(50.0, resultado.getPrecio());
    }
}
