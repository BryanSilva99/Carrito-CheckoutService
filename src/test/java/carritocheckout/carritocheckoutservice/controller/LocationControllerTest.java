package carritocheckout.carritocheckoutservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocationControllerTest {

    @Test
    void getLocation_ok() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("8.8.8.8");


        RestTemplate rt = mock(RestTemplate.class);

        Map<String, Object> fakeResponse = Map.of(
                "latitude", -12.05,
                "longitude", -77.05
        );

        when(rt.getForObject("https://ipapi.co/8.8.8.8/json/", Map.class))
                .thenReturn(fakeResponse);

        LocationController controller = new LocationController(rt);

        ResponseEntity<Map<String, Object>> resp = controller.getLocation(request);

        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(-12.05, resp.getBody().get("lat"));
        assertEquals(-77.05, resp.getBody().get("lng"));
    }


    @Test
    void getLocation_badResponse() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("1.1.1.1");

        RestTemplate rt = mock(RestTemplate.class);
        when(rt.getForObject("https://ipapi.co/1.1.1.1/json/", Map.class))
                .thenReturn(Map.of()); // no latitude

        LocationController controller = new LocationController(rt);

        ResponseEntity<Map<String, Object>> resp = controller.getLocation(request);

        assertEquals(400, resp.getStatusCodeValue());
        assertEquals("No se pudo obtener ubicación", resp.getBody().get("error"));
    }


    @Test
    void getLocation_exception() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("9.9.9.9");

        RestTemplate rt = mock(RestTemplate.class);
        when(rt.getForObject("https://ipapi.co/9.9.9.9/json/", Map.class))
                .thenThrow(new RuntimeException("Error"));

        LocationController controller = new LocationController(rt);

        ResponseEntity<Map<String, Object>> resp = controller.getLocation(request);

        assertEquals(500, resp.getStatusCodeValue());
        assertEquals("Error al obtener ubicación por IP", resp.getBody().get("error"));
    }
}
