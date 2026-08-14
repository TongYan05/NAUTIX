<template>
  <div class="nx-page">
    <div class="nx-page-header">
      <div>
        <h2 class="nx-page-title">Weather Monitor</h2>
        <p class="nx-page-desc">Real-time environmental parameters for maritime weather regions</p>
      </div>
      <el-button @click="reload"><el-icon><Refresh /></el-icon>Refresh</el-button>
    </div>

    <el-empty v-if="!loading && regions.length === 0" description="No weather data available. Please check the backend service." />

    <div v-loading="loading" class="weather-grid">
      <div v-for="r in visibleRegions" :key="r.id" class="weather-card nx-panel">
        <div class="weather-head">
          <el-icon :size="22" color="#38bdf8"><Cloudy /></el-icon>
          <div class="region-name">{{ r.regionName || `Region ${r.id}` }}</div>
          <el-tag size="small" effect="dark" round>{{ r.weatherType || 'Unknown' }}</el-tag>
        </div>

        <div class="temp-row">
          <div class="temp-block">
            <div class="temp-value hot">{{ formatNum(r.airTemperature) }}°</div>
            <div class="temp-label">Air Temp</div>
          </div>
          <div class="temp-block">
            <div class="temp-value cool">{{ formatNum(r.seaTemperature) }}°</div>
            <div class="temp-label">Sea Temp</div>
          </div>
        </div>

        <div class="metric-grid">
          <div class="metric">
            <el-icon><WindPower /></el-icon>
            <span>{{ formatNum(r.windSpeed) }} m/s</span>
            <em>Wind Speed</em>
          </div>
          <div class="metric">
            <el-icon><Position /></el-icon>
            <span>{{ formatNum(r.windDirection) }}°</span>
            <em>Wind Dir</em>
          </div>
          <div class="metric">
            <el-icon><Drizzling /></el-icon>
            <span>{{ formatNum(r.humidity) }}%</span>
            <em>Humidity</em>
          </div>
          <div class="metric">
            <el-icon><Stopwatch /></el-icon>
            <span>{{ formatNum(r.pressure) }} hPa</span>
            <em>Pressure</em>
          </div>
          <div class="metric">
            <el-icon><MagicStick /></el-icon>
            <span>{{ formatNum(r.waveHeight) }} m</span>
            <em>Wave Height</em>
          </div>
        </div>

        <div class="update-time">Updated: {{ r.updateTime || '-' }}</div>
      </div>
    </div>

    <div ref="sentinelRef" class="load-sentinel">
      <span v-if="!loading && visibleRegions.length < regions.length" class="load-hint">
        Showing {{ visibleRegions.length }} of {{ regions.length }} regions — scroll to load more
      </span>
      <span v-else-if="!loading && regions.length > 0" class="load-hint">
        All {{ regions.length }} regions loaded
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllWeatherRegions } from '@/api/weather'
import type { WeatherRegion } from '@/api/types'

const loading = ref(false)
const regions = ref<WeatherRegion[]>([])

/* 增量渲染：首批 24 张，滚到底部每次追加 24 张，避免一次性渲染数千卡片 */
const BATCH = 24
const visibleCount = ref(BATCH)
const visibleRegions = computed(() => regions.value.slice(0, visibleCount.value))

const sentinelRef = ref<HTMLElement>()
let observer: IntersectionObserver | null = null

function loadMore() {
  if (visibleCount.value < regions.value.length) {
    visibleCount.value = Math.min(visibleCount.value + BATCH, regions.value.length)
  }
}

function formatNum(v?: number) {
  return v == null ? '-' : Number(v).toFixed(1)
}

async function reload() {
  loading.value = true
  try {
    regions.value = (await getAllWeatherRegions()) || []
    visibleCount.value = BATCH
  } catch {
    ElMessage.error('Failed to load weather data. Please check the backend service.')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  reload()
  observer = new IntersectionObserver(entries => {
    if (entries.some(e => e.isIntersecting)) loadMore()
  }, { rootMargin: '200px' })
  if (sentinelRef.value) observer.observe(sentinelRef.value)
})

onBeforeUnmount(() => {
  observer?.disconnect()
  observer = null
})
</script>

<style scoped>
.weather-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}

.weather-card {
  padding: 18px 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.weather-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 40px rgba(2, 8, 23, 0.6);
}

.weather-head {
  display: flex;
  align-items: center;
  gap: 10px;
}

.region-name {
  font-size: 16px;
  font-weight: 700;
  flex: 1;
}

.temp-row {
  display: flex;
  gap: 14px;
}

.temp-block {
  flex: 1;
  text-align: center;
  padding: 12px 0;
  border-radius: 12px;
  background: rgba(56, 189, 248, 0.06);
  border: 1px solid rgba(56, 189, 248, 0.12);
}

.temp-value {
  font-size: 28px;
  font-weight: 800;
  font-variant-numeric: tabular-nums;
}

.temp-value.hot { color: #fbbf24; }
.temp-value.cool { color: #38bdf8; }

.temp-label {
  font-size: 12px;
  color: var(--nx-text-dim);
  margin-top: 2px;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.metric {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 8px 4px;
  border-radius: 10px;
  background: rgba(10, 20, 40, 0.5);
}

.metric .el-icon {
  color: #7dd3fc;
}

.metric span {
  font-size: 13px;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
}

.metric em {
  font-style: normal;
  font-size: 11px;
  color: var(--nx-text-dim);
}

.update-time {
  font-size: 12px;
  color: var(--nx-text-dim);
  text-align: right;
}

.load-sentinel {
  padding: 18px 0 6px;
  text-align: center;
  min-height: 40px;
}

.load-hint {
  font-size: 13px;
  color: #64748b;
}
</style>
