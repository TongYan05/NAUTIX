<template>
  <div class="nx-page">
    <div class="nx-page-header">
      <div>
        <h2 class="nx-page-title">{{ lang.t('we.title') }}</h2>
        <p class="nx-page-desc">{{ lang.t('we.desc') }}</p>
      </div>
      <el-button round @click="reload"><el-icon><Refresh /></el-icon>{{ lang.t('common.refresh') }}</el-button>
    </div>

    <el-empty v-if="!loading && regions.length === 0" :description="lang.t('we.empty')" />

    <div v-loading="loading" class="weather-grid">
      <div v-for="r in visibleRegions" :key="r.id" class="weather-card nx-panel">
        <div class="weather-head">
          <el-icon :size="22" color="#38bdf8"><Cloudy /></el-icon>
          <div class="region-name">{{ r.regionName || lang.t('we.region', { id: r.id ?? '' }) }}</div>
          <el-tag size="small" effect="dark" round :type="weatherTagType(r.weatherType)">{{ weatherLabel(r.weatherType) }}</el-tag>
        </div>

        <div class="temp-row">
          <div class="temp-block">
            <div class="temp-value hot">{{ formatNum(r.airTemperature) }}°</div>
            <div class="temp-label">{{ lang.t('we.airTemp') }}</div>
          </div>
          <div class="temp-block">
            <div class="temp-value cool">{{ formatNum(r.seaTemperature) }}°</div>
            <div class="temp-label">{{ lang.t('we.seaTemp') }}</div>
          </div>
        </div>

        <div class="metric-grid">
          <div class="metric">
            <el-icon><WindPower /></el-icon>
            <span>{{ formatNum(r.windSpeed) }} m/s</span>
            <em>{{ lang.t('we.windSpeed') }}</em>
          </div>
          <div class="metric">
            <el-icon><Position /></el-icon>
            <span>{{ formatNum(r.windDirection) }}°</span>
            <em>{{ lang.t('we.windDir') }}</em>
          </div>
          <div class="metric">
            <el-icon><Drizzling /></el-icon>
            <span>{{ formatNum(r.humidity) }}%</span>
            <em>{{ lang.t('we.humidity') }}</em>
          </div>
          <div class="metric">
            <el-icon><Stopwatch /></el-icon>
            <span>{{ formatNum(r.pressure) }} hPa</span>
            <em>{{ lang.t('we.pressure') }}</em>
          </div>
          <div class="metric">
            <el-icon><MagicStick /></el-icon>
            <span>{{ formatNum(r.waveHeight) }} m</span>
            <em>{{ lang.t('we.waveHeight') }}</em>
          </div>
        </div>

        <div class="update-time">{{ lang.t('we.updated', { t: r.updateTime || '-' }) }}</div>
      </div>
    </div>

    <div ref="sentinelRef" class="load-sentinel">
      <span v-if="!loading && visibleRegions.length < regions.length" class="load-hint">
        {{ lang.t('we.showing', { n: visibleRegions.length, total: regions.length }) }}
      </span>
      <span v-else-if="!loading && regions.length > 0" class="load-hint">
        {{ lang.t('we.allLoaded', { n: regions.length }) }}
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllWeatherRegions } from '@/api/weather'
import { useLang } from '@/stores/lang'
import { weatherTypes } from '@/i18n'
import type { WeatherRegion } from '@/api/types'

const lang = useLang()

const loading = ref(false)
const regions = ref<WeatherRegion[]>([])

/* 增量渲染：首批 24 张，滚到底部每次追加 24 张，避免一次性渲染数千卡片 */
const BATCH = 24
const visibleCount = ref(BATCH)
const visibleRegions = computed(() => regions.value.slice(0, visibleCount.value))

const sentinelRef = ref<HTMLElement>()
let observer: IntersectionObserver | null = null

function weatherLabel(code?: string) {
  if (!code) return lang.t('common.unknown')
  const entry = weatherTypes[code]
  if (!entry) return code
  return lang.lang === 'zh' ? entry.zh : entry.en
}

function weatherTagType(code?: string) {
  if (!code) return 'info'
  if (['TYPHOON', 'HURRICANE', 'CYCLONE', 'TROPICAL_STORM', 'STORM', 'EXTREME_WEATHER', 'VERY_HIGH_WAVE', 'LIGHTNING_STORM', 'THUNDERSTORM'].includes(code)) return 'danger'
  if (['ROUGH_SEA', 'HIGH_WAVE', 'SEA_FOG', 'FREEZING_FOG', 'FOG', 'GALE', 'STORM_FORCE_WIND', 'STRONG_WIND', 'SAND_STORM', 'ICE_WARNING', 'LOW_VISIBILITY'].includes(code)) return 'warning'
  if (['SUNNY', 'CLEAR'].includes(code)) return 'success'
  return 'info'
}

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
    ElMessage.error(lang.t('common.loadFailed'))
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
