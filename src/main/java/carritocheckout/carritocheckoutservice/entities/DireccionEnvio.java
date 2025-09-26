package carritocheckout.carritocheckoutservice.entities;

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

    private Integer idUsuario;   // FK lógica, no relación directa porque User está en otro servicio

    private String nombreCompleto;
    private String telefono;       // Teléfono de contacto
    
    private String direccionLinea1; // Calle principal, número
    private String direccionLinea2; // Piso, apartamento, referencia
    private String ciudad;
    private String provincia;
    private String codigoPostal;
    private String pais;

    private boolean principal; // Si esta es la dirección principal del usuario
}
