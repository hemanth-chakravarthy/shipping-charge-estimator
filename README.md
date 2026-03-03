# B2B Shipping Charge Estimator

A production-quality **Spring Boot 3.2 REST API** for estimating B2B shipping charges on a Kirana store marketplace.

---

## Quick Start

### Prerequisites
- **Java 17+** (tested with Java 22)
- **Maven 3.9+**

### Run

```bash
# Clone/navigate to project
cd "d:\Web Development\shipping-charge-estimator"

# Build
mvn package -DskipTests

# Run
java -jar target/estimator-1.0.0.jar
```

Server starts on **port 8082**.

---

## API Endpoints

### Base URL: `http://localhost:8082/api/v1`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/data` | List all seeded entity IDs (for testing) |
| GET | `/warehouse/nearest` | Find nearest warehouse for a seller |
| GET | `/shipping-charge` | Calculate shipping charge (warehouse to customer) |
| POST | `/shipping-charge/calculate` | Combined: find warehouse + calculate charge |

### 1. Get Seeded IDs
```bash
curl http://localhost:8082/api/v1/data
```

### 2. Get Nearest Warehouse
```bash
curl "http://localhost:8082/api/v1/warehouse/nearest?sellerId=<sellerId>&productId=<productId>"
```

**Response:**
```json
{
  "warehouseId": "7be583a4-11a6-40f8-93ef-ece0831ed2e8",
  "warehouseLocation": { "lat": 28.7041, "lon": 77.1025 }
}
```

### 3. Get Shipping Charge (Warehouse to Customer)
```bash
curl "http://localhost:8082/api/v1/shipping-charge?warehouseId=<warehouseId>&customerId=<customerId>&deliverySpeed=standard"
```

**Response:**
```json
{ "shippingCharge": 871.68 }
```

### 4. Combined Calculation
```bash
curl -X POST http://localhost:8082/api/v1/shipping-charge/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "sellerId": "<sellerId>",
    "customerId": "<customerId>",
    "deliverySpeed": "express"
  }'
```

**Response:**
```json
{
  "shippingCharge": 2158.0,
  "nearestWarehouse": {
    "warehouseId": "7be583a4-11a6-40f8-93ef-ece0831ed2e8",
    "warehouseLocation": { "lat": 28.7041, "lon": 77.1025 }
  }
}
```

---

## Testing Tools

| Tool | URL |
|------|-----|
| **Swagger UI** | http://localhost:8082/swagger-ui.html |
| **H2 Console** | http://localhost:8082/h2-console |
| **OpenAPI JSON** | http://localhost:8082/api-docs |

**H2 Connection:**
- JDBC URL: `jdbc:h2:mem:shippingdb`
- Username: `sa`
- Password: *(empty)*

### H2 Console - Sample Queries

Open the H2 Console, connect with the credentials above, and run these SQL queries to inspect the seeded data:

```sql
-- See all sellers
SELECT * FROM SELLERS;
```
```
LATITUDE    LONGITUDE   ID                                      NAME
28.6139     77.209      20211748-7b12-4ea7-ae72-6a300fbbdc88   Sharma Traders
19.076      72.8777     bf23159d-1334-45f0-9106-ff7ce55bd9c5   Mumbai Distributors
13.0827     80.2707     36bbd679-f2df-47e6-bcd2-d29d18a1e5ae   Chennai Wholesale
```

```sql
-- See all warehouses
SELECT * FROM WAREHOUSES;
```
```
LATITUDE    LONGITUDE   ID                                      NAME
28.7041     77.1025     a5665fde-370c-4814-bf89-e212f431d499   North Warehouse - Delhi
19.1136     72.8697     cd05a3a8-94b7-4e31-a685-3e774840df44   West Warehouse - Mumbai
12.9716     77.5946     5afd001a-0b6d-4e1d-9051-0b40ee03f81e   South Warehouse - Bangalore
```

```sql
-- See all customers
SELECT * FROM CUSTOMERS;
```
```
LATITUDE    LONGITUDE   ID                                      NAME                    PHONE_NUMBER
26.8467     80.9462     373a6a4a-08b4-44a3-949a-6765d4e90adb   Rajesh Kirana Store     +91-9876543210
23.0225     72.5714     d1352321-b185-4c47-9913-91f15f4411b7   Suresh General Store    +91-9123456789
17.385      78.4867     9ce009da-d53c-4bcb-ac95-212f4bbf717a   Priya Provision Store   +91-9988776655
```

```sql
-- See all products with seller name
SELECT p.name, p.weight_kg, s.name AS seller
FROM PRODUCTS p JOIN SELLERS s ON p.seller_id = s.id;
```
```
NAME                WEIGHT_KG   SELLER
Rice (25kg bag)     25.0        Sharma Traders
Cooking Oil (10L)   9.5         Mumbai Distributors
Sugar (50kg bag)    50.0        Chennai Wholesale
```

> Note: IDs are regenerated on every restart since the database is in-memory.

---

## Pricing Rules

| Distance | Transport Mode | Rate |
|:---------|:--------------|:-----|
| 0 - 99 km | Mini Van | Rs 3 / km / kg |
| 100 - 499 km | Truck | Rs 2 / km / kg |
| 500+ km | Aeroplane | Rs 1 / km / kg |

**Formula:**
```
total = (distance x rate x weight) + Rs 10 (standard fee)
        [+ Rs 1.2 x weight  if deliverySpeed = "express"]
```
Result is rounded to 2 decimal places.

---

## Pre-loaded Sample Data

| Entity | Name | Location |
|--------|------|----------|
| Seller | Sharma Traders | Delhi |
| Seller | Mumbai Distributors | Mumbai |
| Seller | Chennai Wholesale | Chennai |
| Warehouse | North Warehouse | Delhi |
| Warehouse | West Warehouse | Mumbai |
| Warehouse | South Warehouse | Bangalore |
| Customer | Rajesh Kirana Store | Lucknow |
| Customer | Suresh General Store | Ahmedabad |
| Customer | Priya Provision Store | Hyderabad |

---

## Running Tests

```bash
mvn test
```

**Tests: 15 unit tests (all passing)**
- `GeoDistanceUtilTest` — Haversine formula (5 tests)
- `TransportStrategyTest` — Distance slabs & pricing logic (10 tests)

---

## Docker

```bash
# Build JAR first
mvn package -DskipTests

# Build image
docker build -t shipping-estimator .

# Run
docker run -p 8082:8082 shipping-estimator
```

---

## Architecture

```text
Controller Layer  (REST endpoints)
       |
Service Layer     (Business logic, validation)
       |
Repository Layer  (Spring Data JPA)
       |
H2 Database       (In-memory)
```

**Design Patterns:**
- **Strategy Pattern** — Transport mode pricing (MiniVan / Truck / Aeroplane)
- **Factory Pattern** — `TransportStrategyFactory` selects strategy by distance
- **Global Exception Handler** — Consistent JSON error responses, no stack traces

---

## Project Structure

```text
src/main/java/com/shipping/estimator/
├── ShippingEstimatorApplication.java
├── controller/
│   ├── WarehouseController.java
│   ├── ShippingController.java
│   └── DataController.java
├── service/
│   ├── WarehouseService.java
│   └── ShippingService.java
├── repository/
│   ├── SellerRepository.java
│   ├── CustomerRepository.java
│   ├── WarehouseRepository.java
│   └── ProductRepository.java
├── entity/
│   ├── Seller.java, Customer.java
│   ├── Warehouse.java, Product.java
├── strategy/
│   ├── TransportPricingStrategy.java
│   ├── MiniVanStrategy.java
│   ├── TruckStrategy.java
│   ├── AeroplaneStrategy.java
│   └── TransportStrategyFactory.java
├── dto/
│   ├── NearestWarehouseResponse.java
│   ├── ShippingChargeResponse.java
│   ├── CombinedShippingRequest.java
│   ├── CombinedShippingResponse.java
│   └── WarehouseLocation.java
├── exception/
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
├── util/
│   └── GeoDistanceUtil.java  <- Haversine formula
└── config/
    └── DataLoader.java       <- Seeds sample data
```

---

## Assumptions & Design Decisions

1. **GET /shipping-charge** uses a default weight of 1kg when no `productId` is passed. Use the POST endpoint for precise per-product pricing.
2. **Haversine formula** is used for all distance calculations — accurate to +-0.5%.
3. **Transport boundary `100km` uses Truck** (not MiniVan) — `distance < 100` uses MiniVan; `distance >= 100` uses Truck.
4. **H2 is in-memory** — all data resets on restart. Ready for PostgreSQL with an env variable change.
5. **No authentication** — APIs are public for evaluation purposes.
