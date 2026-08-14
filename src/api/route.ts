import request from './axios'
import type { PageResult, Route } from './types'

/* Fetch routes with paging (GET /route/page).
 * The legacy GET /route endpoint does not exist on the backend. */
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

/* Top N routes by distance, for the Analytics chart */
export function getTopRoutesByDistance(count = 10) {
  return getRoutePage({
    page: 1,
    count,
    sortField: 'distanceNm',
    sortOrder: 'desc'
  })
}

export function getRouteDetail(id:number){

    return request({

        url:`/route/${id}`,

        method:'get'

    })

}

export function getRoutePoints(routeId:number){

    return request({

        url:`/route/${routeId}/points`,

        method:'get'

    })

}
