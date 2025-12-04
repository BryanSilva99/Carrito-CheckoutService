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
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CarritoServiceTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CarritoMapper carritoMapper;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------------------------------------------------
    // 1) agregarCarrito()
    // -------------------------------------------------------------



    // -------------------------------------------------------------
    // 2) asignarCarritoAUsuario()
    // -------------------------------------------------------------
    @Test
    void asignarCarritoAUsuario_debeActualizarUsuario() {
        Carrito carrito = new Carrito();
        carrito.setId(1); // <-- usar "id" según la entidad
        carrito.setIdUsuario(null);

        when(carritoRepository.findById(1)).thenReturn(Optional.of(carrito));
        when(carritoRepository.save(carrito)).thenReturn(carrito);
        when(carritoMapper.toDTO(carrito)).thenReturn(new CarritoDTO());

        CarritoDTO resultado = carritoService.asignarCarritoAUsuario(1, 99);

        assertNotNull(resultado);
        assertEquals(99, carrito.getIdUsuario());
    }

    // -------------------------------------------------------------
    // 3) crearCarrito()
    // -------------------------------------------------------------
    @Test
    void crearCarrito_debeGuardarCarrito() {
        Carrito carrito = new Carrito();
        carrito.setItems(new ArrayList<>());

        when(carritoRepository.save(carrito)).thenReturn(carrito);
        when(carritoMapper.toDTO(carrito)).thenReturn(new CarritoDTO());

        CarritoDTO resultado = carritoService.crearCarrito(carrito);

        assertNotNull(resultado);
        verify(carritoRepository, times(1)).save(carrito);
    }

    // -------------------------------------------------------------
    // 4) obtenerCarritoPorId()
    // -------------------------------------------------------------
    @Test
    void obtenerCarritoPorId_debeRetornarDTO() {
        Carrito carrito = new Carrito();
        carrito.setId(1); // <-- corregido
        carrito.setItems(new ArrayList<>());

        when(carritoRepository.findById(1)).thenReturn(Optional.of(carrito));
        when(carritoMapper.toDTO(carrito)).thenReturn(new CarritoDTO());

        CarritoDTO resultado = carritoService.obtenerCarritoPorId(1);

        assertNotNull(resultado);
        verify(carritoRepository, times(1)).findById(1);
    }


    // -------------------------------------------------------------
    // 5) agregarItemAlCarrito()
    // -------------------------------------------------------------
    @Test
    void agregarItemAlCarrito_debeAgregarItemCorrectamente() {
        ProductoDTOResponse prod = new ProductoDTOResponse(1, 99, "Producto test", "SKU-TEST", 20.0, 1, "img.jpg");

        Carrito carrito = new Carrito();
        carrito.setId(10);
        carrito.setItems(new ArrayList<>());

        CarritoServiceImpl spyService = Mockito.spy(carritoService);

        // Mock repositorios
        when(carritoRepository.findByIdUsuario(10)).thenReturn(Optional.of(carrito));
        when(itemRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carrito);
        when(carritoMapper.toDTO(any(Carrito.class))).thenReturn(new CarritoDTO());

        // Mockear restTemplate para que devolver un mapa simulado
        RestTemplate mockRest = mock(RestTemplate.class);
        spyService.restTemplate = mockRest;

        Map<String, Object> productoSimulado = Map.of(
                "id", 1,
                "nombre", "Producto test",
                "variantes", List.of(Map.of("id", 99, "precio", 20.0, "sku", "SKU-TEST", "varianteImagenes", List.of(Map.of("imagen", "img.jpg"))))
        );

        when(mockRest.getForEntity(anyString(), eq(Map.class))).thenReturn(ResponseEntity.ok(productoSimulado));

        // Ejecutar método
        CarritoDTO result = spyService.agregarItemAlCarrito(10, prod);

        assertNotNull(result);
        assertEquals(1, carrito.getItems().size());
    }
}