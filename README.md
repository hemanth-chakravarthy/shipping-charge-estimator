# Enterprise Logistics Hub (V3.5)

A production-ready **Spring Boot 3.2** & **React (Vite)** full-stack application for estimating B2B shipping charges. Originally a simple estimator, this project has evolved into a comprehensive logistics engine featuring dynamic volumetric weight calculations, multi-item bulk shipping, a minimalist "Noir" UI, and PDF invoicing.

---

## Key Features

* **Advanced Calculation Engine**: Computes shipping costs using tiered distance rates, volumetric weight logic (`L x W x H / 5000`), fuel surcharges (5%), and handling fees.
* **Multi-Item Cart System**: Users can build complex orders with varying products, quantities, and service levels (Standard/Express).
* **Geospatial Optimization**: Calculates the nearest warehouse to a seller using the Haversine formula and provides real-time route visualization via the **OSRM API** and **Leaflet**.
* **Financial Transparency**: Clear UI and PDF breakdown of Base Shipping, Surcharges, Product Subtotals, and Grand Totals.
* **Hardened Backend**: Features a robust Service layer, DTO mapping (`MasterDataResponse`), input validation (`@Positive`, `@NotBlank`), and a Global Exception Handler.
* **Modern Frontend**: Built with React, Vite, and **Zustand** for global state management, styled with a premium "Noir" aesthetic.
* **Stateless Security**: Integrated **JWT Authentication** for secure API access.

---



## Quick Start

### Prerequisites
- **Java 22** (or 17+)
- **Maven 3.9+**
- **Node.js 18+**
- **PostgreSQL** (Docker recommended)

### 1. Database Setup
Ensure PostgreSQL is running locally on port `5432` with a database named `shipping_estimator`.
*(Alternatively, use the provided `docker-compose.yml` if available).*

### 2. Backend (Spring Boot)
```bash
# Clone/navigate to project
cd shipping-charge-estimator

# Build the jar
mvn clean package -DskipTests

# Run the server
java -jar target/estimator-1.0.0.jar
```
*The Spring Boot server will start on **port 8082**.*
*Note: The `DataLoader` automatically wipes and re-seeds 65+ products, warehouses, and an `admin`/`password` user on startup.*

### 3. Frontend (React / Vite)
```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```
*The React app will start on **port 5173** (or 5174).*

---

##  Tech Stack

### Backend
- **Java 22** & **Spring Boot 3.2.3**
- **PostgreSQL** (Data Persistence)
- **Spring Security & JWT** (Stateless Auth)
- **OpenPDF** (Invoice Generation)
- **Lombok** & **Jakarta Validation**

### Frontend
- **React 18** & **Vite**
- **Zustand** (State Management)
- **Leaflet & OSRM** (Map & Routing)
- **Recharts** (Cost Analytics)
- **Lucide React** (Icons)

---

## Testing

```bash
mvn test
```
Includes unit tests for geospatial calculations (`GeoDistanceUtilTest`) and pricing strategies.

---

## Docker Deployment

To run the application entirely in containers:
```bash
docker-compose up -d --build
```
*(Ensure you update CORS settings in `SecurityConfig.java` and environment variables for production).*

---

## Contact

<div align="center">

[![Email](https://img.shields.io/badge/Email-D14836?style=for-the-badge&logo=gmail&logoColor=white)](mailto:khchakri@gmail.com)
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/hemanth-chakravarthy)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/hemanth-chakravarthy-kancharla-a27b87357)
[![Portfolio](https://img.shields.io/badge/Portfolio-FF5722?style=for-the-badge&logo=todoist&logoColor=white)](https://hemanth-chakravarthy.netlify.app/)

</div>
