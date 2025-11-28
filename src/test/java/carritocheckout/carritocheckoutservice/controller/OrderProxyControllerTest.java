package carritocheckout.carritocheckoutservice.controller;

import carritocheckout.carritocheckoutservice.service.OrderProxyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(OrderProxyController.class)
@Import(OrderProxyControllerTest.Config.class)
class OrderProxyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderProxyService orderProxyService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    static class Config {
        @Bean
        public OrderProxyService orderProxyService() {
            return mock(OrderProxyService.class);
        }
    }

    @Test
    void createOrder_ok() throws Exception {
        Map<String, Object> req = Map.of("amount", 100);
        Map<String, Object> resp = Map.of("success", true, "orderId", 10);

        when(orderProxyService.forwardOrder(req)).thenReturn(resp);

        mockMvc.perform(post("/api/proxy/orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.orderId").value(10));
    }

    @Test
    void createOrder_error() throws Exception {
        Map<String, Object> req = Map.of("amount", 200);

        when(orderProxyService.forwardOrder(req))
                .thenThrow(new RuntimeException("Fallo externo"));

        mockMvc.perform(post("/api/proxy/orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Fallo externo"));
    }
}