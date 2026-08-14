import request from './axios'
import type { PageResult, Port } from './types'

export function getPortPage(params: {
  page?: number
  count?: number
  keyword?: string
  country?: string
  countryCode?: string
  portType?: string
  sortField?: string
  sortOrder?: string
}) {
  return request.get<unknown, PageResult<Port>>('/port/page', { params })
}

export function getAllPorts() {
  return request.get<unknown, Port[]>('/port/all')
}

export function getPortById(id: number) {
  return request.get<unknown, Port>(`/port/${id}`)
}

export function addPort(data: Port) {
  return request.post<unknown, boolean>('/port', data)
}

export function updatePort(data: Port) {
  return request.put<unknown, boolean>('/port', data)
}

export function deletePort(id: number) {
  return request.delete<unknown, boolean>(`/port/${id}`)
}
