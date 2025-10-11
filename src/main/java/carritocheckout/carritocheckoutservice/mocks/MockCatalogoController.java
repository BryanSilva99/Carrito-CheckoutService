package carritocheckout.carritocheckoutservice.mocks;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/mock/catalogo")
public class MockCatalogoController {

    private final List<Map<String, Object>> productos = List.of(
            Map.of("idProducto", 1, "nombre", "Casaca Deportiva", "precio", 2500.0, "imagenUrl", "https://texsamex.pe/wp-content/uploads/2021/11/2-2.png"),
            Map.of("idProducto", 2, "nombre", "Pantalon Deportivo", "precio", 80.0, "imagenUrl", "https://png.pngtree.com/png-vector/20250304/ourmid/pngtree-casual-black-jogger-pants-with-elastic-cuffs-png-image_15704509.png"),
            Map.of("idProducto", 3, "nombre", "Zapatillas Deportivas", "precio", 150.0, "imagenUrl", "https://png.pngtree.com/png-clipart/20241231/original/pngtree-running-shoes-or-sneakers-on-a-transparent-background-png-image_18457027.png")
    );

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return productos;
    }

    @GetMapping("/{idProducto}")
    public Map<String, Object> getProducto(@PathVariable Integer idProducto) {
        return productos.stream()
                .filter(p -> p.get("idProducto").equals(idProducto))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }
}
