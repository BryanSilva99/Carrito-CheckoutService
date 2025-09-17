package carritocheckout.carritocheckoutservice.repository;

import carritocheckout.carritocheckoutservice.entities.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Integer> {
    Optional<Carrito> findByIdUsuario(Integer idUsuario);
}