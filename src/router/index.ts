import { createRouter, createWebHistory } from 'vue-router'
import { tokenExpired } from '@/api/axios'

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

    // 受保护路由（需要登录）；meta.title=英文，meta.titleZh=中文
    { path: '/', redirect: '/dashboard' },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/dashboard/Dashboard.vue'),
      meta: { title: 'Dashboard', titleZh: '仪表盘' },
    },
    {
      path: '/ship',
      name: 'ship',
      component: () => import('@/views/ship/Ship.vue'),
      meta: { title: 'Ship Management', titleZh: '船舶管理' },
    },
    {
      path: '/sensor/realtime',
      name: 'sensor-realtime',
      component: () => import('@/views/sensor/SensorRealtime.vue'),
      meta: { title: 'Sensor Realtime Data', titleZh: '传感器实时数据' },
    },
    {
      path: '/sensor/config',
      name: 'sensor-config',
      component: () => import('@/views/sensor/SensorConfig.vue'),
      meta: { title: 'Sensor Configuration', titleZh: '传感器配置' },
    },
    {
      path: '/sensor/dict',
      name: 'sensor-dict',
      component: () => import('@/views/sensor/SensorDictionary.vue'),
      meta: { title: 'Sensor Dictionary', titleZh: '传感器字典' },
    },
    {
      path: '/alert/records',
      name: 'alert-records',
      component: () => import('@/views/alert/AlertRecords.vue'),
      meta: { title: 'Alert Records', titleZh: '告警记录' },
    },
    {
      path: '/alert/rules',
      name: 'alert-rules',
      component: () => import('@/views/alert/AlertRules.vue'),
      meta: { title: 'Alert Rules', titleZh: '告警规则' },
    },
    {
      path: '/route',
      name: 'route-manage',
      component: () => import('@/views/route/Route.vue'),
      meta: { title: 'Route Management', titleZh: '航线管理' },
    },
    {
      path: '/port',
      name: 'port',
      component: () => import('@/views/port/Port.vue'),
      meta: { title: 'Port Management', titleZh: '港口管理' },
    },
    {
      path: '/weather',
      name: 'weather',
      component: () => import('@/views/weather/Weather.vue'),
      meta: { title: 'Weather Monitor', titleZh: '天气监测' },
    },
    {
      path: '/analytics',
      name: 'analytics',
      component: () => import('@/views/analytics/Analytics.vue'),
      meta: { title: 'Analytics Center', titleZh: '分析中心' },
    },
    {
      path: '/system/logs',
      name: 'system-logs',
      component: () => import('@/views/system/Logs.vue'),
      meta: { title: 'Operation Logs', titleZh: '操作日志' },
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
  if (!token || tokenExpired()) {
    // 未登录或登录已过期，清理失效状态并跳转登录页，
    // 重新登录后 token 刷新，各页面即可正常加载数据
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
  } else {
    next()
  }
})

export default router
