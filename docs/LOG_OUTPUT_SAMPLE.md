# Deliverable 4: Log Output Sample

## End-to-End Order Processing Flow Logs

This document shows the expected console/terminal log output when an order is placed and processed successfully through all workflow steps.

---

## Scenario A: Successful Order (80% probability)

```
=== ORDER PLACED ===
2024-01-15 10:30:00.123  INFO [order-service] --- [nio-8080-exec-1] c.f.orderservice.service.OrderService    :
[OrderService] Order #1 - Status: PLACED, Workflow started

2024-01-15 10:30:00.245  INFO [order-service] --- [nio-8080-exec-1] c.f.orderservice.jms.OrderMessagePublisher :
Published order.created message for orderId: 1

=== CAMUNDA WORKFLOW STARTED ===
2024-01-15 10:30:00.312  INFO [order-service] --- [DefaultMessageListenerContainer-1] c.f.orderservice.jms.OrderMessageListener :
Received order.created message: {"orderId":1}

2024-01-15 10:30:00.415  INFO [order-service] --- [DefaultMessageListenerContainer-1] c.f.orderservice.jms.OrderMessageListener :
Started Camunda process for orderId: 1

=== STEP 1: PAYMENT PROCESSING ===
2024-01-15 10:30:00.520  INFO [order-service] --- [camunda-job-executor-thread-1] c.f.orderservice.delegate.ProcessPaymentDelegate :
[CAMUNDA] Processing payment for orderId: 1

2024-01-15 10:30:00.521  INFO [order-service] --- [camunda-job-executor-thread-1] c.f.orderservice.service.OrderService :
[OrderService] Order #1 - Status updated to: PAYMENT_PROCESSING

2024-01-15 10:30:00.612  INFO [payment-service] --- [nio-8081-exec-1] c.f.paymentservice.service.PaymentService :
[PaymentService] Order #1 - Payment processing...

2024-01-15 10:30:00.695  INFO [payment-service] --- [nio-8081-exec-1] c.f.paymentservice.service.PaymentService :
[PaymentService] Order #1 - Payment processing... SUCCESS

=== STEP 2: KITCHEN PREPARATION ===
2024-01-15 10:30:00.780  INFO [order-service] --- [camunda-job-executor-thread-1] c.f.orderservice.delegate.PrepareKitchenDelegate :
[CAMUNDA] Preparing kitchen ticket for orderId: 1

2024-01-15 10:30:00.781  INFO [order-service] --- [camunda-job-executor-thread-1] c.f.orderservice.service.OrderService :
[OrderService] Order #1 - Status updated to: KITCHEN_PREP

2024-01-15 10:30:00.850  INFO [kitchen-service] --- [nio-8082-exec-1] c.f.kitchenservice.service.KitchenService :
[KitchenService] Order #1 - Kitchen ticket created for item: Butter Chicken

2024-01-15 10:30:00.851  INFO [kitchen-service] --- [nio-8082-exec-1] c.f.kitchenservice.service.KitchenService :
[KitchenService] Order #1 - Kitchen ticket created, preparing food...

2024-01-15 10:30:02.870  INFO [kitchen-service] --- [nio-8082-exec-1] c.f.kitchenservice.service.KitchenService :
[KitchenService] Order #1 - Kitchen ticket created, preparing food... READY

=== STEP 3: DELIVERY ===
2024-01-15 10:30:02.950  INFO [order-service] --- [camunda-job-executor-thread-1] c.f.orderservice.delegate.AssignDeliveryDelegate :
[CAMUNDA] Assigning delivery for orderId: 1

2024-01-15 10:30:02.951  INFO [order-service] --- [camunda-job-executor-thread-1] c.f.orderservice.service.OrderService :
[OrderService] Order #1 - Status updated to: OUT_FOR_DELIVERY

2024-01-15 10:30:03.020  INFO [delivery-service] --- [nio-8083-exec-1] c.f.deliveryservice.service.DeliveryService :
[DeliveryService] Order #1 - Driver assigned, delivering...

2024-01-15 10:30:03.021  INFO [delivery-service] --- [nio-8083-exec-1] c.f.deliveryservice.service.DeliveryService :
[DeliveryService] Order #1 - Driver Ravi Kumar is IN_TRANSIT

2024-01-15 10:30:04.545  INFO [delivery-service] --- [nio-8083-exec-1] c.f.deliveryservice.service.DeliveryService :
[DeliveryService] Order #1 - Driver assigned, delivering... DELIVERED (Driver: Ravi Kumar)

=== WORKFLOW COMPLETE ===
2024-01-15 10:30:04.620  INFO [order-service] --- [camunda-job-executor-thread-1] c.f.orderservice.service.OrderService :
[OrderService] Order #1 - Status updated to: DELIVERED

2024-01-15 10:30:04.621  INFO [order-service] --- [camunda-job-executor-thread-1] c.f.orderservice.delegate.UpdateOrderStatusDelegate :
[OrderService] Order #1 - Workflow COMPLETE
```

---

## Scenario B: Payment Failed → Order Cancelled (20% probability)

```
=== ORDER PLACED ===
2024-01-15 10:31:00.100  INFO [order-service] --- [nio-8080-exec-2] c.f.orderservice.service.OrderService :
[OrderService] Order #2 - Status: PLACED, Workflow started

=== STEP 1: PAYMENT FAILED ===
2024-01-15 10:31:00.450  INFO [payment-service] --- [nio-8081-exec-2] c.f.paymentservice.service.PaymentService :
[PaymentService] Order #2 - Payment processing...

2024-01-15 10:31:00.510  INFO [payment-service] --- [nio-8081-exec-2] c.f.paymentservice.service.PaymentService :
[PaymentService] Order #2 - Payment processing... FAILED

=== CAMUNDA GATEWAY → CANCEL PATH ===
2024-01-15 10:31:00.600  INFO [order-service] --- [camunda-job-executor-thread-1] c.f.orderservice.service.OrderService :
[OrderService] Order #2 - Status updated to: CANCELLED

2024-01-15 10:31:00.601  INFO [order-service] --- [camunda-job-executor-thread-1] c.f.orderservice.delegate.CancelOrderDelegate :
[OrderService] Order #2 - CANCELLED
```

---

## Key Log Format Reference

| Log Statement | Source File |
|---------------|-------------|
| `[OrderService] Order #N - Status: PLACED, Workflow started` | `OrderService.java:placeOrder()` |
| `[PaymentService] Order #N - Payment processing... SUCCESS/FAILED` | `PaymentService.java:processPayment()` |
| `[KitchenService] Order #N - Kitchen ticket created, preparing food... READY` | `KitchenService.java:prepareFood()` |
| `[DeliveryService] Order #N - Driver assigned, delivering... DELIVERED` | `DeliveryService.java:assignDelivery()` |
| `[OrderService] Order #N - Workflow COMPLETE` | `UpdateOrderStatusDelegate.java:execute()` |
| `[OrderService] Order #N - CANCELLED` | `CancelOrderDelegate.java:execute()` |
