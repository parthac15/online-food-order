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

@Component("prepareKitchenDelegate")
@RequiredArgsConstructor
@Slf4j
public class PrepareKitchenDelegate implements JavaDelegate {

    private final RestTemplate restTemplate;
    private final OrderService orderService;

    @org.springframework.beans.factory.annotation.Value("${KITCHEN_SERVICE_URL:http://localhost:8082/api/kitchen/tickets}")
    private String kitchenServiceUrl;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        log.info("[CAMUNDA] Preparing kitchen ticket for orderId: {}", orderId);

        // Update order status
        orderService.updateOrderStatus(orderId, "KITCHEN_PREP");

        // Get order details to know the item
        var order = orderService.getOrderById(orderId);

        // Call Kitchen Service
        Map<String, Object> kitchenRequest = new HashMap<>();
        kitchenRequest.put("orderId", orderId);
        kitchenRequest.put("item", order.getItem());

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    kitchenServiceUrl, kitchenRequest, String.class);
            log.info("[CAMUNDA] Kitchen response for orderId {}: {}", orderId, response.getBody());
        } catch (Exception e) {
            log.error("[CAMUNDA] Kitchen prep failed for orderId: {}", orderId, e);
        }
    }
}
