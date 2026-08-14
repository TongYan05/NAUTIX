import request from './axios'
import type { PageResult, SysOperationLog } from './types'

export function getLogPage(params: {
  page?: number
  size?: number
  userName?: string
  targetTable?: string
}) {
  return request.get<unknown, PageResult<SysOperationLog>>('/sysOperationLog/page', { params })
}

export function getAllLogs() {
  return request.get<unknown, SysOperationLog[]>('/sysOperationLog')
}

export function getLogById(id: number) {
  return request.get<unknown, SysOperationLog>(`/sysOperationLog/${id}`)
}

export function deleteLog(id: number) {
  return request.delete<unknown, boolean>(`/sysOperationLog/${id}`)
}
