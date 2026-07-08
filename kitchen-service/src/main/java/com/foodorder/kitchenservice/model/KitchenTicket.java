package com.foodorder.kitchenservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "kitchen_tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KitchenTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private String item;

    @Column(nullable = false)
    private String status; // RECEIVED, PREPARING, READY

    @Column(name = "ticket_created_at")
    private LocalDateTime ticketCreatedAt;

    @Column(name = "ticket_completed_at")
    private LocalDateTime ticketCompletedAt;

    @PrePersist
    protected void onCreate() {
        this.ticketCreatedAt = LocalDateTime.now();
    }
}
