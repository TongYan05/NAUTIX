import request from './axios'

export interface DashboardStats {
  shipCount: number
  alertCount: number
  sensorConfigCount: number
  weatherCount: number
}

export interface AlertStatusSummary {
  status: string
  count: number
}

export interface ShipTypeDistribution {
  type: string
  count: number
}

export const getDashboardStats = () => {
  return request.get<unknown, DashboardStats>('/dashboard/stats')
}

export const getAlertStatusSummary = () => {
  return request.get<unknown, AlertStatusSummary[]>('/dashboard/alert-summary')
}

export const getShipTypeDistribution = () => {
  return request.get<unknown, ShipTypeDistribution[]>('/dashboard/ship-type-dist')
}

export const getAlertHourDistribution = () => {
  return request.get<unknown, { hour: number; count: number }[]>('/dashboard/alert-hour-dist')
}

export const pingBackend = () => {
  return request.get<unknown, { status: string; timestamp: number }>('/public/ping')
}
