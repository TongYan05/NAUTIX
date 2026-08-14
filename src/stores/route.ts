import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getRoutePage } from '@/api/route'
import type { Route } from '@/types/route'

export const useRouteStore = defineStore('route', () => {

    const routes = ref<Route[]>([])

    const currentRoute = ref<Route>()

    async function loadRoutes() {
        const res: any = await getRoutePage({ page: 1, count: 100 })
        routes.value = res?.records ?? []
    }

    function selectRoute(route: Route) {
        currentRoute.value = route
    }

    return {
        routes,
        currentRoute,
        loadRoutes,
        selectRoute
    }

})
