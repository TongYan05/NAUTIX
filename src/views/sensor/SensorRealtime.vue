<template>
  <div class="nx-page">
    <div class="nx-page-header">
      <div>
        <h2 class="nx-page-title">
          {{ lang.t('sr.title') }}
          <span v-if="autoRefresh" class="live-badge"><span class="live-dot"></span>{{ lang.t('sr.live') }}</span>
        </h2>
        <p class="nx-page-desc">{{ lang.t('sr.desc') }}</p>
      </div>
      <el-switch
        v-model="autoRefresh"
        :active-text="lang.t('sr.autoRefresh')"
        inline-prompt
        style="--el-switch-on-color: #10b981"
      />
    </div>

    <div class="nx-panel filter-bar">
      <el-select-v2
        v-model="filterShipId"
        :options="shipOptions"
        :placeholder="lang.t('sr.selectShip')"
        clearable
        filterable
        style="width: 220px"
        @change="reload"
      />
      <el-date-picker
        v-model="timeRange"
        type="datetimerange"
        :range-separator="lang.lang === 'zh' ? '至' : 'to'"
        :start-placeholder="lang.t('sr.startTime')"
        :end-placeholder="lang.t('sr.endTime')"
        value-format="YYYY-MM-DD HH:mm:ss"
        @change="reload"
      />
      <el-select v-model="size" style="width: 130px" @change="reload">
        <el-option :label="lang.t('sr.perPage', { n: 20 })" :value="20" />
        <el-option :label="lang.t('sr.perPage', { n: 50 })" :value="50" />
        <el-option :label="lang.t('sr.perPage', { n: 100 })" :value="100" />
      </el-select>
      <el-button type="primary" round @click="reload">
        <el-icon><Refresh /></el-icon>{{ lang.t('common.query') }}
      </el-button>
      <span v-if="lastRefreshAt" class="last-refresh">{{ lang.t('sr.lastUpdate', { t: lastRefreshAt }) }}</span>
    </div>

    <div class="stat-grid">
      <div v-for="s in summary" :key="s.label" class="stat-card nx-panel">
        <div class="stat-label">{{ s.label }}</div>
        <div class="stat-value" :style="{ color: s.color }">{{ s.value }}</div>
      </div>
    </div>

    <div class="nx-panel chart-card">
      <div class="card-title"><el-icon><TrendCharts /></el-icon>{{ lang.t('sr.trend') }}</div>
      <div ref="chartEl" class="chart-box" />
    </div>

    <div class="nx-panel table-card">
      <el-table v-loading="loading" :data="rows" stripe height="380">
        <el-table-column prop="id" :label="lang.t('col.id')" width="90" />
        <el-table-column prop="shipId" :label="lang.t('col.shipId')" width="90">
          <template #default="{ row }">
            <el-tag size="small" effect="plain">{{ shipNameOf(row.shipId) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="configId" :label="lang.t('col.configId')" width="90" />
        <el-table-column prop="dataValue" :label="lang.t('common.value')" width="120">
          <template #default="{ row }">
            <span class="value-text">{{ row.dataValue?.toFixed(3) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="recordedAt" :label="lang.t('col.recordedAt')" min-width="170" />
      </el-table>

      <div class="pager">
        <el-pagination
          v-model:current-page="page"
          :total="total"
          :page-size="size"
          layout="total, prev, pager, next"
          @current-change="reload"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getSensorDataPage } from '@/api/sensor'
import { useShipStore } from '@/stores/shipStore'
import { useChart, chartTheme } from '@/hooks/useChart'
import { useLang } from '@/stores/lang'
import type { SensorData } from '@/api/types'

const lang = useLang()
const shipStore = useShipStore()

const shipOptions = computed(() =>
  shipStore.ships.map(s => ({ value: s.id as number, label: `${s.shipName} (${s.id})` }))
)

const loading = ref(false)
const rows = ref<SensorData[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(50)
const filterShipId = ref<number>()
const timeRange = ref<[string, string] | null>(null)

const autoRefresh = ref(true)
const lastRefreshAt = ref('')

function shipNameOf(shipId?: number) {
  if (shipId == null) return '-'
  return shipStore.shipMap.get(shipId)?.shipName || `#${shipId}`
}

const chartEl = ref<HTMLElement>()
const chart = useChart(chartEl)

const summary = computed(() => {
  const values = rows.value.map(r => r.dataValue).filter((v): v is number => v != null)
  if (values.length === 0) {
    return [
      { label: lang.t('sr.records'), value: 0, color: '#38bdf8' },
      { label: lang.t('sr.max'), value: '-', color: '#fbbf24' },
      { label: lang.t('sr.min'), value: '-', color: '#34d399' },
      { label: lang.t('sr.average'), value: '-', color: '#a78bfa' },
    ]
  }
  const max = Math.max(...values)
  const min = Math.min(...values)
  const avg = values.reduce((a, b) => a + b, 0) / values.length
  return [
    { label: lang.t('sr.records'), value: values.length, color: '#38bdf8' },
    { label: lang.t('sr.max'), value: max.toFixed(3), color: '#fbbf24' },
    { label: lang.t('sr.min'), value: min.toFixed(3), color: '#34d399' },
    { label: lang.t('sr.average'), value: avg.toFixed(3), color: '#a78bfa' },
  ]
})

function fmt(dt: Date) {
  const p = (n: number) => String(n).padStart(2, '0')
  return `${dt.getFullYear()}-${p(dt.getMonth() + 1)}-${p(dt.getDate())} ${p(dt.getHours())}:${p(dt.getMinutes())}:${p(dt.getSeconds())}`
}

async function reload(silent = false) {
  if (!silent) loading.value = true
  try {
    const res = await getSensorDataPage({
      page: page.value,
      size: size.value,
      shipId: filterShipId.value,
      startTime: timeRange.value?.[0],
      endTime: timeRange.value?.[1],
    })
    rows.value = res?.records || []
    total.value = res?.total || 0
    lastRefreshAt.value = fmt(new Date())
    renderChart()
  } catch {
    if (!silent) ElMessage.error(lang.t('common.loadFailed'))
  } finally {
    loading.value = false
  }
}

function renderChart() {
  const sorted = [...rows.value].sort(
    (a, b) => new Date(a.recordedAt || 0).getTime() - new Date(b.recordedAt || 0).getTime()
  )
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 60, right: 24, top: 30, bottom: 40 },
    xAxis: {
      type: 'category',
      data: sorted.map(r => (r.recordedAt || '').slice(5, 19)),
      axisLine: { lineStyle: { color: chartTheme.axisLine } },
      axisLabel: { color: chartTheme.legendText, fontSize: 10, rotate: 30 },
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: chartTheme.legendText },
      splitLine: { lineStyle: { color: chartTheme.splitLine } },
    },
    series: [
      {
        type: 'line',
        smooth: true,
        symbolSize: 5,
        data: sorted.map(r => r.dataValue),
        lineStyle: { width: 2, color: '#22d3ee' },
        itemStyle: { color: '#22d3ee' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(34,211,238,0.30)' },
              { offset: 1, color: 'rgba(34,211,238,0.02)' },
            ],
          },
        },
      },
    ],
  })
}

// 图表本身无语言相关文字，切换语言仅影响表头，由 computed 自动更新

let autoTimer: number | undefined

onMounted(async () => {
  await shipStore.loadShips()
  // 默认查询最近 1 小时，进入页面立即看到数据
  if (!timeRange.value) {
    const now = new Date()
    const from = new Date(now.getTime() - 60 * 60 * 1000)
    timeRange.value = [fmt(from), fmt(now)]
  }
  reload()

  autoTimer = window.setInterval(() => {
    if (!autoRefresh.value) return
    // 实时刷新：滑动时间窗，始终看最近 1 小时
    if (!filterShipId.value || timeRange.value) {
      const now = new Date()
      const from = new Date(now.getTime() - 60 * 60 * 1000)
      timeRange.value = [fmt(from), fmt(now)]
    }
    if (page.value !== 1) page.value = 1
    reload(true)
  }, 10000)
})

onUnmounted(() => {
  if (autoTimer) clearInterval(autoTimer)
})
</script>

<style scoped>
.filter-bar {
  display: flex;
  gap: 12px;
  padding: 14px 16px;
  flex-wrap: wrap;
  align-items: center;
}

.last-refresh { margin-left: auto; font-size: 12px; color: #64748b; }

.live-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin-left: 12px;
  padding: 2px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 1px;
  color: #34d399;
  background: rgba(16, 185, 129, 0.12);
  border: 1px solid rgba(16, 185, 129, 0.35);
  vertical-align: middle;
}
.live-dot {
  width: 8px; height: 8px;
  border-radius: 50%;
  background: #34d399;
  animation: livePulse 1.2s ease-in-out infinite;
}
@keyframes livePulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.4; transform: scale(0.75); }
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 12px;
}

.stat-card {
  padding: 14px 18px;
  text-align: center;
  transition: transform 0.2s ease;
}
.stat-card:hover { transform: translateY(-3px); }

.stat-label {
  font-size: 12px;
  color: var(--nx-text-dim);
}

.stat-value {
  font-size: 24px;
  font-weight: 800;
  margin-top: 4px;
  font-variant-numeric: tabular-nums;
}

.chart-card {
  padding: 16px 18px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #b8d4f5;
  margin-bottom: 8px;
}

.chart-box {
  height: 260px;
}

.table-card {
  padding: 10px 14px 14px;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.value-text {
  font-variant-numeric: tabular-nums;
  color: #7dd3fc;
  font-weight: 600;
}
</style>
