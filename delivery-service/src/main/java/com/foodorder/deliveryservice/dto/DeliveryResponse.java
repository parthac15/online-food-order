package com.foodorder.deliveryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponse {
    private Long deliveryId;
    private Long orderId;
    private String driverName;
    private String status;
}
