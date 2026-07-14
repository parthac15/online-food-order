# Deliverable 1: API Low-Level Design (LLD)

## Overview

This document describes the complete API contracts for the Online Food Order Processing System, including REST endpoint contracts, ActiveMQ message formats, and error handling strategies.

---

## 1. Order Service – REST API (`http://localhost:8080`)

### 1.1 POST /api/orders — Place a New Order

**Purpose:** Accepts an order from the React UI, persists it with status `PLACED`, and publishes an event to ActiveMQ to start the Camunda workflow.

**Request:**
```http
POST /api/orders
Content-Type: application/json
```

```json
{
  "customerName": "John Doe",
  "item": "Butter Chicken",
  "amount": 15.99
}
```

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `customerName` | `String` | ✅ | Non-null, non-blank |
| `item` | `String` | ✅ | Non-null, non-blank |
| `amount` | `BigDecimal` | ✅ | Positive decimal |

**Response – HTTP 201 Created:**
```json
{
  "id": 1,
  "customerName": "John Doe",
  "item": "Butter Chicken",
  "amount": 15.99,
  "status": "PLACED",
  "createdAt": "2024-01-15T10:30:00"
}
```

**Error Responses:**
| HTTP Code | Scenario |
|-----------|----------|
| `400 Bad Request` | Missing required fields |
| `500 Internal Server Error` | Database or ActiveMQ failure |

---

### 1.2 GET /api/orders — Get All Orders

**Purpose:** Returns all orders in reverse chronological order. Used by the React dashboard for real-time polling (every 2 seconds).

**Request:**
```http
GET /api/orders
```

**Response – HTTP 200 OK:**
```json
[
  {
    "id": 3,
    "customerName": "Priya Singh",
    "item": "Chicken Biryani",
    "amount": 14.99,
    "status": "DELIVERED",
    "createdAt": "2024-01-15T10:35:00"
  },
  {
    "id": 2,
    "customerName": "Amit Patel",
    "item": "Paneer Tikka",
    "amount": 10.99,
    "status": "KITCHEN_PREP",
    "createdAt": "2024-01-15T10:32:00"
  },
  {
    "id": 1,
    "customerName": "John Doe",
    "item": "Butter Chicken",
    "amount": 15.99,
    "status": "CANCELLED",
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

**Order Status Enum:**
| Status | Description |
|--------|-------------|
| `PLACED` | Order received, workflow starting |
| `PAYMENT_PROCESSING` | Camunda Step 1 in progress |
| `KITCHEN_PREP` | Payment succeeded, kitchen preparing |
| `OUT_FOR_DELIVERY` | Food ready, driver assigned |
| `DELIVERED` | Order completed successfully |
| `CANCELLED` | Payment failed, order cancelled |

---

### 1.3 GET /api/orders/{id} — Get Single Order

**Request:**
```http
GET /api/orders/1
```

**Response – HTTP 200 OK:**
```json
{
  "id": 1,
  "customerName": "John Doe",
  "item": "Butter Chicken",
  "amount": 15.99,
  "status": "DELIVERED",
  "createdAt": "2024-01-15T10:30:00"
}
```

**Error Responses:**
| HTTP Code | Scenario |
|-----------|----------|
| `500 Internal Server Error` | Order not found (RuntimeException thrown) |

---

## 2. Payment Service – REST API (`http://localhost:8081`)

### 2.1 POST /api/payments — Process Payment

**Purpose:** Called by Camunda (ProcessPaymentDelegate) as a Service Task. Simulates payment processing with 80% success rate.

**Request:**
```http
POST /api/payments
Content-Type: application/json
```

```json
{
  "orderId": 1,
  "amount": 15.99
}
```

**Response – HTTP 200 OK:**
```json
{
  "paymentId": 1,
  "orderId": 1,
  "status": "SUCCESS"
}
```

| Field | Possible Values |
|-------|----------------|
| `status` | `"SUCCESS"` (80% probability) or `"FAILED"` (20% probability) |

**Camunda Integration:** The `paymentStatus` variable is set in the process execution. The BPMN exclusive gateway routes to either `prepareKitchen` (SUCCESS) or `cancelOrder` (FAILED).

---

## 3. Kitchen Service – REST API (`http://localhost:8082`)

### 3.1 POST /api/kitchen/tickets — Create Kitchen Ticket

**Purpose:** Called by Camunda (PrepareKitchenDelegate) as Step 2. Simulates food preparation with a 2-second delay.

**Request:**
```http
POST /api/kitchen/tickets
Content-Type: application/json
```

```json
{
  "orderId": 1,
  "item": "Butter Chicken"
}
```

**Response – HTTP 200 OK:**
```json
{
  "ticketId": 1,
  "orderId": 1,
  "status": "READY"
}
```

**Internal Status Progression:** `RECEIVED → PREPARING → READY`

---

## 4. Delivery Service – REST API (`http://localhost:8083`)

### 4.1 POST /api/deliveries — Assign Delivery

**Purpose:** Called by Camunda (AssignDeliveryDelegate) as Step 3. Assigns a random driver and simulates 1.5-second transit.

**Request:**
```http
POST /api/deliveries
Content-Type: application/json
```

```json
{
  "orderId": 1
}
```

**Response – HTTP 200 OK:**
```json
{
  "deliveryId": 1,
  "orderId": 1,
  "driverName": "Ravi Kumar",
  "status": "DELIVERED"
}
```

**Available Mock Drivers:** Ravi Kumar, Priya Sharma, Amit Patel, Sneha Reddy, Vikram Singh

**Internal Status Progression:** `ASSIGNED → IN_TRANSIT → DELIVERED`

---

## 5. ActiveMQ Queue Contract

### Queue: `order.created`

| Property | Value |
|----------|-------|
| **Publisher** | Order Service (`OrderMessagePublisher`) |
| **Consumer** | Camunda Workflow Engine (`OrderMessageListener`) |
| **Purpose** | Triggers start of the Camunda `orderProcess` workflow |
| **Broker** | Embedded (`vm://embedded?broker.persistent=false`) |

**Message Format (JSON String):**
```json
{"orderId": 1}
```

**Consumer Behavior:**
1. Parses `orderId` from message
2. Creates Camunda process variables: `{"orderId": 1}`
3. Calls `runtimeService.startProcessInstanceByKey("orderProcess", businessKey, variables)`
4. Camunda BPMN then orchestrates Payment → Kitchen → Delivery steps

---

## 6. Camunda BPMN Process Key

| Property | Value |
|----------|-------|
| **Process Definition Key** | `orderProcess` |
| **BPMN File** | `order-service/src/main/resources/order-process.bpmn` |
| **Delegates** | `processPaymentDelegate`, `prepareKitchenDelegate`, `assignDeliveryDelegate`, `updateOrderStatusDelegate`, `cancelOrderDelegate` |

---

## 7. Error Handling Strategy

| Scenario | Handling |
|----------|----------|
| Payment failure (FAILED) | Camunda gateway routes to `cancelOrderDelegate`, order marked `CANCELLED` |
| Kitchen service down | `PrepareKitchenDelegate` catches exception, logs error, workflow continues |
| Delivery service down | `AssignDeliveryDelegate` catches exception, logs error, workflow continues |
| Order not found | `OrderService.getOrderById()` throws `RuntimeException` |
| ActiveMQ connection fail | Spring auto-reconnects (embedded broker, always available) |
| Camunda engine error | Logged via `OrderMessageListener` catch block |
