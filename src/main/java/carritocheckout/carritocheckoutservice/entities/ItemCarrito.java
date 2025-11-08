package carritocheckout.carritocheckoutservice.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private Integer productoId; // ID del producto que viene de otro servicio

    private Integer idVariante;

    private Integer cantidad;

    private Double precioUnitario; // para calcular totales incluso si cambia el precio en el catálogo

    public Double getSubtotal() {
        return precioUnitario * cantidad;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    @JsonIgnore
    @JsonBackReference
    private Carrito carrito;
}
