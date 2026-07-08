package com.foodorder.kitchenservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KitchenRequest {
    private Long orderId;
    private String item;
}
