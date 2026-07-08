package com.foodorder.orderservice.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodorder.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component("processPaymentDelegate")
@RequiredArgsConstructor
@Slf4j
public class ProcessPaymentDelegate implements JavaDelegate {

    private final RestTemplate restTemplate;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        log.info("[CAMUNDA] Processing payment for orderId: {}", orderId);

        // Update order status
        orderService.updateOrderStatus(orderId, "PAYMENT_PROCESSING");

        // Get order details to know the amount
        var order = orderService.getOrderById(orderId);

        // Call Payment Service
        Map<String, Object> paymentRequest = new HashMap<>();
        paymentRequest.put("orderId", orderId);
        paymentRequest.put("amount", order.getAmount());

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://localhost:8081/api/payments", paymentRequest, String.class);

            JsonNode responseBody = objectMapper.readTree(response.getBody());
            String paymentStatus = responseBody.get("status").asText();

            execution.setVariable("paymentStatus", paymentStatus);
            log.info("[CAMUNDA] Payment status for orderId {}: {}", orderId, paymentStatus);

        } catch (Exception e) {
            log.error("[CAMUNDA] Payment failed for orderId: {}", orderId, e);
            execution.setVariable("paymentStatus", "FAILED");
        }
    }
}
