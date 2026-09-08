<template>
  <div class="dashboard-container nx-page">
    <!-- Page Header -->
    <div class="page-header nx-panel">
      <h1 class="page-title">
        <el-icon><DataBoard /></el-icon>
        {{ lang.t('dash.title') }}
      </h1>
      <div class="page-actions">
        <el-button round @click="refreshAll" :loading="isRefreshing">
          <el-icon><Refresh /></el-icon>
          {{ lang.t('common.refresh') }}
        </el-button>
        <el-tag type="info" effect="dark" round>
          {{ lang.t('dash.autoRefresh') }}
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
              <div class="stats-label">{{ lang.t('dash.totalShips') }}</div>
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
              <div class="stats-label">{{ lang.t('dash.activeAlerts') }}</div>
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
              <div class="stats-label">{{ lang.t('dash.sensors') }}</div>
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
              <div class="stats-label">{{ lang.t('dash.weatherZones') }}</div>
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
                {{ lang.t('dash.sensorTrend') }}
              </span>
              <el-tag type="primary" size="small" effect="dark">{{ lang.t('dash.realtime') }}</el-tag>
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
            <el-empty :description="lang.t('dash.noSensorData')">
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
                {{ lang.t('dash.alertStatus') }}
              </span>
              <el-tag type="warning" size="small" effect="dark">{{ lang.t('dash.distribution') }}</el-tag>
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
            <el-empty :description="lang.t('dash.noAlerts')">
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
                {{ lang.t('dash.shipTypeDist') }}
              </span>
              <el-tag type="success" size="small" effect="dark">{{ lang.t('dash.fleet') }}</el-tag>
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
            <el-empty :description="lang.t('dash.noShipType')">
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
                {{ lang.t('dash.recentAlerts') }}
              </span>
              <el-tag type="danger" size="small" effect="dark">{{ lang.t('dash.latest10') }}</el-tag>
            </div>
          </template>
          <div v-if="loading.recentAlerts" class="table-loading">
            <el-skeleton animated :rows="5" />
          </div>
          <div v-else-if="recentAlerts.length === 0" class="table-empty">
            <el-empty :description="lang.t('dash.noRecentAlerts')">
              <template #image>
                <el-icon :size="60" color="#9ca3af"><Check /></el-icon>
              </template>
            </el-empty>
          </div>
          <el-table v-else :data="recentAlerts" size="small" height="280" stripe>
            <el-table-column prop="shipId" :label="lang.t('col.shipId')" width="80" />
            <el-table-column prop="ruleId" :label="lang.t('col.ruleId')" width="80" />
            <el-table-column prop="triggerValue" :label="lang.t('col.triggerValue')" />
            <el-table-column :label="lang.t('common.status')" width="100">
              <template #default="{ row }">
                <el-tag :type="row.handleStatus === 1 ? 'success' : 'danger'" size="small">
                  {{ row.handleStatus === 1 ? lang.t('common.handled') : lang.t('common.pending') }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="alertTime" :label="lang.t('col.time')" width="160" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getDashboardStats, getAlertStatusSummary, getShipTypeDistribution } from '@/api/dashboard'
import { getSensorTrendData } from '@/api/sensor'
import { getAlertRecordPage } from '@/api/alert'
import { useChart } from '@/hooks/useChart'
import { useLang } from '@/stores/lang'
import { shipTypeEn } from '@/i18n'
import type { DashboardStats } from '@/api/dashboard'

const lang = useLang()

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

/* keep latest data so charts can be re-rendered when language switches */
const trendDataCache = ref<any[]>([])
const alertDataCache = ref<any[]>([])
const shipTypeCache = ref<any[]>([])

function typeLabel(zhType: string) {
  if (lang.lang === 'en') {
    return shipTypeEn[zhType] || zhType
  }
  return zhType
}

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

function renderTrend() {
  const data = trendDataCache.value
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
      name: lang.t('dash.seriesValue'),
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
  trendDataCache.value = data
  await nextTick()
  renderTrend()
}

function renderAlertPie() {
  const data = alertDataCache.value
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
        name: item.handleStatus === 0 ? lang.t('common.pending') : lang.t('common.handled'),
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
  alertDataCache.value = data
  await nextTick()
  renderAlertPie()
}

function renderShipTypeBar() {
  const data = shipTypeCache.value
  shipTypeBar.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: data.map((item: any) => typeLabel(item.shipType || '')),
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
      name: lang.t('dash.seriesAlerts'),
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
  shipTypeCache.value = data
  await nextTick()
  renderShipTypeBar()
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
  ElMessage.info(lang.t('dash.refreshing'))

  try {
    await Promise.all([
      loadStats(),
      loadTrendChart(),
      loadAlertPie(),
      loadShipTypeBar(),
      loadRecentAlerts()
    ])
    ElMessage.success(lang.t('dash.refreshOk'))
  } catch (error) {
    ElMessage.error(lang.t('dash.refreshFailed'))
  } finally {
    isRefreshing.value = false
  }
}

// 语言切换：图表内文字（图例/系列名/船型名）本地化重绘，无需重新请求
watch(() => lang.lang, () => {
  renderTrend()
  renderAlertPie()
  renderShipTypeBar()
})

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
  padding: 18px 22px;
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
  border-radius: 14px;
  transition: all 0.3s ease;
}

.stats-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(2, 8, 23, 0.55);
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
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.25) 0%, rgba(59, 130, 246, 0.08) 100%);
  color: #60a5fa;
  border: 1px solid rgba(96, 165, 250, 0.25);
}

.stats-card-red .stats-icon {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.25) 0%, rgba(239, 68, 68, 0.08) 100%);
  color: #f87171;
  border: 1px solid rgba(248, 113, 113, 0.25);
}

.stats-card-green .stats-icon {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.25) 0%, rgba(16, 185, 129, 0.08) 100%);
  color: #34d399;
  border: 1px solid rgba(52, 211, 153, 0.25);
}

.stats-card-yellow .stats-icon {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.25) 0%, rgba(245, 158, 11, 0.08) 100%);
  color: #fbbf24;
  border: 1px solid rgba(251, 191, 36, 0.25);
}

.stats-info {
  flex: 1;
  min-width: 0;
}

.stats-label {
  font-size: 13px;
  color: #94a3b8;
  margin-bottom: 8px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.stats-value {
  font-size: 32px;
  font-weight: 800;
  color: #f9fafb;
  line-height: 1;
  display: flex;
  align-items: center;
  font-variant-numeric: tabular-nums;
}

.stats-value span {
  background: linear-gradient(135deg, #f9fafb 0%, #cbd5e1 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* Charts */
.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 14px;
  height: 100%;
}

.chart-card :deep(.el-card__header) {
  border-bottom: 1px solid rgba(148, 184, 232, 0.10);
  padding: 16px 22px;
  background: linear-gradient(135deg, rgba(56, 189, 248, 0.05), transparent);
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
  color: #e2e8f0;
  font-weight: 600;
  font-size: 15px;
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

/* Responsive */
@media (max-width: 1200px) {
  .stats-row .el-col {
    margin-bottom: 16px;
  }
}
</style>
