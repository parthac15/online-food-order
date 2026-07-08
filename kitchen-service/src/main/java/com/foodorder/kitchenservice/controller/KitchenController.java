package com.foodorder.kitchenservice.controller;

import com.foodorder.kitchenservice.dto.KitchenRequest;
import com.foodorder.kitchenservice.dto.KitchenResponse;
import com.foodorder.kitchenservice.service.KitchenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kitchen")
@RequiredArgsConstructor
public class KitchenController {

    private final KitchenService kitchenService;

    @PostMapping("/tickets")
    public ResponseEntity<KitchenResponse> prepareFood(@RequestBody KitchenRequest request) {
        KitchenResponse response = kitchenService.prepareFood(request);
        return ResponseEntity.ok(response);
    }
}
