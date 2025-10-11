package carritocheckout.carritocheckoutservice.repository;

import carritocheckout.carritocheckoutservice.entities.Carrito;
import carritocheckout.carritocheckoutservice.entities.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ItemRepository extends JpaRepository<ItemCarrito, Integer> {
    Optional<ItemCarrito> findByCarritoAndProductoId(Carrito carrito, Integer productoId);
    List<ItemCarrito> findByCarrito(Carrito carrito);
    void deleteByCarrito(Carrito carrito);
}
