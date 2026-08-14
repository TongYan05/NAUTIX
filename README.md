# ShipSensor — Backend

A Spring Boot backend for a **ship sensor monitoring platform**. It provides REST APIs for sensor data management, alerting, ship/route/port information, and dashboard statistics — with JWT authentication, Redis caching, and a built-in data simulator that keeps the platform alive with realistic sensor readings.

> This is the **backend branch**. The front end lives on the frontend branch: **Nautix Frontend** (Vue 3 + Cesium).

---

## Features

- **JWT Authentication** — register/login via Spring Security + jjwt
- **Sensor data management** — CRUD, pagination, trend analysis, volatility analysis
- **Sensor dictionary & configuration** — manage sensor types and per-sensor configs
- **Alert system** — alert rules, alert records, and alert events
- **Maritime domain data** — ships, runtime status, routes, and ports
- **Dashboard** — aggregated statistics, alert summaries, ship-type distribution
- **Support modules** — weather regions, system operation logs
- **Built-in simulator** — scheduled tasks generate realistic sensor data at runtime (can be switched off for production use)
- **Port initialization** — port data is loaded from `ports.xlsx` (EasyExcel) on startup

## Tech Stack

| Category | Technology |
|---|---|
| Language / Framework | Java 17, Spring Boot 3.5 |
| Security | Spring Security + JWT (jjwt) |
| Data Access | MyBatis-Plus 3.5.9 |
| Database | MySQL (`ship_sensor`) |
| Connection Pool | Druid (with monitoring) |
| Cache | Redis (Lettuce) |
| Excel Import | EasyExcel |
| Utilities | Lombok, DataFaker (simulated data generation) |

## Project Structure

```
src/main/java/shipsensor/
├── ShipsensorApplication.java   # entry point
├── controller/                  # REST controllers (context path: /api)
├── entity/                      # MyBatis-Plus entities (SensorData, AlertRule, Route, Port, ...)
├── mapper/                      # MyBatis-Plus mappers
├── service/                     # business logic (IService-based)
├── simulator/
│   ├── generator/               # data generation tasks
│   ├── cache/                   # cache layer for realtime data
│   └── scheduler/               # @Scheduled simulation jobs
└── config/                      # security, JWT, MyBatis-Plus config
src/main/resources/
├── application.yaml             # main configuration
└── data/ports.xlsx              # port seed data
```

## Getting Started

### Prerequisites

- JDK 17+
- MySQL 8.x (database `ship_sensor`)
- Redis
- Maven (or use the bundled `mvnw` wrapper)

### 1. Clone and check out the backend branch

```bash
git clone <your-repo-url>
git checkout backend   # the branch that holds this service
```

### 2. Prepare the database

```sql
CREATE DATABASE ship_sensor DEFAULT CHARACTER SET utf8mb4;
```

### 3. Configure `src/main/resources/application.yaml`

Update at minimum:

- `spring.datasource.*` — MySQL URL / username / password
- `spring.data.redis.*` — Redis host and port
- JWT secret used by Spring Security

### 4. Run

```bash
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

The service starts on **port 1910**; all APIs are served under the context path **`/api`**.

## API Overview

| Controller | Base Path | Purpose |
|---|---|---|
| AuthController | `/api/auth` | Login / register (JWT) |
| SensorDataController | `/api/sensorData` | Sensor data CRUD, `/trend`, `/volatility`, `/page` |
| SensorDictController | `/api/sensorDict` | Sensor type dictionary |
| SensorConfigController | `/api/sensorConfig` | Sensor configuration |
| DashboardController | `/api/dashboard` | Statistics, alert summary, ship-type distribution |
| AlertRuleController | `/api/alertRule` | Alert rule management |
| AlertRecordController | `/api/alertRecord` | Alert record management |
| AlertEventController | `/api/alert-event` | Alert events |
| ShipController / RuntimeController | `/api/...` | Ship info and realtime status |
| RouteController / PortController | `/api/...` | Route and port management |
| WeatherRegionController | `/api/...` | Weather regions |
| SysOperationLogController | `/api/...` | Operation logs |

## Data Simulator

Instead of importing a static SQL dump, the platform ships with a **runtime simulator**:

- `SensorDataGeneratorTask` / `SimulatorScheduler` — `@Scheduled` tasks that generate sensor readings continuously
- `AlertDataGenerator` — produces alert data for testing the alert pipeline
- `PortInitializer` — seeds port data from `data/ports.xlsx` at startup

Toggle it in `application.yaml`:

```yaml
simulator:
  enabled: true      # master switch for data simulation
realtime:
  enabled: true      # high-frequency realtime data generation
```

Turn these off when connecting to a live data source.

## Deployment Note

The service was designed to run on a small Linux VM (e.g., Ubuntu on cloud ECS) under `/home/admin/ShipSensor`. Disk space matters — if you import large datasets, stream them (e.g., `zcat dump.sql.gz | mysql ...`) and check free space with `df -h` first.
