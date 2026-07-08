package com.foodorder.kitchenservice.service;

import com.foodorder.kitchenservice.dto.KitchenRequest;
import com.foodorder.kitchenservice.dto.KitchenResponse;
import com.foodorder.kitchenservice.model.KitchenTicket;
import com.foodorder.kitchenservice.repository.KitchenTicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class KitchenService {

    private final KitchenTicketRepository kitchenTicketRepository;

    public KitchenResponse prepareFood(KitchenRequest request) {
        log.info("Received kitchen ticket for orderId: {}, item: {}", request.getOrderId(), request.getItem());

        // Create ticket with RECEIVED status
        KitchenTicket ticket = new KitchenTicket();
        ticket.setOrderId(request.getOrderId());
        ticket.setItem(request.getItem());
        ticket.setStatus("RECEIVED");
        kitchenTicketRepository.save(ticket);

        // Simulate cooking time (2 seconds)
        try {
            log.info("Preparing food for orderId: {} ...", request.getOrderId());
            ticket.setStatus("PREPARING");
            kitchenTicketRepository.save(ticket);
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Mark as READY
        ticket.setStatus("READY");
        ticket.setTicketCompletedAt(LocalDateTime.now());
        KitchenTicket saved = kitchenTicketRepository.save(ticket);

        log.info("Food READY for orderId: {}", request.getOrderId());

        return new KitchenResponse(saved.getId(), saved.getOrderId(), saved.getStatus());
    }
}
