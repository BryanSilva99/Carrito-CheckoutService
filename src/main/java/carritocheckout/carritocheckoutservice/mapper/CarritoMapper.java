package carritocheckout.carritocheckoutservice.mapper;

import carritocheckout.carritocheckoutservice.dtos.CarritoDTO;
import carritocheckout.carritocheckoutservice.dtos.ProductoDTOResponse;
import carritocheckout.carritocheckoutservice.entities.Carrito;
import carritocheckout.carritocheckoutservice.entities.ItemCarrito;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CarritoMapper {

    public CarritoDTO toDTO(Carrito carrito) {
        CarritoDTO dto = new CarritoDTO();
        dto.setIdCarrito(carrito.getId());
        dto.setIdUsuario(carrito.getIdUsuario());
        dto.setTotal(carrito.getTotal());

        if (carrito.getItems() != null) {
            dto.setItems(carrito.getItems().stream()
                    .map(this::toProductoDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private ProductoDTOResponse toProductoDTO(ItemCarrito item) {
        ProductoDTOResponse dto = new ProductoDTOResponse();
        dto.setIdProducto(item.getProductoId());
        dto.setCantidad(item.getCantidad());
        dto.setPrecio(item.getPrecioUnitario());

        // Luego, en el Service, puedes rellenar los demás campos con el mock
        return dto;
    }
}
