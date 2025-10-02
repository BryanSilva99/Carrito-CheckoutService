package carritocheckout.carritocheckoutservice.repository;

import carritocheckout.carritocheckoutservice.entities.Carrito;
import carritocheckout.carritocheckoutservice.entities.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ItemRepository extends JpaRepository<ItemCarrito, Integer> {
    void deleteByCarrito(Carrito carrito);
}
