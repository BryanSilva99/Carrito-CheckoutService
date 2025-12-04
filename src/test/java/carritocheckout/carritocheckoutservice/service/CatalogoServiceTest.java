package carritocheckout.carritocheckoutservice.service;
import carritocheckout.carritocheckoutservice.dtos.CarritoDTO;
import carritocheckout.carritocheckoutservice.dtos.ProductoDTOResponse;
import carritocheckout.carritocheckoutservice.entities.Carrito;
import carritocheckout.carritocheckoutservice.entities.ItemCarrito;
import carritocheckout.carritocheckoutservice.mapper.CarritoMapper;
import carritocheckout.carritocheckoutservice.repository.CarritoRepository;
import carritocheckout.carritocheckoutservice.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CatalogoServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CatalogoServiceImpl catalogoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerProducto_debeRetornarProducto() {
        // Datos simulados
        ProductoDTOResponse productoSimulado = new ProductoDTOResponse();
        productoSimulado.setIdProducto(1);
        productoSimulado.setNombre("Producto Test");
        productoSimulado.setPrecio(20.0);
        productoSimulado.setSku("SKU-TEST");

        // Mockear RestTemplate
        when(restTemplate.getForObject("http://apiCatalogo1", ProductoDTOResponse.class))
                .thenReturn(productoSimulado);

        // Ejecutar método
        ProductoDTOResponse resultado = catalogoService.obtenerProducto(1);

        // Verificar
        assertNotNull(resultado);
        assertEquals(1, resultado.getIdProducto());
        assertEquals("Producto Test", resultado.getNombre());
        assertEquals(20.0, resultado.getPrecio());
    }
}
