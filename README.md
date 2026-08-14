# Nautix Frontend

A **Cesium-based maritime digital earth platform** built with Vue 3 and TypeScript. Nautix provides a 3D globe for real-time ship monitoring, sensor data visualization, route replay/animation, weather layers, alerting, and AI-assisted analytics — backed by the ShipSensor REST API.

> This is the **frontend branch**. The backend service lives on the backend branch: **ShipSensor** (Spring Boot).

---

## Features

- **3D Maritime Map** — CesiumJS globe with layer selection (map view + layer selector)
- **Real-time Ship Monitoring** — live ship positions and runtime status via WebSocket
- **Sensor Visualization** — realtime sensor values, trend charts (ECharts), sensor dictionary and configuration pages
- **Route Replay** — animated route playback with timeline, speed gauge, and heading indicator
- **Weather Layers** — wind and wave overlays on the globe
- **Alert Management** — alert rules and alert records
- **Domain Pages** — ships, routes, ports, weather regions
- **Analytics & AI** — analytics dashboard and AI-assisted report generation
- **System** — login/register (JWT), operation logs

## Tech Stack

| Category | Technology |
|---|---|
| Framework | Vue 3.5 + TypeScript |
| Build Tool | Vite 8 |
| 3D Globe | CesiumJS |
| Charts | ECharts + vue-echarts |
| UI Components | Element Plus |
| State Management | Pinia |
| HTTP | Axios |
| Routing | Vue Router |

## Project Structure

```
src/
├── router/          # routes: /dashboard /ship /sensor/* /alert/* /route /port
│                    #         /weather /analytics /system/logs /login /register
├── views/           # page-level components
├── components/
│   ├── map/         # MapView, LayerSelector
│   ├── ship/        # ShipReplay, SpeedGauge, HeadingIndicator
│   ├── route/       # RouteAnimation, RouteTimeline
│   ├── weather/     # WindLayer, WaveLayer
│   └── ai/          # ReportGenerator
├── hooks/           # useMap, useWebSocket, useReplay, useSensor, useAI, useChart ...
├── stores/          # Pinia stores
└── api/             # axios wrappers for the ShipSensor API
```

## Getting Started

### Prerequisites

- Node.js 18+
- The ShipSensor backend running (default: `http://localhost:1910/api`)

### 1. Clone and check out the frontend branch

```bash
git clone <your-repo-url>
git checkout frontend   # the branch that holds this app
```

### 2. Install dependencies

```bash
npm install
```

### 3. Configure the API address

`.env.development`:

```
VITE_API_BASE_URL=http://localhost:1910/api
```

Point this to your ShipSensor backend.

### 4. Run in development mode

```bash
npm run dev
```

### 5. Production build

```bash
npm run build    # type-check (vue-tsc) + Vite build into dist/
npm run preview  # serve the built app locally
```

## How It Connects to the Backend

- All requests go to `VITE_API_BASE_URL` (context path `/api` on the ShipSensor service).
- Authentication uses the JWT issued by `/api/auth/login`.
- Real-time ship/sensor data is pushed through WebSocket connections (`useWebSocket` hook).

## Repository Note

This branch is dedicated to the frontend application. The backend Spring Boot service is maintained on the **backend branch** of the same repository. Some legacy stub files at the repo root (`ai.ts`, `fleet.ts`, `replay.ts`, etc.) are empty placeholders and are kept for reference only; `export-db.cjs` is a utility script that exports the `ship_sensor` MySQL database to SQL in cursor-based batches.
