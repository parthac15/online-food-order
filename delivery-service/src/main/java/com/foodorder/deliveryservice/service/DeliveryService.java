package com.foodorder.deliveryservice.service;

import com.foodorder.deliveryservice.dto.DeliveryRequest;
import com.foodorder.deliveryservice.dto.DeliveryResponse;
import com.foodorder.deliveryservice.model.Delivery;
import com.foodorder.deliveryservice.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final Random random = new Random();

    private static final List<String> DRIVERS = Arrays.asList(
            "Ravi Kumar", "Priya Sharma", "Amit Patel", "Sneha Reddy", "Vikram Singh"
    );

    public DeliveryResponse assignDelivery(DeliveryRequest request) {
        log.info("[DeliveryService] Order #{} - Driver assigned, delivering...", request.getOrderId());

        // Pick a random driver
        String driverName = DRIVERS.get(random.nextInt(DRIVERS.size()));

        Delivery delivery = new Delivery();
        delivery.setOrderId(request.getOrderId());
        delivery.setDriverName(driverName);
        delivery.setStatus("ASSIGNED");
        deliveryRepository.save(delivery);

        // Simulate transit (1.5 seconds)
        try {
            delivery.setStatus("IN_TRANSIT");
            deliveryRepository.save(delivery);
            log.info("[DeliveryService] Order #{} - Driver {} is IN_TRANSIT", request.getOrderId(), driverName);
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Mark as delivered
        delivery.setStatus("DELIVERED");
        delivery.setDeliveredAt(LocalDateTime.now());
        Delivery saved = deliveryRepository.save(delivery);

        log.info("[DeliveryService] Order #{} - Driver assigned, delivering... DELIVERED (Driver: {})",
                request.getOrderId(), driverName);

        return new DeliveryResponse(saved.getId(), saved.getOrderId(), saved.getDriverName(), saved.getStatus());
    }
}
