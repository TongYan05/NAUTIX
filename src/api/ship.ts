import request from './axios'
import type {
  PageResult,
  ShipInfo,
  Route,
  RoutePoint,
  ShipRuntimeStatus,
} from './types'

/* ---------- Ship Information ---------- */

export function getShipPage(params: {
  page?: number
  count?: number
  keyword?: string
  shipStatus?: string
  shipType?: string
  registryPort?: string
  sortField?: string
  sortOrder?: string
}) {
  return request.get<unknown, PageResult<ShipInfo>>('/shipInfo/page', { params })
}

export function getAllShips() {
  return request.get<unknown, ShipInfo[]>('/shipInfo/all')
}

export function getShipById(id: number) {
  return request.get<unknown, ShipInfo>(`/shipInfo/${id}`)
}

export function addShip(data: ShipInfo) {
  return request.post<unknown, boolean>('/shipInfo', data)
}

export function updateShip(data: ShipInfo) {
  return request.put<unknown, boolean>('/shipInfo', data)
}

export function deleteShip(id: number) {
  return request.delete<unknown, boolean>(`/shipInfo/${id}`)
}

/* ---------- Runtime Status ---------- */

export function getAllRuntimeStatus() {
  return request.get<unknown, ShipRuntimeStatus[]>('/runtime')
}

export function getRuntimeByShip(shipId: number) {
  return request.get<unknown, ShipRuntimeStatus>(`/runtime/${shipId}`)
}

/* ---------- Routes ---------- */

export function getRoutePage(params: {
  page?: number
  count?: number
  keyword?: string
  startPortId?: number
  endPortId?: number
  sortField?: string
  sortOrder?: string
}) {
  return request.get<unknown, PageResult<Route>>('/route/page', { params })
}

export function getRouteById(id: number) {
  return request.get<unknown, Route>(`/route/${id}`)
}

export function addRoute(data: Route) {
  return request.post<unknown, boolean>('/route', data)
}

export function updateRoute(data: Route) {
  return request.put<unknown, boolean>('/route', data)
}

export function deleteRoute(id: number) {
  return request.delete<unknown, boolean>(`/route/${id}`)
}

/* ---------- Route Points ---------- */

export function getAllRoutePoints() {
  return request.get<unknown, RoutePoint[]>('/route-point')
}

export function getRoutePointById(id: number) {
  return request.get<unknown, RoutePoint>(`/route-point/${id}`)
}
