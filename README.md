# FoodFlow – Online Food Order Processing System

> A production-ready microservices system built with **Spring Boot**, **Camunda BPMN**, **ActiveMQ**, and **React**.

[![Java](https://img.shields.io/badge/Java-17-orange)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-61dafb)](https://reactjs.org/)
[![Camunda](https://img.shields.io/badge/Camunda-7.19-CC0000)](https://camunda.com/)

---

## 🏗️ Architecture

```
React UI (Vite)
    │
    │ REST (POST /api/orders, GET /api/orders)
    ▼
Order Service (Port 8080)
    │  ┌──────────────────────────────────┐
    │  │  Camunda BPMN Workflow Engine     │
    │  │  (Embedded inside Order Service)  │
    ├─►│                                  │
    │  │  Step 1 ──► Payment Service (8081)│
    │  │  Step 2 ──► Kitchen Service (8082)│
    │  │  Step 3 ──► Delivery Service(8083)│
    │  └──────────────────────────────────┘
    │
    │ ActiveMQ (Embedded, vm://embedded)
    │ Queue: order.created
    │
    └── H2 In-Memory Database (per service)
```

## 🔄 Order Lifecycle

```
PLACED → PAYMENT_PROCESSING → KITCHEN_PREP → OUT_FOR_DELIVERY → DELIVERED
                          └→ CANCELLED (on payment failure)
```

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React 18, Vite, Framer Motion, Lucide Icons |
| Backend | Spring Boot 2.7.18, Java 17 |
| Workflow | Camunda BPM 7.19 (embedded) |
| Messaging | Apache ActiveMQ (embedded, `vm://embedded`) |
| Database | H2 In-Memory (per service, local) |
| Deployment | Render (Docker), Vercel (Frontend) |

---

## 🚀 Running Locally (No Docker Needed)

### Prerequisites
- Java 17 JDK → [Download](https://adoptium.net/)
- Node.js 18+ → [Download](https://nodejs.org/)

### Step 1: Start all 4 backend services

Open 4 separate terminals in the project root:

```powershell
# Terminal 1 – Order Service (Port 8080)
.\mvnw.cmd spring-boot:run -pl order-service

# Terminal 2 – Payment Service (Port 8081)
.\mvnw.cmd spring-boot:run -pl payment-service

# Terminal 3 – Kitchen Service (Port 8082)
.\mvnw.cmd spring-boot:run -pl kitchen-service

# Terminal 4 – Delivery Service (Port 8083)
.\mvnw.cmd spring-boot:run -pl delivery-service
```

> ⚡ First run downloads all Maven dependencies (~3-5 minutes). Subsequent runs are fast.

### Step 2: Start the Frontend

```powershell
cd frontend
npm install
npm run dev
```

Open **http://localhost:5173** in your browser.

---

## 📡 API Endpoints

### Order Service (`http://localhost:8080`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/orders` | Place a new order |
| `GET` | `/api/orders` | Get all orders (used by dashboard) |
| `GET` | `/api/orders/{id}` | Get single order by ID |

**POST /api/orders – Request Body:**
```json
{
  "customerName": "John Doe",
  "item": "Butter Chicken",
  "amount": 15.99
}
```

**POST /api/orders – Response (HTTP 201):**
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

### Payment Service (`http://localhost:8081`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/payments` | Process payment (called by Camunda) |

### Kitchen Service (`http://localhost:8082`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/kitchen/tickets` | Create kitchen ticket (called by Camunda) |

### Delivery Service (`http://localhost:8083`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/deliveries` | Assign delivery (called by Camunda) |

---

## ☁️ Deploying to Render (Backend)

1. Push to GitHub:
   ```bash
   git push origin main
   ```

2. Go to [render.com](https://render.com) → **New Blueprint Instance**

3. Connect this repository — Render reads `render.yaml` and auto-creates all 4 services

4. Each service will build via its own `Dockerfile.*` file

> ⚠️ Free tier (512MB RAM) is tight for Spring Boot + Camunda. Set `JAVA_TOOL_OPTIONS=-Xmx400m` as env var or upgrade to Starter ($7/mo).

---

## 🗃️ Project Structure

```
online-food-order/
├── order-service/          # Main service: REST API + Camunda + ActiveMQ publisher
│   ├── src/main/java/
│   │   └── com/foodorder/orderservice/
│   │       ├── controller/   # REST endpoints
│   │       ├── service/      # Business logic
│   │       ├── delegate/     # Camunda JavaDelegate implementations
│   │       ├── jms/          # ActiveMQ publisher & listener
│   │       ├── model/        # JPA entities
│   │       ├── dto/          # Request/Response DTOs
│   │       └── repository/   # Spring Data JPA repos
│   └── src/main/resources/
│       ├── application.yml   # Service config (H2, embedded ActiveMQ, Camunda)
│       └── order-process.bpmn # Camunda BPMN workflow definition
│
├── payment-service/        # Payment processing microservice (Port 8081)
├── kitchen-service/        # Kitchen preparation microservice (Port 8082)
├── delivery-service/       # Delivery assignment microservice (Port 8083)
│
├── frontend/               # React + Vite frontend
│   └── src/
│       ├── App.jsx           # Main app with polling logic
│       ├── components/
│       │   ├── OrderForm.jsx      # Order placement form
│       │   └── OrderDashboard.jsx # Real-time order status dashboard
│       └── index.css         # Premium dark-theme styles
│
├── docs/                   # Submission deliverables
│   ├── API_LLD.md
│   ├── DATABASE_DESIGN.md
│   ├── LOG_OUTPUT_SAMPLE.md
│   └── IMPLEMENTATION_REPORT.md
│
├── Dockerfile.order        # Docker build for order-service
├── Dockerfile.payment      # Docker build for payment-service
├── Dockerfile.kitchen      # Docker build for kitchen-service
├── Dockerfile.delivery     # Docker build for delivery-service
├── render.yaml             # Render deployment blueprint
├── vercel.json             # Vercel frontend deployment config
└── pom.xml                 # Parent Maven POM
```

---

## 📋 Submission Deliverables

| Deliverable | File |
|-------------|------|
| API Low-Level Design | [docs/API_LLD.md](docs/API_LLD.md) |
| Database Design | [docs/DATABASE_DESIGN.md](docs/DATABASE_DESIGN.md) |
| Log Output Sample | [docs/LOG_OUTPUT_SAMPLE.md](docs/LOG_OUTPUT_SAMPLE.md) |
| Implementation Report | [docs/IMPLEMENTATION_REPORT.md](docs/IMPLEMENTATION_REPORT.md) |

---

## 📊 Expected Log Output (End-to-End Flow)

```
[OrderService]   Order #1 - Status: PLACED, Workflow started
[PaymentService] Order #1 - Payment processing... SUCCESS
[KitchenService] Order #1 - Kitchen ticket created, preparing food... READY
[DeliveryService] Order #1 - Driver assigned, delivering... DELIVERED
[OrderService]   Order #1 - Workflow COMPLETE
```
