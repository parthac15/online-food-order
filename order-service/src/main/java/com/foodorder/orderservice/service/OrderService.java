package com.foodorder.orderservice.service;

import com.foodorder.orderservice.dto.OrderRequest;
import com.foodorder.orderservice.model.Order;
import com.foodorder.orderservice.repository.OrderRepository;
import com.foodorder.orderservice.jms.OrderMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMessagePublisher messagePublisher;

    public Order placeOrder(OrderRequest request) {
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setItem(request.getItem());
        order.setAmount(request.getAmount());
        order.setStatus("PLACED");

        Order savedOrder = orderRepository.save(order);
        log.info("Order placed with ID: {}", savedOrder.getId());

        // Publish message to ActiveMQ
        messagePublisher.publishOrderCreated(savedOrder.getId());

        return savedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public void updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        orderRepository.save(order);
        log.info("Order {} status updated to: {}", orderId, status);
    }
}
