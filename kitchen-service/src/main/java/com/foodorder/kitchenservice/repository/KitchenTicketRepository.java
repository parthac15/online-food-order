package com.foodorder.kitchenservice.repository;

import com.foodorder.kitchenservice.model.KitchenTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KitchenTicketRepository extends JpaRepository<KitchenTicket, Long> {
}
