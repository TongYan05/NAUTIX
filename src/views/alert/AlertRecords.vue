<template>
  <div class="nx-page">
    <div class="nx-page-header">
      <div>
        <h2 class="nx-page-title">{{ lang.t('ar.title') }}</h2>
        <p class="nx-page-desc">{{ lang.t('ar.desc') }}</p>
      </div>
      <el-button round @click="reload"><el-icon><Refresh /></el-icon>{{ lang.t('common.refresh') }}</el-button>
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
      <el-select v-model="filterStatus" :placeholder="lang.t('common.status')" clearable style="width: 140px" @change="reload">
        <el-option :label="lang.t('common.pending')" :value="0" />
        <el-option :label="lang.t('common.handled')" :value="1" />
      </el-select>
    </div>

    <div class="nx-panel table-card">
      <div v-if="!loading && rows.length === 0" class="empty-state">
        <el-empty :description="lang.t('ar.empty')">
          <template #image>
            <el-icon :size="60" color="#9ca3af"><Bell /></el-icon>
          </template>
        </el-empty>
      </div>
      <template v-else>
        <div class="table-info">
          <span class="result-count">{{ lang.t('ar.countFound', { n: total }) }}</span>
        </div>
        <el-table v-loading="loading" :data="rows" stripe height="calc(100vh - 370px)">
          <el-table-column prop="id" :label="lang.t('col.id')" width="80" />
          <el-table-column prop="shipId" :label="lang.t('col.ship')" width="140">
            <template #default="{ row }">
              <el-tag size="small" effect="plain">{{ shipNameOf(row.shipId) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="ruleId" :label="lang.t('col.ruleId')" width="90" />
          <el-table-column prop="triggerValue" :label="lang.t('col.triggerValue')" width="120">
            <template #default="{ row }">
              <span class="trigger-value">{{ row.triggerValue }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="alertTime" :label="lang.t('col.alertTime')" min-width="170" />
          <el-table-column :label="lang.t('common.status')" width="110">
            <template #default="{ row }">
              <el-tag :type="row.handleStatus === 1 ? 'success' : 'danger'" effect="dark" size="small">
                {{ row.handleStatus === 1 ? lang.t('common.handled') : lang.t('common.pending') }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column :label="lang.t('common.actions')" width="180" fixed="right">
            <template #default="{ row }">
              <el-button
                v-if="row.handleStatus !== 1"
                link type="success" size="small"
                @click="markHandled(row)"
              >{{ lang.t('ar.markHandled') }}</el-button>
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
            layout="total, sizes, prev, pager, next"
            @size-change="reload"
            @current-change="reload"
          />
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteAlertRecord, getAlertRecordPage, updateAlertRecord } from '@/api/alert'
import { useShipStore } from '@/stores/shipStore'
import { useLang } from '@/stores/lang'
import type { AlertRecord } from '@/api/types'

const lang = useLang()
const shipStore = useShipStore()

const shipOptions = computed(() =>
  shipStore.ships.map(s => ({ value: s.id as number, label: `${s.shipName} (${s.id})` }))
)

const loading = ref(false)
const rows = ref<AlertRecord[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(20)
const filterShipId = ref<number>()
const filterStatus = ref<number>()

function shipNameOf(id?: number) {
  if (id == null) return '-'
  return shipStore.shipMap.get(id)?.shipName || `#${id}`
}

async function reload() {
  loading.value = true
  try {
    const res = await getAlertRecordPage({
      page: page.value,
      size: size.value,
      shipId: filterShipId.value,
      handleStatus: filterStatus.value,
    })
    rows.value = res?.records || []
    total.value = res?.total || 0
  } catch {
    ElMessage.error(lang.t('common.loadFailed'))
  } finally {
    loading.value = false
  }
}

async function markHandled(row: AlertRecord) {
  try {
    await updateAlertRecord({ ...row, handleStatus: 1 })
    ElMessage.success(lang.t('ar.marked'))
    reload()
  } catch {
    ElMessage.error(lang.t('common.opFailed'))
  }
}

async function confirmDelete(row: AlertRecord) {
  try {
    await ElMessageBox.confirm(lang.t('ar.deleteConfirm'), lang.t('common.confirmDeletion'), { type: 'warning' })
  } catch {
    return
  }
  try {
    await deleteAlertRecord(row.id!)
    ElMessage.success(lang.t('common.deleted'))
    reload()
  } catch {
    ElMessage.error(lang.t('common.deleteFailed'))
  }
}

onMounted(() => {
  // 并行加载：不等待 ships 加载完成
  shipStore.loadShips()
  reload()
})
</script>

<style scoped>
.filter-bar { display: flex; gap: 10px; padding: 14px 16px; align-items: center; }
.table-card { padding: 10px 14px 14px; }
.table-info { padding: 12px 0; display: flex; align-items: center; }
.result-count { font-size: 14px; color: #9ca3af; font-weight: 500; }
.pager { display: flex; justify-content: flex-end; margin-top: 12px; }
.trigger-value { color: #fbbf24; font-weight: 700; font-variant-numeric: tabular-nums; }
.empty-state { padding: 60px 0; display: flex; justify-content: center; align-items: center; min-height: 400px; }
</style>
