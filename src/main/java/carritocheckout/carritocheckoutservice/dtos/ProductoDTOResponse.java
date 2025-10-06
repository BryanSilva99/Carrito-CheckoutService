package carritocheckout.carritocheckoutservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTOResponse {
    private Integer id;
    private String nombre;
    private Double precio;
    private Integer cantidad;
    private Integer idUsuario;
}
