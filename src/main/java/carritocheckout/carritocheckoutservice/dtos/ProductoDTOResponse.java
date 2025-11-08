package carritocheckout.carritocheckoutservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTOResponse {
    private Integer idProducto;
    private Integer idVariante;
    private String nombre;
    private String sku;
    private Double precio;
    private Integer cantidad;
    private String imagenUrl;
}
