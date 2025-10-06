package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.dtos.ProductoDTOResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CatalogoServiceImpl {
    private final RestTemplate restTemplate;

    public CatalogoServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ProductoDTOResponse obtenerProducto(Integer productoId){
        String url = "http://apiCatalogo" + productoId;
        ProductoDTOResponse productoDTO = restTemplate.getForObject(url, ProductoDTOResponse.class);
        return productoDTO;
    }



}
