import request from './axios'
import type {
  PageResult,
  SensorConfig,
  SensorData,
  SensorDict,
} from './types'

/* ---------- Sensor Data ---------- */

export function getSensorDataPage(params: {
  page?: number
  size?: number
  shipId?: number
  startTime?: string
  endTime?: string
}) {
  return request.get<unknown, PageResult<SensorData>>('/sensorData/page', { params })
}

export function getSensorDataList() {
  return request.get<unknown, SensorData[]>('/sensorData/list')
}

export function addSensorData(data: SensorData) {
  return request.post<unknown, boolean>('/sensorData', data)
}

export function getSensorTrendData(limit: number = 60) {
  return request.get<unknown, SensorData[]>('/sensorData/trend', { params: { limit } })
}

export function addSensorDataBatch(data: SensorData[]) {
  return request.post<unknown, boolean>('/sensorData/batch', data)
}

export function updateSensorData(data: SensorData) {
  return request.put<unknown, boolean>('/sensorData', data)
}

export function deleteSensorData(id: number) {
  return request.delete<unknown, boolean>(`/sensorData/${id}`)
}

export interface VolatilityStat {
  configId: number
  cnt: number
  stdDev: number | null
  mean: number | null
}

export function getSensorVolatility(scan = 300000) {
  return request.get<unknown, VolatilityStat[]>('/sensorData/volatility', { params: { scan } })
}

/* ---------- Sensor Dictionary ---------- */

export function getSensorDictPage(params: {
  page?: number
  count?: number
  keyword?: string
  sortField?: string
  sortOrder?: string
}) {
  return request.get<unknown, PageResult<SensorDict>>('/sensorDict/page', { params })
}

export function getAllSensorDicts() {
  return request.get<unknown, SensorDict[]>('/sensorDict/list')
}

export function getSensorDictById(id: number) {
  return request.get<unknown, SensorDict>(`/sensorDict/${id}`)
}

export function addSensorDict(data: SensorDict) {
  return request.post<unknown, boolean>('/sensorDict', data)
}

export function updateSensorDict(data: SensorDict) {
  return request.put<unknown, boolean>('/sensorDict', data)
}

export function deleteSensorDict(id: number) {
  return request.delete<unknown, boolean>(`/sensorDict/${id}`)
}

/* ---------- Sensor Configuration ---------- */

export function getSensorConfigPage(params: {
  page?: number
  count?: number
  shipId?: number
  typeId?: number
  sensorName?: string
  status?: number
  sortField?: string
  sortOrder?: string
}) {
  return request.get<unknown, PageResult<SensorConfig>>('/sensorConfig/list', { params })
}

export function getAllSensorConfigs() {
  return request.get<unknown, SensorConfig[]>('/sensorConfig')
}

export function getSensorConfigById(id: number) {
  return request.get<unknown, SensorConfig>(`/sensorConfig/${id}`)
}

export function addSensorConfig(data: SensorConfig) {
  return request.post<unknown, boolean>('/sensorConfig', data)
}

export function updateSensorConfig(data: SensorConfig) {
  return request.put<unknown, boolean>('/sensorConfig', data)
}

export function deleteSensorConfig(id: number) {
  return request.delete<unknown, boolean>(`/sensorConfig/${id}`)
}
