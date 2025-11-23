package carritocheckout.carritocheckoutservice.service;

import org.springframework.http.HttpHeaders;

import java.util.Map;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderProxyService {

    private final RestTemplate restTemplate;

    public OrderProxyService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public Map<String, Object> forwardOrder(Map<String, Object> orderPayload) {
        String externalUrl = "https://orders-command-833583666995.us-central1.run.app/api/orders";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(orderPayload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                externalUrl,
                request,
                Map.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error calling external API");
        }

        return response.getBody();
    }
}

