# NAUTIX — Maritime Sensor Monitoring Platform

> Full-stack fleet telemetry: real-time sensors, alerting, routes & ports, weather, analytics, bilingual UI (中文 / English) and an offline AI assistant — plus a built-in data simulator that keeps 250M+ rows flowing.
![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green?logo=springboot)
![Vue](https://img.shields.io/badge/Vue-3.5-brightgreen?logo=vue.js)
![MySQL](https://img.shields.io/badge/MySQL-8-blue?logo=mysql)
![Vite](https://img.shields.io/badge/Vite-8-purple?logo=vite)
![License](https://img.shields.io/badge/language-中文%20%7C%20English-lightgrey)

| | |
|---|---|
| **Repository layout** | Backend (`shipsensor`, Spring Boot) lives on branch **`main`**; frontend (`nautix-frontend`, Vue 3) lives on branch **`frontend`** |
| **API base** | `http://localhost:1910/api` |
| **Web UI** | `http://localhost:5173` (dev) |

---

## Why this project

A ship-owner's operations desk, built end to end: ten thousand simulated vessels stream engine, fuel, electrical, GPS and weather telemetry into MySQL; rules turn abnormal readings into alerts; dashboards, analytics and a natural-language assistant make the whole estate observable in one screen — and the entire UI (assistant included) switches between Chinese and English with one click.

## Highlights

### Bilingual by design (中文 / English)
- One-click **中 / EN** switch in the top bar (also on the login/register pages and inside the assistant panel); the choice persists across sessions
- Every page, table header, dialog, pagination control and Element Plus built-in text is localized via a typed i18n dictionary (`src/i18n/`, ~200 keys)
- The AI assistant **answers in the active language**: same data, English or Chinese wording, including localized ship types, sensor names and 30+ weather categories

### Embedded AI assistant (`/api/ai`)
- A floating orb on **every page** — click to summon, no separate module or route
- **Grounded in the real database**: ship counts, live status, sensor readings, alerts, rules, routes, ports and sea weather are answered by intent-routed SQL queries — never hallucinated, fully offline, no external LLM
- **Also handles daily life**: current time/date/weekday, arithmetic (`128 × 7 → 896`), unit conversion (km↔mi, ℃↔℉, kg↔lb…), BMI and ~20 health/lifestyle topics — with word-boundary intent routing so business and casual questions never collide
- Follow-up suggestions after every answer; friendly recovery on expired sessions

### Production-minded data layer
- Built-in **simulator**: 17 typed generators (GPS, speed, heading, engine RPM/temp/pressure, exhaust, fuel flow/level, power, battery, current, vibration, sea temperature, humidity, pressure…) with route following and cross-sensor correlation; a low-frequency realtime drip keeps live charts moving
- Scales: `sensor_data` holds **250M+ rows** — every assistant/statistics query runs over indexed conditions with strict `LIMIT`s; row counts use `information_schema` estimates
- JWT authentication end to end: expired tokens return proper **401**, the SPA clears the session and re-guides to login — no silent blank pages
- Operation-log audit trail; ports seeded from Excel (EasyExcel) on first boot

### Visual system
- Dark "deep-sea glass" theme: glassmorphism sidebar with gradient active states, glowing focus rings, gradient primary actions, pill pagination, page-transition fades
- Dashboard with live trend / alert-pie / ship-type distribution charts (ECharts, language-aware re-render), card hover lift throughout

---

## Tech stack

### Backend (`main` branch — `shipsensor/`)
| Concern | Choice |
|---|---|
| Language / framework | Java 17 · Spring Boot 3.5.16 |
| Security | Spring Security + JWT (jjwt 0.12.6), stateless sessions, 401/403 entry points |
| ORM / DB | MyBatis-Plus 3.5.9 · MySQL 8 (`ship_sensor`) · Druid pool |
| AI assistant | Rule-based intent router + real-SQL answers (`AiAssistantServiceImpl`, `DailyLifeAssistant`, bilingual `AiText`) |
| Utilities | Lombok, DataFaker, EasyExcel + POI |
| Build | Maven (wrapper included: `mvnw` / `mvnw.cmd`) |

### Frontend (`frontend` branch — `nautix-frontend/`)
| Concern | Choice |
|---|---|
| Framework | Vue 3.5 · TypeScript · Vite 8 |
| UI kit | Element Plus 2.14 (+ icons), dark maritime theme |
| Charts | ECharts 6 (vue-echarts) · Cesium available for 3D earth views |
| State / routing | Pinia 4 · Vue Router 4 (token + expiry guard) |
| i18n | Zero-dependency typed dictionary (`src/i18n`) + Pinia lang store + `el-config-provider` locale binding |
| HTTP | Axios instance with JWT interceptor, 401/403 auto re-login, 30 s timeout |

---

## Feature map

| Module | What it does |
|---|---|
| **Dashboard** | Fleet/alert/sensor/weather KPIs, sensor trend, alert status pie, ship-type distribution, latest alerts — auto-refresh, language-aware |
| **Ships** | 10k-vessel registry: full CRUD, search by name/IMO/port/company, sortable columns |
| **Sensor center** | Realtime stream view (10 s auto refresh, ship & time filters, stats cards + trend chart), per-vessel configuration CRUD, sensor-type dictionary (16 codes) |
| **Alerts** | Records (filter by ship/status, mark handled), 68 seeded rules with severity levels, threshold/operators/duration editor, carousel rule cards with stats header |
| **Routes & ports** | 100k routes / 3.8k ports: CRUD, origin/destination filters, distance sort |
| **Weather** | 1,000 sea regions: temperature/wind/wave/humidity/pressure cards with localized condition labels and severity colors, infinite scroll |
| **Analytics** | Alert hourly distribution, operator share, sensor volatility scatter, longest-route top 10 |
| **AI assistant** | Floating orb, bilingual Q&A over live data + daily-life engine, suggested follow-ups |
| **Auth & system** | Register/login (JWT), operation-log viewer, protected-route guard with expiry check |

## Backend REST surface

All endpoints under **`/api`** (port 1910). Everything except `/auth/**` and `/public/**` requires `Authorization: Bearer <token>`.

| Base path | Purpose |
|---|---|
| `/auth` · `/public` | register / login · health ping (open) |
| `/ai` | assistant: `POST /ai/chat` `{message, lang}`, `GET /ai/suggestions?lang=` |
| `/dashboard` | aggregate stats, alert summary, ship-type & hourly distributions |
| `/shipInfo` `/runtime` | ships CRUD/page · realtime status per vessel |
| `/sensorData` `/sensorConfig` `/sensorDict` | readings page/trend/volatility · configs · type dictionary |
| `/alertRule` `/alertRecord` `/alert-event` | threshold rules · records · event windows |
| `/route` `/route-point` `/port` `/weather` | routes · points · ports · sea weather regions |
| `/sysOperationLog` | audit trail |

## Data simulator

Switches in `application.yaml` (current deployment runs the low-freq drip only):

```yaml
simulator:
  enabled: false            # master switch for bulk generation
  sensor-data:
    interval: 1000          # ms between bulk batches
    batch-size: 50000
    initializer:            # one-time seeders (routes / runtime / alerts)
      route-enabled: false
  realtime:
    enabled: true           # low-frequency live drip (independent switch)
    interval-seconds: 10
    per-tick: 10            # ≈ 86k rows/day at defaults
```

---

## Getting started

### Prerequisites
- JDK 17+ (dev machine runs Temurin/OpenJDK; 21/25 also verified)
- MySQL 8.x, Node.js 20+ (22 LTS recommended)

### 1 · Database
```sql
CREATE DATABASE IF NOT EXISTS ship_sensor CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```
Tables are created/seeded automatically on first boot (dictionary, alerts, routes, weather, runtime states; ports import from `resources/data/ports.xlsx`).

### 2 · Backend
```bash
git clone https://github.com/TongYan05/NAUTIX.git
cd NAUTIX                              # branch: main
# adjust spring.datasource.* and jwt.secret-key in src/main/resources/application.yaml if needed
./mvnw spring-boot:run                 # Windows: mvnw.cmd spring-boot:run
```
API at `http://localhost:1910/api` — health check: `GET /api/public/ping`.

### 3 · Frontend
```bash
git clone -b frontend https://github.com/TongYan05/NAUTIX.git nautix-frontend
cd nautix-frontend
npm install
npm run dev                            # http://localhost:5173
```
`.env.development` and `src/api/axios.ts` both target `http://localhost:1910/api` (the axios instance is the source of truth). Production build: `npm run build` (includes `vue-tsc` type check) → serve `dist/` behind Nginx, proxying `/api` to the backend.

### 4 · Try it
1. Register any account → login
2. Click through Dashboard / Ships / Sensors / Alerts — all bilingual via the top-bar **中 / EN** toggle
3. Press the floating orb (bottom-right) and ask, in English:
   `How many ships are on the platform?` · `What is 128 * 7?` · `How many miles is 5 km?`
   or in 中文：`平台有多少艘船？` · `远洋-00000号现在什么状态？` · `最近天气怎么样`

---

## Project structure (abridged)

```
shipsensor/ (main)
└── src/main/java/shipsensor/
    ├── config/          SecurityConfig (401/403), JWT filter, CORS, exception handler
    ├── controller/      auth · public · ai · dashboard · sensor · alert · ship · system
    ├── service/         domain services + AiAssistantService · AiText (bilingual)
    │   └── impl/        AiAssistantServiceImpl (intent router), DailyLifeAssistant
    ├── entity/ inter/   MyBatis-Plus entities & mappers (14 tables)
    └── simulator/       generators · correlation engine · route navigator · cache · initializers

nautix-frontend/ (frontend)
└── src/
    ├── i18n/            zh/en dictionary (typed keys)
    ├── stores/          lang · ai · auth · ship · sensor · alert … (Pinia)
    ├── api/             axios instance (JWT + 401/403) + per-domain request modules
    ├── layouts/         MainLayout (glass sidebar, EN/中 toggle, route-transition)
    ├── components/      ai/AIAssistantFloat (embedded orb + panel) + domain components
    ├── views/           dashboard · ship · sensor/* · alert/* · route · port · weather ·
    │                    analytics · system/logs · auth/*  (+ prototype modules)
    └── router/          guards with JWT expiry check
```

## Notes
- Demo data is simulated; ship names, ports and metrics are synthetic but internally consistent.
- The AI assistant intentionally uses a local rule engine: deterministic, auditable, works without internet, and cannot invent data.
- Designed for classroom/portfolio use across CN & AU: one toggle switches the whole product's language.

## Author
**Yan Tong** — [@TongYan05](https://github.com/TongYan05)
