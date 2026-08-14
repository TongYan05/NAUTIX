<template>
  <div class="nx-page">
    <div class="nx-page-header">
      <div>
        <h2 class="nx-page-title">Route Management</h2>
        <p class="nx-page-desc">Maintain inter-port shipping routes and navigation distances</p>
      </div>
      <el-button type="primary" @click="openCreate"><el-icon><Plus /></el-icon>Add Route</el-button>
    </div>

    <div class="nx-panel filter-bar">
      <el-input v-model="keyword" placeholder="Search route name" clearable style="width: 240px" @keyup.enter="reload" @clear="reload">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-select-v2
        v-model="filterStartPortId"
        :options="portSelectOptions"
        placeholder="Origin Port"
        clearable
        filterable
        style="width: 200px"
        @change="reload"
      />
      <el-select-v2
        v-model="filterEndPortId"
        :options="portSelectOptions"
        placeholder="Destination Port"
        clearable
        filterable
        style="width: 200px"
        @change="reload"
      />
      <el-button @click="reload"><el-icon><Refresh /></el-icon>Refresh</el-button>
    </div>

    <div class="nx-panel table-card">
      <template v-if="!loading && rows.length === 0">
        <el-empty description="No routes found">
          <template #image>
            <el-icon :size="60" color="#9ca3af"><Promotion /></el-icon>
          </template>
        </el-empty>
      </template>
      <template v-else>
        <div class="table-info">
          <span class="result-count">{{ total }} routes found</span>
        </div>
        <el-table v-loading="loading" :data="rows" stripe height="calc(100vh - 330px)">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="routeName" label="Route Name" min-width="180" show-overflow-tooltip />
        <el-table-column label="Origin Port" min-width="140">
          <template #default="{ row }">
            <el-tag size="small" effect="plain" type="success">{{ portNameOf(row.startPortId) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Destination Port" min-width="140">
          <template #default="{ row }">
            <el-tag size="small" effect="plain" type="warning">{{ portNameOf(row.endPortId) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Distance (nm)" width="120">
          <template #default="{ row }">
            <span class="dist-value">{{ row.distanceNm }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="Description" min-width="160" show-overflow-tooltip />
        <el-table-column label="Actions" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">Edit</el-button>
            <el-button link type="danger" size="small" @click="confirmDelete(row)">Delete</el-button>
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

    <el-dialog v-model="dialogVisible" :title="editing ? 'Edit Route' : 'Add Route'" width="520px" destroy-on-close>
      <el-form :model="form" label-width="110px">
        <el-form-item label="Route Name" required>
          <el-input v-model="form.routeName" placeholder="e.g. Shanghai to Singapore" />
        </el-form-item>
        <el-form-item label="Origin Port">
          <el-select-v2
            v-model="form.startPortId"
            :options="portSelectOptions"
            placeholder="Select origin port"
            filterable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="Dest. Port">
          <el-select-v2
            v-model="form.endPortId"
            :options="portSelectOptions"
            placeholder="Select destination port"
            filterable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="Distance (nm)">
          <el-input-number v-model="form.distanceNm" :min="0" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="form.description" type="textarea" :rows="2" />
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
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addRoute, deleteRoute, getRoutePage, updateRoute } from '@/api/ship'
import { getAllPorts } from '@/api/port'
import type { Port, Route } from '@/api/types'

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
    ElMessage.error('Failed to load route data. Please check the backend service.')
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
    ElMessage.warning('Route name is required')
    return
  }
  saving.value = true
  try {
    if (editing.value) {
      await updateRoute(form.value)
      ElMessage.success('Updated successfully')
    } else {
      await addRoute(form.value)
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

async function confirmDelete(row: Route) {
  try {
    await ElMessageBox.confirm(`Are you sure to delete route "${row.routeName}"?`, 'Confirm Deletion', { type: 'warning' })
  } catch {
    return
  }
  try {
    await deleteRoute(row.id!)
    ElMessage.success('Deleted successfully')
    reload()
  } catch {
    ElMessage.error('Delete failed. Please check the backend service.')
  }
}

onMounted(() => {
  loadPorts()
  reload()
})
</script>

<style scoped>
.filter-bar { display: flex; gap: 10px; padding: 14px 16px; align-items: center; }
.table-card { padding: 10px 14px 14px; }
.table-info { padding: 12px 0; display: flex; align-items: center; }
.result-count { font-size: 14px; color: #9ca3af; font-weight: 500; }
.pager { display: flex; justify-content: flex-end; margin-top: 12px; }
.dist-value { color: #38bdf8; font-weight: 700; font-variant-numeric: tabular-nums; }
</style>
