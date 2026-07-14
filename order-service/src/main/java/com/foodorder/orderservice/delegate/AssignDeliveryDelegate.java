package com.foodorder.orderservice.delegate;

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

@Component("assignDeliveryDelegate")
@RequiredArgsConstructor
@Slf4j
public class AssignDeliveryDelegate implements JavaDelegate {

    private final RestTemplate restTemplate;
    private final OrderService orderService;

    @org.springframework.beans.factory.annotation.Value("${DELIVERY_SERVICE_URL:http://localhost:8083/api/deliveries}")
    private String deliveryServiceUrl;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        log.info("[CAMUNDA] Assigning delivery for orderId: {}", orderId);

        // Update order status
        orderService.updateOrderStatus(orderId, "OUT_FOR_DELIVERY");

        // Call Delivery Service
        Map<String, Object> deliveryRequest = new HashMap<>();
        deliveryRequest.put("orderId", orderId);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    java.util.Objects.requireNonNull(deliveryServiceUrl), deliveryRequest, String.class);
            String body = response.getBody() != null ? response.getBody() : "";
            log.info("[CAMUNDA] Delivery response for orderId {}: {}", orderId, body);
        } catch (Exception e) {
            log.error("[CAMUNDA] Delivery assignment failed for orderId: {}", orderId, e);
        }
    }
}
