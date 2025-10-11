package carritocheckout.carritocheckoutservice.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String direccionLinea1;
    private String direccionLinea2;
    private String ciudad;
    private String provincia;
    private String codigoPostal;
    private String pais;

    private boolean principal;

    @ManyToOne
    @JoinColumn(name = "usuario_envio_id")
    @JsonBackReference
    private UsuarioEnvio usuarioEnvio;
}
