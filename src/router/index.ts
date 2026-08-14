import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // 公开路由（无需登录）
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/auth/Login.vue'),
      meta: { title: 'Login', public: true },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/auth/Register.vue'),
      meta: { title: 'Register', public: true },
    },

    // 受保护路由（需要登录）
    { path: '/', redirect: '/dashboard' },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/dashboard/Dashboard.vue'),
      meta: { title: 'Dashboard' },
    },
    {
      path: '/ship',
      name: 'ship',
      component: () => import('@/views/ship/Ship.vue'),
      meta: { title: 'Ship Management' },
    },
    {
      path: '/sensor/realtime',
      name: 'sensor-realtime',
      component: () => import('@/views/sensor/SensorRealtime.vue'),
      meta: { title: 'Sensor Realtime Data' },
    },
    {
      path: '/sensor/config',
      name: 'sensor-config',
      component: () => import('@/views/sensor/SensorConfig.vue'),
      meta: { title: 'Sensor Configuration' },
    },
    {
      path: '/sensor/dict',
      name: 'sensor-dict',
      component: () => import('@/views/sensor/SensorDictionary.vue'),
      meta: { title: 'Sensor Dictionary' },
    },
    {
      path: '/alert/records',
      name: 'alert-records',
      component: () => import('@/views/alert/AlertRecords.vue'),
      meta: { title: 'Alert Records' },
    },
    {
      path: '/alert/rules',
      name: 'alert-rules',
      component: () => import('@/views/alert/AlertRules.vue'),
      meta: { title: 'Alert Rules' },
    },
    {
      path: '/route',
      name: 'route-manage',
      component: () => import('@/views/route/Route.vue'),
      meta: { title: 'Route Management' },
    },
    {
      path: '/port',
      name: 'port',
      component: () => import('@/views/port/Port.vue'),
      meta: { title: 'Port Management' },
    },
    {
      path: '/weather',
      name: 'weather',
      component: () => import('@/views/weather/Weather.vue'),
      meta: { title: 'Weather Monitor' },
    },
    {
      path: '/analytics',
      name: 'analytics',
      component: () => import('@/views/analytics/Analytics.vue'),
      meta: { title: 'Analytics Center' },
    },
    {
      path: '/system/logs',
      name: 'system-logs',
      component: () => import('@/views/system/Logs.vue'),
      meta: { title: 'Operation Logs' },
    },
    { path: '/:pathMatch(.*)*', redirect: '/dashboard' },
  ],
})

// 路由守卫：检查认证状态
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  const isPublicRoute = to.meta.public === true

  // 如果是公开路由（登录/注册），直接放行
  if (isPublicRoute) {
    // 如果已登录且访问登录页，重定向到首页
    if (token && (to.path === '/login' || to.path === '/register')) {
      next('/dashboard')
    } else {
      next()
    }
    return
  }

  // 如果是受保护路由，检查是否已登录
  if (!token) {
    // 未登录，重定向到登录页，并保存当前路径
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
  } else {
    next()
  }
})

export default router
