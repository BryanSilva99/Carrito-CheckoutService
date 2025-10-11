package carritocheckout.carritocheckoutservice.repository;

import carritocheckout.carritocheckoutservice.entities.UsuarioEnvio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioEnvioRepository extends JpaRepository<UsuarioEnvio, Integer> {
    Optional<UsuarioEnvio> findByIdUsuario(Integer idUsuario);
}
