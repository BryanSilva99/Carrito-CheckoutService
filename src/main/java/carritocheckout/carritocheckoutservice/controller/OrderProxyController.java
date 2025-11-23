package carritocheckout.carritocheckoutservice.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import carritocheckout.carritocheckoutservice.service.OrderProxyService;

@RestController
@RequestMapping("/api/proxy/orders")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderProxyController {

    private final OrderProxyService orderProxyService;

    public OrderProxyController(OrderProxyService orderProxyService) {
        this.orderProxyService = orderProxyService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> orderData) {
        try {
            Map<String, Object> response = orderProxyService.forwardOrder(orderData);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("success", false, "message", ex.getMessage()));
        }
    }
}
