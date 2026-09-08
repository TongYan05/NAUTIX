<template>
  <div class="nx-page">
    <div class="nx-page-header">
      <div>
        <h2 class="nx-page-title">{{ lang.t('route.title') }}</h2>
        <p class="nx-page-desc">{{ lang.t('route.desc') }}</p>
      </div>
      <el-button type="primary" round @click="openCreate"><el-icon><Plus /></el-icon>{{ lang.t('route.add') }}</el-button>
    </div>

    <div class="nx-panel filter-bar">
      <el-input v-model="keyword" :placeholder="lang.t('route.search')" clearable style="width: 240px" @keyup.enter="reload" @clear="reload">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-select-v2
        v-model="filterStartPortId"
        :options="portSelectOptions"
        :placeholder="lang.t('route.originPh')"
        clearable
        filterable
        style="width: 200px"
        @change="reload"
      />
      <el-select-v2
        v-model="filterEndPortId"
        :options="portSelectOptions"
        :placeholder="lang.t('route.destPh')"
        clearable
        filterable
        style="width: 200px"
        @change="reload"
      />
      <el-button round @click="reload"><el-icon><Refresh /></el-icon>{{ lang.t('common.refresh') }}</el-button>
      <span class="result-count">{{ lang.t('route.countFound', { n: total }) }}</span>
    </div>

    <div class="nx-panel table-card">
      <template v-if="!loading && rows.length === 0">
        <el-empty :description="lang.t('route.empty')">
          <template #image>
            <el-icon :size="60" color="#9ca3af"><Promotion /></el-icon>
          </template>
        </el-empty>
      </template>
      <template v-else>
        <el-table v-loading="loading" :data="rows" stripe height="calc(100vh - 330px)">
        <el-table-column prop="id" :label="lang.t('col.id')" width="70" />
        <el-table-column prop="routeName" :label="lang.t('col.routeName')" min-width="180" show-overflow-tooltip />
        <el-table-column :label="lang.t('col.originPort')" min-width="140">
          <template #default="{ row }">
            <el-tag size="small" effect="plain" type="success">{{ portNameOf(row.startPortId) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="lang.t('col.destPort')" min-width="140">
          <template #default="{ row }">
            <el-tag size="small" effect="plain" type="warning">{{ portNameOf(row.endPortId) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="lang.t('col.distanceNm')" width="120">
          <template #default="{ row }">
            <span class="dist-value">{{ row.distanceNm }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" :label="lang.t('col.description')" min-width="160" show-overflow-tooltip />
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
          layout="total, sizes, prev, pager, next"
          @size-change="reload"
          @current-change="reload"
        />
      </div>
      </template>
    </div>

    <el-dialog v-model="dialogVisible" :title="editing ? lang.t('route.editTitle') : lang.t('route.add')" width="520px" destroy-on-close>
      <el-form :model="form" label-width="110px">
        <el-form-item :label="lang.t('col.routeName')" required>
          <el-input v-model="form.routeName" :placeholder="lang.t('route.namePh')" />
        </el-form-item>
        <el-form-item :label="lang.t('col.originPort')">
          <el-select-v2
            v-model="form.startPortId"
            :options="portSelectOptions"
            :placeholder="lang.t('route.selectOrigin')"
            filterable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item :label="lang.t('col.destPort')">
          <el-select-v2
            v-model="form.endPortId"
            :options="portSelectOptions"
            :placeholder="lang.t('route.selectDest')"
            filterable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item :label="lang.t('col.distanceNm')">
          <el-input-number v-model="form.distanceNm" :min="0" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item :label="lang.t('col.description')">
          <el-input v-model="form.description" type="textarea" :rows="2" />
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
import { addRoute, deleteRoute, getRoutePage, updateRoute } from '@/api/ship'
import { getAllPorts } from '@/api/port'
import { useLang } from '@/stores/lang'
import type { Port, Route } from '@/api/types'

const lang = useLang()

const loading = ref(false)
const saving = ref(false)
const rows = ref<Route[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(20)
const keyword = ref('')
const filterStartPortId = ref<number>()
const filterEndPortId = ref<number>()

const dialogVisible = ref(false)
const editing = ref(false)
const form = ref<Route>({})

const portOptions = ref<Port[]>([])
const portSelectOptions = computed(() =>
  portOptions.value
    .filter(p => p.id != null)
    .map(p => ({ value: p.id as number, label: `${p.portName} (${p.id})` }))
)
const portMap = computed(() => {
  const m = new Map<number, string>()
  for (const p of portOptions.value) if (p.id != null) m.set(p.id, p.portName || `#${p.id}`)
  return m
})

function portNameOf(id?: number) {
  if (id == null) return '-'
  return portMap.value.get(id) || `#${id}`
}

async function loadPorts() {
  try {
    portOptions.value = (await getAllPorts()) || []
  } catch {
    portOptions.value = []
  }
}

async function reload() {
  loading.value = true
  try {
    const res = await getRoutePage({
      page: page.value,
      count: size.value,
      keyword: keyword.value || undefined,
      startPortId: filterStartPortId.value,
      endPortId: filterEndPortId.value,
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
  form.value = {}
  dialogVisible.value = true
}

function openEdit(row: Route) {
  editing.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

async function save() {
  if (!form.value.routeName?.trim()) {
    ElMessage.warning(lang.t('route.nameRequired'))
    return
  }
  saving.value = true
  try {
    if (editing.value) {
      await updateRoute(form.value)
      ElMessage.success(lang.t('common.updated'))
    } else {
      await addRoute(form.value)
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

async function confirmDelete(row: Route) {
  try {
    await ElMessageBox.confirm(lang.t('route.deleteConfirm', { name: row.routeName || '' }), lang.t('common.confirmDeletion'), { type: 'warning' })
  } catch {
    return
  }
  try {
    await deleteRoute(row.id!)
    ElMessage.success(lang.t('common.deleted'))
    reload()
  } catch {
    ElMessage.error(lang.t('common.deleteFailed'))
  }
}

onMounted(() => {
  loadPorts()
  reload()
})
</script>

<style scoped>
.filter-bar { display: flex; gap: 10px; padding: 14px 16px; align-items: center; }
.result-count { margin-left: auto; font-size: 14px; color: #9ca3af; font-weight: 500; }
.table-card { padding: 10px 14px 14px; }
.pager { display: flex; justify-content: flex-end; margin-top: 12px; }
.dist-value { color: #38bdf8; font-weight: 700; font-variant-numeric: tabular-nums; }
</style>
