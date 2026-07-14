package com.foodorder.paymentservice.service;

import com.foodorder.paymentservice.dto.PaymentRequest;
import com.foodorder.paymentservice.dto.PaymentResponse;
import com.foodorder.paymentservice.model.Payment;
import com.foodorder.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final Random random = new Random();

    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("[PaymentService] Order #{} - Payment processing...", request.getOrderId());

        // Simulate payment: 80% success rate
        boolean isSuccess = random.nextInt(100) < 80;
        String status = isSuccess ? "SUCCESS" : "FAILED";

        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setStatus(status);

        Payment saved = paymentRepository.save(payment);
        log.info("[PaymentService] Order #{} - Payment processing... {}", request.getOrderId(), status);

        return new PaymentResponse(saved.getId(), saved.getOrderId(), saved.getStatus());
    }
}
