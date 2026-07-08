package com.foodorder.kitchenservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KitchenResponse {
    private Long ticketId;
    private Long orderId;
    private String status;
}
