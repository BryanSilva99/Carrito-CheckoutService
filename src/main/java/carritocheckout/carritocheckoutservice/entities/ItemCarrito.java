package carritocheckout.carritocheckoutservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer productoId; // o @ManyToOne Producto producto;

    private Integer cantidad;

    private Double precioUnitario; // para calcular totales incluso si cambia el precio en el catálogo

    public Double getSubtotal() {
        return precioUnitario * cantidad;
    }
}
