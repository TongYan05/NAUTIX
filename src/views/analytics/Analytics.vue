<template>
  <div class="nx-page analytics-container">
    <div class="nx-page-header">
      <div>
        <h2 class="nx-page-title">{{ lang.t('an.title') }}</h2>
        <p class="nx-page-desc">{{ lang.t('an.subtitle') }}</p>
      </div>
    </div>

    <!-- Row 1: Alert Time Distribution + Ship Operator Distribution -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card class="chart-card nx-panel">
          <template #header>
            <div class="card-header">
              <span><el-icon><Clock /></el-icon>{{ lang.t('an.alertTime') }}</span>
              <el-tag type="primary" size="small">{{ lang.t('an.byHour') }}</el-tag>
            </div>
          </template>
          <div ref="alertTimeChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card nx-panel">
          <template #header>
            <div class="card-header">
              <span><el-icon><OfficeBuilding /></el-icon>{{ lang.t('an.operator') }}</span>
              <el-tag type="success" size="small">{{ lang.t('an.top10') }}</el-tag>
            </div>
          </template>
          <div ref="operatorPieRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Row 2: Sensor Volatility Analysis + Route Top 10 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card class="chart-card nx-panel">
          <template #header>
            <div class="card-header">
              <span><el-icon><TrendCharts /></el-icon>{{ lang.t('an.volatility') }}</span>
              <el-tag type="warning" size="small">{{ lang.t('an.stdDevTag') }}</el-tag>
            </div>
          </template>
          <div ref="sensorVolatilityRef" class="chart-container" v-loading="sensorVolatilityLoading">
            <div v-if="sensorVolatilityEmpty && !sensorVolatilityLoading" class="empty-chart-state">
              <el-empty :description="lang.t('an.insufficient')" :image-size="80" />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card nx-panel">
          <template #header>
            <div class="card-header">
              <span><el-icon><Guide /></el-icon>{{ lang.t('an.topRoutes') }}</span>
              <el-tag type="info" size="small">{{ lang.t('an.nmi') }}</el-tag>
            </div>
          </template>
          <div ref="routeTop10Ref" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { Clock, OfficeBuilding, TrendCharts, Guide } from '@element-plus/icons-vue'
import { getAlertHourDistribution } from '@/api/dashboard'
import { getTopRoutesByDistance } from '@/api/route'
import { getSensorVolatility } from '@/api/sensor'
import { useChart } from '@/hooks/useChart'
import { useShipStore } from '@/stores/shipStore'
import { useLang } from '@/stores/lang'

const lang = useLang()
const shipStore = useShipStore()

const alertTimeChartRef = ref<HTMLElement>()
const operatorPieRef = ref<HTMLElement>()
const sensorVolatilityRef = ref<HTMLElement>()
const routeTop10Ref = ref<HTMLElement>()

const alertTimeChart = useChart(alertTimeChartRef)
const operatorPie = useChart(operatorPieRef)
const sensorVolatility = useChart(sensorVolatilityRef)
const routeTop10 = useChart(routeTop10Ref)

// Sensor volatility states
const sensorVolatilityLoading = ref(false)
const sensorVolatilityEmpty = ref(false)

/* caches so charts re-render on language switch without refetching */
const hourCache = ref<{ hour: number; count: number }[]>([])
const operatorCache = ref<{ name: string; value: number }[]>([])
const volatilityCache = ref<any[]>([])
const routeCache = ref<any[]>([])

// Load alert time distribution (server-side aggregation)
const loadAlertTimeDistribution = async () => {
  try {
    const rows = await getAlertHourDistribution() || []
    hourCache.value = rows
    renderAlertTime()
  } catch (error) {
    console.error('Failed to load alert time distribution:', error)
  }
}

function renderAlertTime() {
  const rows = hourCache.value
  const hourDistribution = new Array(24).fill(0)
  rows.forEach((row: any) => {
    const hour = Number(row.hour)
    if (hour >= 0 && hour < 24) hourDistribution[hour] = Number(row.count) || 0
  })

  alertTimeChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: Array.from({ length: 24 }, (_, i) => `${i}:00`),
      axisLine: { lineStyle: { color: '#4b5563' } },
      axisLabel: {
        color: '#9ca3af',
        interval: 2
      }
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#4b5563' } },
      axisLabel: { color: '#9ca3af' },
      splitLine: { lineStyle: { color: '#374151' } }
    },
    series: [{
      name: lang.t('an.seriesCount'),
      type: 'bar',
      data: hourDistribution,
      itemStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: '#ef4444' },
            { offset: 1, color: '#dc2626' }
          ]
        }
      }
    }]
  })
}

// Load operator distribution
const loadOperatorDistribution = async () => {
  try {
    await shipStore.loadShips()
    const ships = shipStore.ships

    // Count by operator
    const operatorCount: Record<string, number> = {}

    ships.forEach((ship: any) => {
      const operator = ship.operatingCompany || lang.t('an.unknown')
      operatorCount[operator] = (operatorCount[operator] || 0) + 1
    })

    // Get top 10
    const top10 = Object.entries(operatorCount)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 10)
      .map(([name, value]) => ({ name, value }))

    operatorCache.value = top10
    renderOperatorPie()
  } catch (error) {
    console.error('Failed to load operator distribution:', error)
  }
}

function renderOperatorPie() {
  operatorPie.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      type: 'scroll',
      orient: 'vertical',
      right: 10,
      top: 20,
      bottom: 20,
      textStyle: { color: '#d1d5db' }
    },
    series: [{
      name: lang.t('an.operator'),
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#1f2937',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 16,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: operatorCache.value
    }]
  })
}

// Load sensor volatility analysis
const loadSensorVolatility = async () => {
  sensorVolatilityLoading.value = true
  sensorVolatilityEmpty.value = false

  try {
    // 数据库级聚合：只取最近 30 万行做 GROUP BY，秒级返回
    const stats = (await getSensorVolatility(300000)) || []

    const volatilityData = stats
      .filter(s => s.stdDev != null && s.mean != null && s.cnt >= 2)
      .map(s => ({
        configId: Number(s.configId),
        stdDev: parseFloat(Number(s.stdDev).toFixed(2)),
        cv: Number(s.mean) !== 0
          ? parseFloat(((Number(s.stdDev) / Math.abs(Number(s.mean))) * 100).toFixed(2))
          : 0,
        count: Number(s.cnt),
      }))
      .sort((a, b) => b.stdDev - a.stdDev)
      .slice(0, 300)

    if (volatilityData.length === 0) {
      sensorVolatilityEmpty.value = true
      return
    }
    volatilityCache.value = volatilityData
    renderVolatility()
  } catch (error) {
    console.error('Failed to load sensor volatility:', error)
    sensorVolatilityEmpty.value = true
  } finally {
    sensorVolatilityLoading.value = false
  }
}

function renderVolatility() {
  const volatilityData = volatilityCache.value
  sensorVolatility.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const data = params.data
        return `${lang.t('an.configId')}: ${data[3]}<br/>${lang.t('an.stdDevTag')}: ${data[0]}<br/>${lang.t('an.axisCv')}: ${data[1]}%<br/>${lang.t('an.samples')}: ${data[2]}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      name: lang.t('an.axisStdDev'),
      nameTextStyle: { color: '#9ca3af' },
      axisLine: { lineStyle: { color: '#4b5563' } },
      axisLabel: { color: '#9ca3af' },
      splitLine: { lineStyle: { color: '#374151' } }
    },
    yAxis: {
      type: 'value',
      name: lang.t('an.axisCv'),
      nameTextStyle: { color: '#9ca3af' },
      axisLine: { lineStyle: { color: '#4b5563' } },
      axisLabel: { color: '#9ca3af' },
      splitLine: { lineStyle: { color: '#374151' } }
    },
    series: [{
      name: lang.t('an.volatility'),
      type: 'scatter',
      symbolSize: (data: any) => Math.max(Math.sqrt(data[2]) * 2, 8),
      data: volatilityData.map(item => [item.stdDev, item.cv, item.count, item.configId]),
      itemStyle: {
        color: {
          type: 'radial',
          x: 0.5,
          y: 0.5,
          r: 0.5,
          colorStops: [
            { offset: 0, color: '#fbbf24' },
            { offset: 1, color: '#f59e0b' }
          ]
        },
        shadowBlur: 10,
        shadowColor: 'rgba(251, 191, 36, 0.5)'
      }
    }]
  })
}

// Load route top 10 by distance
const loadRouteTop10 = async () => {
  try {
    const page = await getTopRoutesByDistance(10)
    const top10 = (page?.records || []).slice().reverse() // Reverse for horizontal bar chart
    routeCache.value = top10
    renderRouteTop10()
  } catch (error) {
    console.error('Failed to load route top 10:', error)
  }
}

function renderRouteTop10() {
  const top10 = routeCache.value
  routeTop10.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '10%',
      bottom: '3%',
      top: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#4b5563' } },
      axisLabel: { color: '#9ca3af' },
      splitLine: { lineStyle: { color: '#374151' } }
    },
    yAxis: {
      type: 'category',
      data: top10.map((route: any) => route.routeName || lang.t('an.route', { id: route.id })),
      axisLine: { lineStyle: { color: '#4b5563' } },
      axisLabel: {
        color: '#d1d5db',
        width: 120,
        overflow: 'truncate'
      }
    },
    series: [{
      name: lang.t('an.seriesDistance'),
      type: 'bar',
      data: top10.map((route: any) => route.distanceNm || 0),
      itemStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 1, y2: 0,
          colorStops: [
            { offset: 0, color: '#10b981' },
            { offset: 1, color: '#059669' }
          ]
        }
      },
      label: {
        show: true,
        position: 'right',
        color: '#9ca3af',
        formatter: '{c} nm'
      }
    }]
  })
}

// 语言切换：图表文字本地化重绘
watch(() => lang.lang, () => {
  renderAlertTime()
  renderOperatorPie()
  if (!sensorVolatilityEmpty.value) renderVolatility()
  renderRouteTop10()
})

onMounted(() => {
  loadAlertTimeDistribution()
  loadOperatorDistribution()
  loadSensorVolatility()
  loadRouteTop10()
})
</script>

<style scoped>
.analytics-container {
  min-height: 100%;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 14px;
  height: 100%;
}

.chart-card :deep(.el-card__header) {
  border-bottom: 1px solid rgba(148, 184, 232, 0.10);
  padding: 16px 20px;
  background: linear-gradient(135deg, rgba(56, 189, 248, 0.05), transparent);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #f9fafb;
  font-weight: 600;
  font-size: 15px;
}

.card-header span {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.card-header .el-icon {
  color: #60a5fa;
}

.chart-container {
  height: 400px;
  position: relative;
}

.empty-chart-state {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}
</style>
