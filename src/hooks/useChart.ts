import * as echarts from 'echarts'
import { onMounted, onUnmounted, type Ref } from 'vue'

/**
 * Lightweight ECharts wrapper: binds to container, auto-resizes, disposes on unmount
 * @param el template ref pointing to the DOM container
 */
export function useChart(el: Ref<HTMLElement | undefined | null>) {
  let chart: echarts.ECharts | null = null

  const resizeObserver = typeof ResizeObserver !== 'undefined' ? new ResizeObserver(() => {
    chart?.resize()
  }) : null

  onMounted(() => {
    if (el.value) {
      chart = echarts.init(el.value)
      if (resizeObserver) resizeObserver.observe(el.value)
    }
  })

  onUnmounted(() => {
    resizeObserver?.disconnect()
    chart?.dispose()
    chart = null
  })

  function setOption(option: echarts.EChartsOption) {
    if (!chart && el.value) {
      chart = echarts.init(el.value)
      if (resizeObserver) resizeObserver.observe(el.value)
    }
    chart?.setOption(option, true)
  }

  return { setOption }
}

/* Chart common dark theme tokens */
export const chartTheme = {
  textStyle: { color: '#a8bdd9' },
  legendText: '#a8bdd9',
  axisLine: 'rgba(148,184,232,0.25)',
  splitLine: 'rgba(148,184,232,0.08)',
  palette: ['#38bdf8', '#a78bfa', '#34d399', '#fbbf24', '#f472b6', '#22d3ee', '#fb7185', '#818cf8'],
}
