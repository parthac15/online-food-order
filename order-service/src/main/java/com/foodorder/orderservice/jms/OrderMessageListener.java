package com.foodorder.orderservice.jms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderMessageListener {

    private final RuntimeService runtimeService;
    private final ObjectMapper objectMapper;

    @JmsListener(destination = "order.created")
    public void onOrderCreated(String message) {
        try {
            log.info("Received order.created message: {}", message);

            JsonNode json = objectMapper.readTree(message);
            Long orderId = json.get("orderId").asLong();

            // Start Camunda process instance
            Map<String, Object> variables = new HashMap<>();
            variables.put("orderId", orderId);

            runtimeService.startProcessInstanceByKey("orderProcess", String.valueOf(orderId), variables);
            log.info("Started Camunda process for orderId: {}", orderId);

        } catch (Exception e) {
            log.error("Error processing order.created message: {}", e.getMessage(), e);
        }
    }
}
