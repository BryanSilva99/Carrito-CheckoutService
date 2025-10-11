package carritocheckout.carritocheckoutservice.repository;

import carritocheckout.carritocheckoutservice.entities.DireccionEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DireccionEnvioRepository extends JpaRepository<DireccionEnvio, Integer> {
    List<DireccionEnvio> findByUsuarioEnvio_IdUsuario(Integer idUsuario);
}
