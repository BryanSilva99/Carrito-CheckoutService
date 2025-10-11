package carritocheckout.carritocheckoutservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer idUsuario; // Id lógico del usuario del otro servicio
    private String email;
    private String nombreCompleto;
    private String telefono;

    @OneToMany(mappedBy = "usuarioEnvio", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DireccionEnvio> direcciones;
}