<template>
  <div class="nx-page">
    <div class="nx-page-header">
      <div>
        <h2 class="nx-page-title">Alert Rules</h2>
        <p class="nx-page-desc">Configure sensor alert thresholds, operators and severity levels</p>
      </div>
      <el-button type="primary" @click="openCreate"><el-icon><Plus /></el-icon>Add Rule</el-button>
    </div>

    <div class="nx-panel filter-bar">
      <el-input v-model="filterName" placeholder="Rule Name" clearable style="width: 240px" @keyup.enter="reload" @clear="reload">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-select v-model="filterLevel" placeholder="Alert Level" clearable style="width: 140px" @change="reload">
        <el-option label="Low" :value="1" />
        <el-option label="Medium" :value="2" />
        <el-option label="High" :value="3" />
        <el-option label="Critical" :value="4" />
      </el-select>
      <el-button @click="reload"><el-icon><Refresh /></el-icon>Refresh</el-button>
      <span class="result-count">{{ total }} alert rules found</span>
    </div>

    <div class="stat-grid">
      <div class="stat-card nx-panel">
        <div class="stat-icon" style="color:#38bdf8;background:rgba(56,189,248,.12);border-color:rgba(56,189,248,.25)"><el-icon :size="20"><Files /></el-icon></div>
        <div class="stat-body">
          <div class="stat-value" style="color:#38bdf8">{{ statsTotal }}</div>
          <div class="stat-label">Total Rules</div>
        </div>
      </div>
      <div class="stat-card nx-panel">
        <div class="stat-icon" style="color:#ef4444;background:rgba(239,68,68,.12);border-color:rgba(239,68,68,.25)"><el-icon :size="20"><WarnTriangleFilled /></el-icon></div>
        <div class="stat-body">
          <div class="stat-value" style="color:#ef4444">{{ statsCritical }}</div>
          <div class="stat-label">Critical</div>
        </div>
      </div>
      <div class="stat-card nx-panel">
        <div class="stat-icon" style="color:#f56c6c;background:rgba(245,108,108,.12);border-color:rgba(245,108,108,.25)"><el-icon :size="20"><BellFilled /></el-icon></div>
        <div class="stat-body">
          <div class="stat-value" style="color:#f56c6c">{{ statsHigh }}</div>
          <div class="stat-label">High</div>
        </div>
      </div>
      <div class="stat-card nx-panel">
        <div class="stat-icon" style="color:#e6a23c;background:rgba(230,162,60,.12);border-color:rgba(230,162,60,.25)"><el-icon :size="20"><WarningFilled /></el-icon></div>
        <div class="stat-body">
          <div class="stat-value" style="color:#e6a23c">{{ statsMediumLow }}</div>
          <div class="stat-label">Medium / Low</div>
        </div>
      </div>
      <div class="stat-card nx-panel">
        <div class="stat-icon" style="color:#a78bfa;background:rgba(167,139,250,.12);border-color:rgba(167,139,250,.25)"><el-icon :size="20"><Odometer /></el-icon></div>
        <div class="stat-body">
          <div class="stat-value" style="color:#a78bfa">{{ statsTypes }}</div>
          <div class="stat-label">Sensor Types Covered</div>
        </div>
      </div>
    </div>

    <div v-loading="loading" class="carousel-wrap">
      <el-empty v-if="!loading && rows.length === 0" description="No alert rules found">
        <template #image>
          <el-icon :size="60" color="#9ca3af"><Setting /></el-icon>
        </template>
      </el-empty>

      <template v-else>
        <button class="nav-btn nav-prev" :disabled="!canPrev" @click="scrollByCards(-1)">
          <el-icon :size="18"><ArrowLeft /></el-icon>
        </button>

        <div ref="scrollerRef" class="rules-scroller">
          <div
            v-for="row in rows"
            :key="row.id"
            class="rule-card nx-panel"
            :class="'level-' + (row.alertLevel || 0)"
          >
            <div class="rule-top">
              <div class="rule-icon"><el-icon :size="20"><component :is="levelIcon(row.alertLevel)" /></el-icon></div>
              <el-tag :type="levelType(row.alertLevel)" effect="dark" size="small">{{ levelText(row.alertLevel) }}</el-tag>
            </div>
            <div class="rule-name" :title="row.ruleName">{{ row.ruleName }}</div>
            <div class="rule-type"><el-tag size="small" effect="plain">{{ dictNameOf(row.typeId) }}</el-tag></div>
            <div class="rule-cond">
              <span class="cond-text">{{ operatorText(row.operator) }} {{ row.thresholdValue }}</span>
              <span class="cond-label">Trigger Condition</span>
            </div>
            <div class="rule-foot">
              <span class="rule-duration"><el-icon :size="13"><Timer /></el-icon>{{ row.durationSeconds }}s</span>
              <span class="rule-actions">
                <el-button link type="primary" size="small" @click="openEdit(row)">Edit</el-button>
                <el-button link type="danger" size="small" @click="confirmDelete(row)">Delete</el-button>
              </span>
            </div>
          </div>
        </div>

        <button class="nav-btn nav-next" :disabled="!canNext" @click="scrollByCards(1)">
          <el-icon :size="18"><ArrowRight /></el-icon>
        </button>
      </template>

      <div class="pager">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[20, 40, 60]"
          layout="total, sizes, prev, pager, next"
          @size-change="reload"
          @current-change="reload"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="editing ? 'Edit Rule' : 'Add Rule'" width="520px" destroy-on-close>
      <el-form :model="form" label-width="110px">
        <el-form-item label="Rule Name" required>
          <el-input v-model="form.ruleName" placeholder="e.g. High Exhaust Temp" />
        </el-form-item>
        <el-form-item label="Sensor Type">
          <el-select v-model="form.typeId" placeholder="Select Type" style="width: 100%">
            <el-option v-for="d in dictOptions" :key="d.id" :label="d.typeName" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="Operator">
          <el-select v-model="form.operator" style="width: 100%">
            <el-option label="Greater than (>)" value=">" />
            <el-option label="Greater or equal (>=)" value=">=" />
            <el-option label="Less than (<)" value="<" />
            <el-option label="Less or equal (<=)" value="<=" />
            <el-option label="Equal (=)" value="=" />
          </el-select>
        </el-form-item>
        <el-form-item label="Threshold">
          <el-input-number v-model="form.thresholdValue" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="Duration (seconds)">
          <el-input-number v-model="form.durationSeconds" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="Alert Level">
          <el-select v-model="form.alertLevel" style="width: 100%">
            <el-option label="Low" :value="1" />
            <el-option label="Medium" :value="2" />
            <el-option label="High" :value="3" />
            <el-option label="Critical" :value="4" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="saving" @click="save">Save</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  addAlertRule,
  deleteAlertRule,
  getAllAlertRules,
  getAlertRulePage,
  updateAlertRule,
} from '@/api/alert'
import { getAllSensorDicts } from '@/api/sensor'
import type { AlertRule, SensorDict } from '@/api/types'

const loading = ref(false)
const saving = ref(false)
const rows = ref<AlertRule[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(20)
const filterName = ref('')
const filterLevel = ref<number>()

const dialogVisible = ref(false)
const editing = ref(false)
const form = ref<AlertRule>({ operator: '>', durationSeconds: 60, alertLevel: 2 })

const dictOptions = ref<SensorDict[]>([])
const dictMap = computed(() => {
  const m = new Map<number, string>()
  for (const d of dictOptions.value) if (d.id != null) m.set(d.id, d.typeName || `#${d.id}`)
  return m
})

function dictNameOf(id?: number) {
  if (id == null) return '-'
  return dictMap.value.get(id) || `#${id}`
}

function operatorText(op?: string) {
  const map: Record<string, string> = {
    '>': '>', '>=': '≥', '<': '<', '<=': '≤', '=': '=',
  }
  return map[op || ''] || op || ''
}

function levelText(level?: number) {
  return ['', 'Low', 'Medium', 'High', 'Critical'][level || 0] || 'Unknown'
}

function levelType(level?: number) {
  return ['', 'info', 'warning', 'danger', 'danger'][level || 0] as
    | 'info' | 'warning' | 'danger'
}

function levelIcon(level?: number) {
  // Low / Medium / High / Critical -> distinct icons
  return ['', 'InfoFilled', 'WarningFilled', 'BellFilled', 'WarnTriangleFilled'][level || 0] || 'BellFilled'
}

/* ---- header stats ---- */
const allRules = ref<AlertRule[]>([])
const statsTotal = computed(() => allRules.value.length)
const statsCritical = computed(() => allRules.value.filter(r => r.alertLevel === 4).length)
const statsHigh = computed(() => allRules.value.filter(r => r.alertLevel === 3).length)
const statsMediumLow = computed(() => allRules.value.filter(r => (r.alertLevel || 0) <= 2).length)
const statsTypes = computed(() => new Set(allRules.value.map(r => r.typeId).filter(t => t != null)).size)

async function loadStats() {
  try {
    allRules.value = (await getAllAlertRules()) || []
  } catch {
    allRules.value = []
  }
}

/* ---- horizontal carousel ---- */
const scrollerRef = ref<HTMLElement>()
const canPrev = ref(false)
const canNext = ref(true)

function updateArrows() {
  const el = scrollerRef.value
  if (!el) return
  canPrev.value = el.scrollLeft > 4
  canNext.value = el.scrollLeft + el.clientWidth < el.scrollWidth - 4
}

function scrollByCards(dir: number) {
  const el = scrollerRef.value
  if (!el) return
  const card = el.querySelector<HTMLElement>('.rule-card')
  const step = card ? (card.offsetWidth + 16) * 3 : el.clientWidth
  el.scrollBy({ left: dir * step, behavior: 'smooth' })
}

let scrollListener: (() => void) | null = null

async function attachScrollListener() {
  await nextTick()
  const el = scrollerRef.value
  if (!el) return
  scrollListener?.()
  scrollListener = () => el.removeEventListener('scroll', updateArrows)
  el.addEventListener('scroll', updateArrows, { passive: true })
  updateArrows()
}

onBeforeUnmount(() => scrollListener?.())

async function loadDicts() {
  try {
    dictOptions.value = (await getAllSensorDicts()) || []
  } catch {
    dictOptions.value = []
  }
}

async function reload() {
  loading.value = true
  try {
    const res = await getAlertRulePage({
      page: page.value,
      size: size.value,
      ruleName: filterName.value || undefined,
      alertLevel: filterLevel.value,
    })
    rows.value = res?.records || []
    total.value = res?.total || 0
    attachScrollListener()
    loadStats()
  } catch {
    ElMessage.error('Failed to load alert rules. Please check the backend service.')
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editing.value = false
  form.value = { operator: '>', durationSeconds: 60, alertLevel: 2 }
  dialogVisible.value = true
}

function openEdit(row: AlertRule) {
  editing.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

async function save() {
  if (!form.value.ruleName?.trim()) {
    ElMessage.warning('Rule name is required')
    return
  }
  saving.value = true
  try {
    if (editing.value) {
      await updateAlertRule(form.value)
      ElMessage.success('Updated successfully')
    } else {
      await addAlertRule(form.value)
      ElMessage.success('Created successfully')
    }
    dialogVisible.value = false
    reload()
  } catch {
    ElMessage.error('Save failed. Please check the backend service.')
  } finally {
    saving.value = false
  }
}

async function confirmDelete(row: AlertRule) {
  try {
    await ElMessageBox.confirm(`Are you sure to delete rule "${row.ruleName}"?`, 'Confirm Deletion', { type: 'warning' })
  } catch {
    return
  }
  try {
    await deleteAlertRule(row.id!)
    ElMessage.success('Deleted successfully')
    reload()
  } catch {
    ElMessage.error('Delete failed. Please check the backend service.')
  }
}

onMounted(() => {
  loadDicts()
  loadStats()
  reload()
})
</script>

<style scoped>
.filter-bar { display: flex; gap: 10px; padding: 14px 16px; align-items: center; }
.result-count { margin-left: auto; font-size: 14px; color: #9ca3af; font-weight: 500; }

.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 14px;
  margin-top: 14px;
}
.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 18px;
}
.stat-icon {
  width: 42px; height: 42px; flex: none;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  border: 1px solid;
}
.stat-value { font-size: 24px; font-weight: 800; font-variant-numeric: tabular-nums; line-height: 1.1; }
.stat-label { font-size: 12px; color: #9ca3af; margin-top: 2px; }

.carousel-wrap { position: relative; margin-top: 14px; }

.rules-scroller {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  scroll-snap-type: x mandatory;
  scroll-behavior: smooth;
  padding: 6px 4px 18px;
}
.rules-scroller::-webkit-scrollbar { height: 8px; }
.rules-scroller::-webkit-scrollbar-track { background: rgba(148, 184, 232, 0.06); border-radius: 4px; }
.rules-scroller::-webkit-scrollbar-thumb { background: rgba(56, 189, 248, 0.35); border-radius: 4px; }

.rule-card {
  flex: 0 0 272px;
  scroll-snap-align: start;
  padding: 16px 18px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  border-top: 3px solid transparent;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.rule-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 28px rgba(2, 8, 23, 0.55);
}
.rule-card.level-1 { border-top-color: #909399; }
.rule-card.level-2 { border-top-color: #e6a23c; }
.rule-card.level-3 { border-top-color: #f56c6c; }
.rule-card.level-4 { border-top-color: #c02626; }

.rule-top { display: flex; align-items: center; justify-content: space-between; }
.rule-icon {
  width: 38px; height: 38px;
  border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  color: #fbbf24;
  background: rgba(251, 191, 36, 0.12);
  border: 1px solid rgba(251, 191, 36, 0.25);
}
.level-1 .rule-icon { color: #9ca3af; background: rgba(156, 163, 175, 0.12); border-color: rgba(156, 163, 175, 0.25); }
.level-2 .rule-icon { color: #e6a23c; background: rgba(230, 162, 60, 0.12); border-color: rgba(230, 162, 60, 0.25); }
.level-3 .rule-icon { color: #f56c6c; background: rgba(245, 108, 108, 0.12); border-color: rgba(245, 108, 108, 0.25); }
.level-4 .rule-icon { color: #ef4444; background: rgba(239, 68, 68, 0.16); border-color: rgba(239, 68, 68, 0.3); }

.rule-name {
  font-size: 15px; font-weight: 600; color: #f9fafb;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}

.rule-cond {
  display: flex; flex-direction: column; gap: 2px;
  padding: 8px 12px;
  border-radius: 10px;
  background: rgba(244, 114, 182, 0.07);
  border: 1px solid rgba(244, 114, 182, 0.15);
}
.cond-text { color: #f472b6; font-weight: 700; font-size: 17px; font-variant-numeric: tabular-nums; }
.cond-label { font-size: 11px; color: #9ca3af; }

.rule-foot { display: flex; align-items: center; justify-content: space-between; }
.rule-duration { display: inline-flex; align-items: center; gap: 4px; font-size: 12px; color: #9ca3af; }
.rule-actions { display: flex; gap: 4px; }

.nav-btn {
  position: absolute;
  top: 45%;
  transform: translateY(-50%);
  z-index: 5;
  width: 38px; height: 38px;
  border-radius: 50%;
  border: 1px solid rgba(56, 189, 248, 0.3);
  background: rgba(6, 13, 31, 0.85);
  color: #38bdf8;
  cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  backdrop-filter: blur(6px);
  transition: background 0.15s ease, opacity 0.15s ease;
}
.nav-btn:hover:not(:disabled) { background: rgba(56, 189, 248, 0.18); }
.nav-btn:disabled { opacity: 0.25; cursor: not-allowed; }
.nav-prev { left: -14px; }
.nav-next { right: -14px; }

.pager { display: flex; justify-content: flex-end; margin-top: 8px; }
</style>
