package com.foodorder.orderservice.jms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderMessagePublisher {

    private final JmsTemplate jmsTemplate;

    public void publishOrderCreated(Long orderId) {
        String message = "{\"orderId\":" + orderId + "}";
        jmsTemplate.convertAndSend("order.created", message);
        log.info("Published order.created message for orderId: {}", orderId);
    }
}
