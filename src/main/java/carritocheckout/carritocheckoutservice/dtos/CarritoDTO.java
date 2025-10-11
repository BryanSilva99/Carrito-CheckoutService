package carritocheckout.carritocheckoutservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoDTO {
    private Integer idCarrito;
    private Integer idUsuario;
    private List<ProductoDTOResponse> items;
    private Double total;
}
