package carritocheckout.carritocheckoutservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getLocation(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        String apiUrl = "https://ipapi.co/" + clientIp + "/json/";

        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

            if (response == null || !response.containsKey("latitude")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "No se pudo obtener ubicación"
                ));
            }

            return ResponseEntity.ok(Map.of(
                "lat", response.get("latitude"),
                "lng", response.get("longitude")
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al obtener ubicación por IP"
            ));
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
