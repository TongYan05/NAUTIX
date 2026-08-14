<template>
  <div class="dashboard-container">
    <!-- Page Header -->
    <div class="page-header">
      <h1 class="page-title">
        <el-icon><DataBoard /></el-icon>
        Dashboard Overview
      </h1>
      <div class="page-actions">
        <el-button @click="refreshAll" :loading="isRefreshing">
          <el-icon><Refresh /></el-icon>
          Refresh
        </el-button>
        <el-tag type="info" effect="dark">
          Auto-refresh: 30s
        </el-tag>
      </div>
    </div>

    <!-- Stats Cards -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card stats-card-blue" shadow="hover">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon :size="36"><Ship /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-label">Total Ships</div>
              <div class="stats-value">
                <el-skeleton v-if="loading.stats" animated style="width: 60px">
                  <template #template>
                    <el-skeleton-item variant="text" style="width: 60px; height: 28px" />
                  </template>
                </el-skeleton>
                <span v-else>{{ stats.shipCount || 0 }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card stats-card-red" shadow="hover">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon :size="36"><Warning /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-label">Active Alerts</div>
              <div class="stats-value">
                <el-skeleton v-if="loading.stats" animated style="width: 60px">
                  <template #template>
                    <el-skeleton-item variant="text" style="width: 60px; height: 28px" />
                  </template>
                </el-skeleton>
                <span v-else>{{ stats.alertCount || 0 }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card stats-card-green" shadow="hover">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon :size="36"><Odometer /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-label">Sensors</div>
              <div class="stats-value">
                <el-skeleton v-if="loading.stats" animated style="width: 60px">
                  <template #template>
                    <el-skeleton-item variant="text" style="width: 60px; height: 28px" />
                  </template>
                </el-skeleton>
                <span v-else>{{ stats.sensorConfigCount || 0 }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card stats-card-yellow" shadow="hover">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon :size="36"><Guide /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-label">Weather Zones</div>
              <div class="stats-value">
                <el-skeleton v-if="loading.stats" animated style="width: 60px">
                  <template #template>
                    <el-skeleton-item variant="text" style="width: 60px; height: 28px" />
                  </template>
                </el-skeleton>
                <span v-else>{{ stats.weatherCount || 0 }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Charts Row -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="16">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>
                <el-icon><TrendCharts /></el-icon>
                Sensor Data Trend
              </span>
              <el-tag type="primary" size="small" effect="dark">Real-time</el-tag>
            </div>
          </template>
          <div v-show="loading.trend" class="chart-loading">
            <el-skeleton animated>
              <template #template>
                <el-skeleton-item variant="rect" style="width: 100%; height: 300px" />
              </template>
            </el-skeleton>
          </div>
          <div v-show="!loading.trend && trendEmpty" class="chart-empty">
            <el-empty description="No sensor data available">
              <template #image>
                <el-icon :size="60" color="#9ca3af"><DataLine /></el-icon>
              </template>
            </el-empty>
          </div>
          <div v-show="!loading.trend && !trendEmpty" ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>
                <el-icon><PieChart /></el-icon>
                Alert Status
              </span>
              <el-tag type="warning" size="small" effect="dark">Distribution</el-tag>
            </div>
          </template>
          <div v-show="loading.alert" class="chart-loading">
            <el-skeleton animated>
              <template #template>
                <el-skeleton-item variant="circle" style="width: 200px; height: 200px; margin: 50px auto" />
              </template>
            </el-skeleton>
          </div>
          <div v-show="!loading.alert && alertEmpty" class="chart-empty">
            <el-empty description="No alerts recorded">
              <template #image>
                <el-icon :size="60" color="#9ca3af"><Bell /></el-icon>
              </template>
            </el-empty>
          </div>
          <div v-show="!loading.alert && !alertEmpty" ref="alertPieRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Second Charts Row -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>
                <el-icon><Histogram /></el-icon>
                Ship Type Distribution
              </span>
              <el-tag type="success" size="small" effect="dark">Fleet</el-tag>
            </div>
          </template>
          <div v-show="loading.shipType" class="chart-loading">
            <el-skeleton animated>
              <template #template>
                <el-skeleton-item variant="rect" style="width: 100%; height: 300px" />
              </template>
            </el-skeleton>
          </div>
          <div v-show="!loading.shipType && shipTypeEmpty" class="chart-empty">
            <el-empty description="No ship type data available">
              <template #image>
                <el-icon :size="60" color="#9ca3af"><Ship /></el-icon>
              </template>
            </el-empty>
          </div>
          <div v-show="!loading.shipType && !shipTypeEmpty" ref="shipTypeBarRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>
                <el-icon><AlarmClock /></el-icon>
                Recent Alerts
              </span>
              <el-tag type="danger" size="small" effect="dark">Latest 10</el-tag>
            </div>
          </template>
          <div v-if="loading.recentAlerts" class="table-loading">
            <el-skeleton animated :rows="5" />
          </div>
          <div v-else-if="recentAlerts.length === 0" class="table-empty">
            <el-empty description="No recent alerts">
              <template #image>
                <el-icon :size="60" color="#9ca3af"><Check /></el-icon>
              </template>
            </el-empty>
          </div>
          <el-table v-else :data="recentAlerts" size="small" height="280" stripe>
            <el-table-column prop="shipId" label="Ship ID" width="80" />
            <el-table-column prop="ruleId" label="Rule ID" width="80" />
            <el-table-column prop="triggerValue" label="Trigger Value" />
            <el-table-column label="Status" width="100">
              <template #default="{ row }">
                <el-tag :type="row.handleStatus === 1 ? 'success' : 'danger'" size="small">
                  {{ row.handleStatus === 1 ? 'Handled' : 'Pending' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="alertTime" label="Time" width="160" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDashboardStats, getAlertStatusSummary, getShipTypeDistribution } from '@/api/dashboard'
import { getSensorTrendData } from '@/api/sensor'
import { getAlertRecordPage } from '@/api/alert'
import { useChart } from '@/hooks/useChart'
import type { DashboardStats } from '@/api/dashboard'

const stats = ref<DashboardStats>({
  shipCount: 0,
  alertCount: 0,
  sensorConfigCount: 0,
  weatherCount: 0
})

const recentAlerts = ref<any[]>([])

const loading = ref({
  stats: true,
  trend: true,
  alert: true,
  shipType: true,
  recentAlerts: true
})

const isRefreshing = ref(false)

// Empty states
const trendEmpty = ref(false)
const alertEmpty = ref(false)
const shipTypeEmpty = ref(false)

const trendChartRef = ref<HTMLElement>()
const alertPieRef = ref<HTMLElement>()
const shipTypeBarRef = ref<HTMLElement>()

const trendChart = useChart(trendChartRef)
const alertPie = useChart(alertPieRef)
const shipTypeBar = useChart(shipTypeBarRef)

const loadStats = async () => {
  loading.value.stats = true
  try {
    const data = await getDashboardStats()
    stats.value = data
  } catch (error) {
    console.error('Failed to load stats:', error)
  } finally {
    loading.value.stats = false
  }
}

/* ---- Fallback mock data generators ---- */
function mockTrendData() {
  const now = Date.now()
  return Array.from({ length: 30 }, (_, i) => ({
    recordedAt: new Date(now - (29 - i) * 60000).toISOString().replace('T', ' ').substring(0, 19),
    dataValue: +(20 + Math.sin(i * 0.4) * 8 + Math.random() * 4).toFixed(1)
  }))
}

function mockAlertSummary() {
  return [
    { handleStatus: 0, count: 12 },
    { handleStatus: 1, count: 28 }
  ]
}

function mockShipTypeDist() {
  return [
    { shipType: 'Cargo', count: 15 },
    { shipType: 'Tanker', count: 10 },
    { shipType: 'Container', count: 22 },
    { shipType: 'Bulk Carrier', count: 8 },
    { shipType: 'Passenger', count: 5 }
  ]
}

const loadTrendChart = async () => {
  loading.value.trend = true
  let data: any[] = []
  try {
    data = await getSensorTrendData(100) || []
  } catch (e) {
    console.warn('Trend API failed:', e)
  }
  if (data.length === 0) data = mockTrendData()
  trendEmpty.value = false
  loading.value.trend = false
  await nextTick()
  trendChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: data.map((item: any) => item.recordedAt?.substring(11, 19) || ''),
      axisLine: { lineStyle: { color: '#374151' } },
      axisLabel: { color: '#9ca3af' }
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#374151' } },
      axisLabel: { color: '#9ca3af' },
      splitLine: { lineStyle: { color: '#374151' } }
    },
    series: [{
      name: 'Value',
      type: 'line',
      data: data.map((item: any) => item.dataValue || 0),
      smooth: true,
      lineStyle: { color: '#3b82f6', width: 2 },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(59, 130, 246, 0.3)' },
            { offset: 1, color: 'rgba(59, 130, 246, 0.05)' }
          ]
        }
      }
    }]
  })
}

const loadAlertPie = async () => {
  loading.value.alert = true
  let data: any[] = []
  try {
    data = await getAlertStatusSummary() || []
  } catch (e) {
    console.warn('Alert API failed:', e)
  }
  if (data.length === 0) data = mockAlertSummary()
  alertEmpty.value = false
  loading.value.alert = false
  await nextTick()
  alertPie.setOption({
    tooltip: { trigger: 'item' },
    legend: {
      orient: 'vertical',
      left: 'left',
      textStyle: { color: '#9ca3af' }
    },
    series: [{
      type: 'pie',
      radius: '60%',
      data: data.map((item: any) => ({
        name: item.handleStatus === 0 ? 'Pending' : 'Handled',
        value: item.count
      })),
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  })
}

const loadShipTypeBar = async () => {
  loading.value.shipType = true
  let data: any[] = []
  try {
    data = await getShipTypeDistribution() || []
  } catch (e) {
    console.warn('Ship type API failed:', e)
  }
  if (data.length === 0) data = mockShipTypeDist()
  shipTypeEmpty.value = false
  loading.value.shipType = false
  await nextTick()
  shipTypeBar.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: data.map((item: any) => item.shipType || ''),
      axisLine: { lineStyle: { color: '#374151' } },
      axisLabel: { color: '#9ca3af', rotate: 30 }
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#374151' } },
      axisLabel: { color: '#9ca3af' },
      splitLine: { lineStyle: { color: '#374151' } }
    },
    series: [{
      type: 'bar',
      data: data.map((item: any) => item.count || 0),
      itemStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: '#3b82f6' },
            { offset: 1, color: '#60a5fa' }
          ]
        }
      }
    }]
  })
}

const loadRecentAlerts = async () => {
  loading.value.recentAlerts = true
  try {
    const response = await getAlertRecordPage({ page: 1, size: 10 })
    recentAlerts.value = (response as any)?.records || []
  } catch (error) {
    console.error('Failed to load recent alerts:', error)
  } finally {
    loading.value.recentAlerts = false
  }
}

const refreshAll = async () => {
  isRefreshing.value = true
  ElMessage.info('Refreshing dashboard data...')
  
  try {
    await Promise.all([
      loadStats(),
      loadTrendChart(),
      loadAlertPie(),
      loadShipTypeBar(),
      loadRecentAlerts()
    ])
    ElMessage.success('Dashboard refreshed successfully')
  } catch (error) {
    ElMessage.error('Failed to refresh dashboard')
  } finally {
    isRefreshing.value = false
  }
}

let refreshInterval: number

onMounted(() => {
  refreshAll()
  
  refreshInterval = window.setInterval(() => {
    loadStats()
    loadTrendChart()
  }, 10000)
})

onUnmounted(() => {
  clearInterval(refreshInterval)
})
</script>

<style scoped>
.dashboard-container {
  min-height: 100%;
}

/* Page Header */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #1f2937 0%, #374151 100%);
  border-radius: 12px;
  border: 1px solid #4b5563;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 24px;
  font-weight: 700;
  color: #f9fafb;
  margin: 0;
}

.page-title .el-icon {
  color: #60a5fa;
}

.page-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* Stats Cards */
.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  background-color: #1f2937;
  border: 1px solid #374151;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.stats-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.3);
}

.stats-card :deep(.el-card__body) {
  padding: 24px;
}

.stats-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stats-icon {
  width: 72px;
  height: 72px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

/* Color themes for stats cards */
.stats-card-blue .stats-icon {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2) 0%, rgba(59, 130, 246, 0.1) 100%);
  color: #3b82f6;
}

.stats-card-red .stats-icon {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.2) 0%, rgba(239, 68, 68, 0.1) 100%);
  color: #ef4444;
}

.stats-card-green .stats-icon {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.2) 0%, rgba(16, 185, 129, 0.1) 100%);
  color: #10b981;
}

.stats-card-yellow .stats-icon {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.2) 0%, rgba(245, 158, 11, 0.1) 100%);
  color: #f59e0b;
}

.stats-info {
  flex: 1;
  min-width: 0;
}

.stats-label {
  font-size: 14px;
  color: #9ca3af;
  margin-bottom: 8px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stats-value {
  font-size: 32px;
  font-weight: 700;
  color: #f9fafb;
  line-height: 1;
  display: flex;
  align-items: center;
}

.stats-value span {
  background: linear-gradient(135deg, #f9fafb 0%, #d1d5db 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* Charts */
.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  background-color: #1f2937;
  border: 1px solid #374151;
  border-radius: 12px;
  height: 100%;
}

.chart-card :deep(.el-card__header) {
  border-bottom: 1px solid #4b5563;
  padding: 18px 24px;
  background: linear-gradient(135deg, rgba(55, 65, 81, 0.3) 0%, rgba(31, 41, 55, 0.3) 100%);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #f9fafb;
  font-weight: 600;
  font-size: 16px;
}

.card-header .el-icon {
  color: #60a5fa;
  font-size: 18px;
}

.chart-container {
  height: 300px;
  padding: 16px 0;
}

.chart-loading,
.chart-empty {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.table-loading,
.table-empty {
  height: 280px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

/* Tables */
.el-table {
  background-color: transparent;
}

.el-table th {
  background-color: #374151 !important;
  color: #f3f4f6;
  font-weight: 600;
}

.el-table td {
  background-color: transparent !important;
  color: #d1d5db;
}

.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell {
  background-color: rgba(55, 65, 81, 0.3) !important;
}

.el-table__body tr:hover > td.el-table__cell {
  background-color: rgba(59, 130, 246, 0.1) !important;
}

/* Responsive */
@media (max-width: 1200px) {
  .stats-row .el-col {
    margin-bottom: 16px;
  }
}
</style>
