import request from './axios'
import type { WeatherRegion } from './types'

export function getAllWeatherRegions() {
  return request.get<unknown, WeatherRegion[]>('/weather')
}

export function getWeatherRegionById(id: number) {
  return request.get<unknown, WeatherRegion>(`/weather/${id}`)
}

export function addWeatherRegion(data: WeatherRegion) {
  return request.post<unknown, boolean>('/weather', data)
}

export function updateWeatherRegion(data: WeatherRegion) {
  return request.put<unknown, boolean>('/weather', data)
}

export function deleteWeatherRegion(id: number) {
  return request.delete<unknown, boolean>(`/weather/${id}`)
}
