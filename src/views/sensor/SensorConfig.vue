<template>
  <div class="nx-page">
    <div class="nx-page-header">
      <div>
        <h2 class="nx-page-title">{{ lang.t('sc.title') }}</h2>
        <p class="nx-page-desc">{{ lang.t('sc.desc') }}</p>
      </div>
      <el-button type="primary" round @click="openCreate"><el-icon><Plus /></el-icon>{{ lang.t('sc.add') }}</el-button>
    </div>

    <div class="nx-panel filter-bar">
      <el-select-v2
        v-model="filterShipId"
        :options="shipOptions"
        :placeholder="lang.t('sr.selectShip')"
        clearable
        filterable
        style="width: 200px"
        @change="reload"
      />
      <el-select v-model="filterTypeId" :placeholder="lang.t('col.sensorType')" clearable filterable style="width: 170px" @change="reload">
        <el-option v-for="d in dictOptions" :key="d.id" :label="dictLabel(d)" :value="d.id as number" />
      </el-select>
      <el-select v-model="filterStatus" :placeholder="lang.t('sc.statusLabel')" clearable style="width: 130px" @change="reload">
        <el-option :label="lang.t('common.enabled')" :value="1" />
        <el-option :label="lang.t('common.disabled')" :value="0" />
      </el-select>
      <el-input v-model="filterName" :placeholder="lang.t('col.sensorName')" clearable style="width: 200px" @keyup.enter="reload" @clear="reload" />
      <el-button round @click="reload"><el-icon><Refresh /></el-icon>{{ lang.t('common.refresh') }}</el-button>
    </div>

    <div class="nx-panel table-card">
      <div v-if="!loading && rows.length === 0" class="empty-state">
        <el-empty :description="lang.t('sc.empty')">
          <template #image>
            <el-icon :size="60" color="#9ca3af"><Odometer /></el-icon>
          </template>
        </el-empty>
      </div>
      <template v-else>
        <div class="table-info">
          <span class="result-count">{{ lang.t('sc.countFound', { n: total }) }}</span>
        </div>
        <el-table v-loading="loading" :data="rows" stripe height="calc(100vh - 340px)">
        <el-table-column prop="id" :label="lang.t('col.id')" width="70" />
        <el-table-column prop="shipId" :label="lang.t('col.ship')" width="130">
          <template #default="{ row }">
            <el-tag size="small" effect="plain">{{ shipNameOf(row.shipId) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sensorName" :label="lang.t('col.sensorName')" min-width="160" show-overflow-tooltip />
        <el-table-column prop="typeId" :label="lang.t('common.type')" width="120">
          <template #default="{ row }">
            <el-tag size="small" type="info" effect="plain">{{ dictNameOf(row.typeId) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="installDate" :label="lang.t('col.installDate')" width="120" />
        <el-table-column :label="lang.t('common.status')" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="dark" size="small">
              {{ row.status === 1 ? lang.t('common.enabledTag') : lang.t('common.disabledTag') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="lang.t('common.actions')" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">{{ lang.t('common.edit') }}</el-button>
            <el-button link type="danger" size="small" @click="confirmDelete(row)">{{ lang.t('common.delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="reload"
          @current-change="reload"
        />
      </div>
      </template>
    </div>

    <el-dialog v-model="dialogVisible" :title="editing ? lang.t('sc.editTitle') : lang.t('sc.add')" width="520px" destroy-on-close>
      <el-form :model="form" label-width="110px">
        <el-form-item :label="lang.t('col.ship')" required>
          <el-select-v2
            v-model="form.shipId"
            :options="shipOptions"
            :placeholder="lang.t('sr.selectShip')"
            filterable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item :label="lang.t('col.sensorName')" required>
          <el-input v-model="form.sensorName" :placeholder="lang.t('sc.namePh')" />
        </el-form-item>
        <el-form-item :label="lang.t('col.sensorType')">
          <el-select v-model="form.typeId" :placeholder="lang.t('sc.selectType')" style="width: 100%">
            <el-option v-for="d in dictOptions" :key="d.id" :label="dictLabel(d)" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item :label="lang.t('col.installDate')">
          <el-date-picker v-model="form.installDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item :label="lang.t('common.status')">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" :active-text="lang.t('common.enabled')" :inactive-text="lang.t('common.disabled')" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button round @click="dialogVisible = false">{{ lang.t('common.cancel') }}</el-button>
        <el-button type="primary" round :loading="saving" @click="save">{{ lang.t('common.save') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  addSensorConfig,
  deleteSensorConfig,
  getSensorConfigPage,
  updateSensorConfig,
  getAllSensorDicts,
} from '@/api/sensor'
import { useShipStore } from '@/stores/shipStore'
import { useLang } from '@/stores/lang'
import { sensorTypeEn } from '@/i18n'
import type { SensorConfig, SensorDict } from '@/api/types'

const lang = useLang()
const loading = ref(false)
const saving = ref(false)
const rows = ref<SensorConfig[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(20)

const shipStore = useShipStore()

const shipOptions = computed(() =>
  shipStore.ships.map(s => ({ value: s.id as number, label: `${s.shipName} (${s.id})` }))
)

const filterShipId = ref<number>()
const filterTypeId = ref<number>()
const filterStatus = ref<number>()
const filterName = ref('')

const dialogVisible = ref(false)
const editing = ref(false)
const form = ref<SensorConfig>({ status: 1 })

const dictOptions = ref<SensorDict[]>([])

const dictMap = computed(() => {
  const m = new Map<number, SensorDict>()
  for (const d of dictOptions.value) if (d.id != null) m.set(d.id, d)
  return m
})

// 字典 typeName 存中文，英文模式按 typeCode 翻译成通用英文名
function dictLabel(d?: SensorDict) {
  if (!d) return '-'
  if (lang.lang === 'en' && d.typeCode && sensorTypeEn[d.typeCode]) {
    return sensorTypeEn[d.typeCode]
  }
  return d.typeName || '-'
}

function shipNameOf(id?: number) {
  if (id == null) return '-'
  return shipStore.shipMap.get(id)?.shipName || `#${id}`
}
function dictNameOf(id?: number) {
  if (id == null) return '-'
  const d = dictMap.value.get(id)
  return d ? dictLabel(d) : `#${id}`
}

async function loadOptions() {
  try {
    shipStore.loadShips() // parallel, non-blocking
    const dicts = await getAllSensorDicts()
    dictOptions.value = dicts || []
  } catch {
    /* ignore */
  }
}

async function reload() {
  loading.value = true
  try {
    const res = await getSensorConfigPage({
      page: page.value,
      count: size.value,
      shipId: filterShipId.value,
      typeId: filterTypeId.value,
      status: filterStatus.value,
      sensorName: filterName.value || undefined,
    })
    rows.value = res?.records || []
    total.value = res?.total || 0
  } catch {
    ElMessage.error(lang.t('common.loadFailed'))
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editing.value = false
  form.value = { status: 1 }
  dialogVisible.value = true
}

function openEdit(row: SensorConfig) {
  editing.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

async function save() {
  if (form.value.shipId == null || !form.value.sensorName?.trim()) {
    ElMessage.warning(lang.t('sc.selectShipRequired'))
    return
  }
  saving.value = true
  try {
    if (editing.value) {
      await updateSensorConfig(form.value)
      ElMessage.success(lang.t('common.updated'))
    } else {
      await addSensorConfig(form.value)
      ElMessage.success(lang.t('common.created'))
    }
    dialogVisible.value = false
    reload()
  } catch {
    ElMessage.error(lang.t('common.saveFailed'))
  } finally {
    saving.value = false
  }
}

async function confirmDelete(row: SensorConfig) {
  try {
    await ElMessageBox.confirm(lang.t('sc.deleteConfirm', { name: row.sensorName || '' }), lang.t('common.confirmDeletion'), { type: 'warning' })
  } catch {
    return
  }
  try {
    await deleteSensorConfig(row.id!)
    ElMessage.success(lang.t('common.deleted'))
    reload()
  } catch {
    ElMessage.error(lang.t('common.deleteFailed'))
  }
}

onMounted(() => {
  loadOptions()
  reload()
})
</script>

<style scoped>
.filter-bar { display: flex; gap: 10px; padding: 14px 16px; flex-wrap: wrap; align-items: center; }
.table-card { padding: 10px 14px 14px; }
.table-info { padding: 12px 0; display: flex; align-items: center; }
.result-count { font-size: 14px; color: #9ca3af; font-weight: 500; }
.pager { display: flex; justify-content: flex-end; margin-top: 12px; }
.empty-state { padding: 60px 0; display: flex; justify-content: center; align-items: center; min-height: 400px; }
</style>
