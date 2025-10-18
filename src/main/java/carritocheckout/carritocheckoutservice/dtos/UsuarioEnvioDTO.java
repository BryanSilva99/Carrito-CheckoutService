package carritocheckout.carritocheckoutservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioEnvioDTO {
    private Integer id;
    private Integer idUsuario;
    private String email;
    private String nombreCompleto;
    private String telefono;
}
