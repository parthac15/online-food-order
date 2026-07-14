package com.foodorder.orderservice.delegate;

import com.foodorder.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("updateOrderStatusDelegate")
@RequiredArgsConstructor
@Slf4j
public class UpdateOrderStatusDelegate implements JavaDelegate {

    private final OrderService orderService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        orderService.updateOrderStatus(orderId, "DELIVERED");
        log.info("[OrderService] Order #{} - Workflow COMPLETE", orderId);
    }
}
