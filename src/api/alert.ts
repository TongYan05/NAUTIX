import request from './axios'
import type {
  PageResult,
  AlertEvent,
  AlertRecord,
  AlertRule,
} from './types'

/* ---------- Alert Rules ---------- */

export function getAlertRulePage(params: {
  page?: number
  size?: number
  ruleName?: string
  alertLevel?: number
}) {
  return request.get<unknown, PageResult<AlertRule>>('/alertRule/page', { params })
}

export function getAllAlertRules() {
  return request.get<unknown, AlertRule[]>('/alertRule/list')
}

export function getAlertRuleById(id: number) {
  return request.get<unknown, AlertRule>(`/alertRule/${id}`)
}

export function addAlertRule(data: AlertRule) {
  return request.post<unknown, boolean>('/alertRule', data)
}

export function updateAlertRule(data: AlertRule) {
  return request.put<unknown, boolean>('/alertRule', data)
}

export function deleteAlertRule(id: number) {
  return request.delete<unknown, boolean>(`/alertRule/${id}`)
}

/* ---------- Alert Records ---------- */

export function getAlertRecordPage(params: {
  page?: number
  size?: number
  shipId?: number
  handleStatus?: number
}) {
  return request.get<unknown, PageResult<AlertRecord>>('/alertRecord/page', { params })
}

export function getAllAlertRecords() {
  return request.get<unknown, AlertRecord[]>('/alertRecord/list')
}

export function getAlertRecordById(id: number) {
  return request.get<unknown, AlertRecord>(`/alertRecord/${id}`)
}

export function addAlertRecord(data: AlertRecord) {
  return request.post<unknown, boolean>('/alertRecord', data)
}

export function updateAlertRecord(data: AlertRecord) {
  return request.put<unknown, boolean>('/alertRecord', data)
}

export function deleteAlertRecord(id: number) {
  return request.delete<unknown, boolean>(`/alertRecord/${id}`)
}

/* ---------- Alert Events ---------- */

export function getAllAlertEvents() {
  return request.get<unknown, AlertEvent[]>('/alert-event')
}

export function getAlertEventById(id: number) {
  return request.get<unknown, AlertEvent>(`/alert-event/${id}`)
}

export function addAlertEvent(data: AlertEvent) {
  return request.post<unknown, boolean>('/alert-event', data)
}

export function updateAlertEvent(data: AlertEvent) {
  return request.put<unknown, boolean>('/alert-event', data)
}

export function deleteAlertEvent(id: number) {
  return request.delete<unknown, boolean>(`/alert-event/${id}`)
}
