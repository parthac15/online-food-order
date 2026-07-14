package com.foodorder.deliveryservice.controller;

import com.foodorder.deliveryservice.dto.DeliveryRequest;
import com.foodorder.deliveryservice.dto.DeliveryResponse;
import com.foodorder.deliveryservice.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<DeliveryResponse> assignDelivery(@RequestBody DeliveryRequest request) {
        DeliveryResponse response = deliveryService.assignDelivery(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Delivery Service is UP");
    }
}
