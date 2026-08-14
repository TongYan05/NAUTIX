<template>
  <div class="nx-page">
    <div class="nx-page-header">
      <div>
        <h2 class="nx-page-title">Ship Management</h2>
        <p class="nx-page-desc">Maintain fleet registry with search, sorting and full CRUD operations</p>
      </div>
      <el-button type="primary" @click="openCreate">
        <el-icon><Plus /></el-icon>Add Ship
      </el-button>
    </div>

    <div class="nx-panel filter-bar">
      <el-input
        v-model="query.keyword"
        placeholder="Search name / IMO / port / company"
        clearable
        style="width: 300px"
        @keyup.enter="reload"
        @clear="reload"
      >
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-select v-model="query.shipType" placeholder="Ship Type" clearable style="width: 150px" @change="reload">
        <el-option v-for="t in shipTypes" :key="t" :label="t" :value="t" />
      </el-select>
      <el-select v-model="sortField" placeholder="Sort By" clearable style="width: 140px" @change="reload">
        <el-option label="Name" value="ship_name" />
        <el-option label="Build Year" value="build_year" />
        <el-option label="Length" value="length_overall" />
        <el-option label="Speed" value="sailing_speed" />
      </el-select>
      <el-select v-model="sortOrder" placeholder="Direction" style="width: 110px" @change="reload">
        <el-option label="Ascending" value="asc" />
        <el-option label="Descending" value="desc" />
      </el-select>
      <el-button @click="reload"><el-icon><Refresh /></el-icon>Refresh</el-button>
    </div>

    <div class="nx-panel table-card">
      <div v-if="!loading && rows.length === 0" class="empty-state">
        <el-empty description="No ships found">
          <template #image>
            <el-icon :size="60" color="#9ca3af"><Ship /></el-icon>
          </template>
          <el-button type="primary" @click="openCreate">Add First Ship</el-button>
        </el-empty>
      </div>
      <template v-else>
        <div class="table-info">
          <span class="result-count">{{ total }} ships found</span>
        </div>
        <el-table v-loading="loading" :data="rows" stripe height="calc(100vh - 390px)">
          <el-table-column prop="id" label="ID" width="66" />
          <el-table-column prop="shipName" label="Ship Name" min-width="130" show-overflow-tooltip />
          <el-table-column prop="imo" label="IMO" width="110" />
          <el-table-column prop="shipType" label="Type" width="120">
            <template #default="{ row }">
              <el-tag size="small" effect="plain" type="primary">{{ row.shipType || '-' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="registryPort" label="Registry Port" width="120" show-overflow-tooltip />
          <el-table-column prop="buildYear" label="Build Year" width="90" />
          <el-table-column prop="lengthOverall" label="LOA (m)" width="90" />
          <el-table-column prop="beam" label="Beam (m)" width="90" />
          <el-table-column prop="draft" label="Draft (m)" width="90" />
          <el-table-column prop="sailingSpeed" label="Speed (kn)" width="90" />
          <el-table-column prop="operatingCompany" label="Operator" min-width="130" show-overflow-tooltip />
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
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="reload"
            @current-change="reload"
          />
        </div>
      </template>
    </div>

    <el-dialog v-model="dialogVisible" :title="editing ? 'Edit Ship' : 'Add Ship'" width="640px" destroy-on-close>
      <el-form :model="form" label-width="100px" label-position="right">
        <div class="form-grid">
          <el-form-item label="Ship Name" required>
            <el-input v-model="form.shipName" placeholder="e.g. Pacific Explorer" />
          </el-form-item>
          <el-form-item label="IMO">
            <el-input v-model="form.imo" placeholder="7-digit IMO number" />
          </el-form-item>
          <el-form-item label="Ship Type">
            <el-select v-model="form.shipType" placeholder="Select type" filterable allow-create style="width: 100%">
              <el-option v-for="t in shipTypes" :key="t" :label="t" :value="t" />
            </el-select>
          </el-form-item>
          <el-form-item label="Registry Port">
            <el-input v-model="form.registryPort" />
          </el-form-item>
          <el-form-item label="Build Year">
            <el-input-number v-model="form.buildYear" :min="1900" :max="2100" style="width: 100%" />
          </el-form-item>
          <el-form-item label="LOA (m)">
            <el-input-number v-model="form.lengthOverall" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
          <el-form-item label="Beam (m)">
            <el-input-number v-model="form.beam" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
          <el-form-item label="Draft (m)">
            <el-input-number v-model="form.draft" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
          <el-form-item label="Displacement (t)">
            <el-input-number v-model="form.displacement" :min="0" :precision="1" style="width: 100%" />
          </el-form-item>
          <el-form-item label="Speed (kn)">
            <el-input-number v-model="form.sailingSpeed" :min="0" :precision="1" style="width: 100%" />
          </el-form-item>
          <el-form-item label="Engine Model">
            <el-input v-model="form.mainEngineModel" />
          </el-form-item>
          <el-form-item label="Shipyard">
            <el-input v-model="form.shipyardBuilder" />
          </el-form-item>
          <el-form-item label="Operator">
            <el-input v-model="form.operatingCompany" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="saving" @click="save">Save</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  addShip,
  deleteShip,
  getShipPage,
  updateShip,
} from '@/api/ship'
import type { ShipInfo } from '@/api/types'

const loading = ref(false)
const saving = ref(false)
const rows = ref<ShipInfo[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(20)

const query = reactive({ keyword: '', shipType: '' })
const sortField = ref('')
const sortOrder = ref('desc')

const dialogVisible = ref(false)
const editing = ref(false)
const form = ref<ShipInfo>({})

const shipTypes = [
  '集装箱船', '散货船', '油轮', 'LNG船',
  '滚装船', '拖轮', '渔船', '科考船',
]

async function reload() {
  loading.value = true
  try {
    const res = await getShipPage({
      page: page.value,
      count: size.value,
      keyword: query.keyword || undefined,
      shipType: query.shipType || undefined,
      sortField: sortField.value || undefined,
      sortOrder: sortField.value ? sortOrder.value : undefined,
    })
    rows.value = res?.records || []
    total.value = res?.total || 0
  } catch {
    ElMessage.error('Failed to load ship data. Please check the backend service.')
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editing.value = false
  form.value = {}
  dialogVisible.value = true
}

function openEdit(row: ShipInfo) {
  editing.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

async function save() {
  if (!form.value.shipName?.trim()) {
    ElMessage.warning('Ship name is required')
    return
  }
  saving.value = true
  try {
    if (editing.value) {
      await updateShip(form.value)
      ElMessage.success('Updated successfully')
    } else {
      await addShip(form.value)
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

async function confirmDelete(row: ShipInfo) {
  try {
    await ElMessageBox.confirm(
      `Are you sure to delete ship "${row.shipName}"? This action cannot be undone.`,
      'Confirm Deletion',
      { type: 'warning', confirmButtonText: 'Delete', cancelButtonText: 'Cancel' }
    )
  } catch {
    return
  }
  try {
    await deleteShip(row.id!)
    ElMessage.success('Deleted successfully')
    reload()
  } catch {
    ElMessage.error('Delete failed. Please check the backend service.')
  }
}

onMounted(reload)
</script>

<style scoped>
.filter-bar { display: flex; gap: 10px; padding: 14px 16px; flex-wrap: wrap; align-items: center; }
.table-card { padding: 10px 14px 14px; }
.table-info { padding: 12px 0; display: flex; align-items: center; }
.result-count { font-size: 14px; color: #9ca3af; font-weight: 500; }
.pager { display: flex; justify-content: flex-end; margin-top: 12px; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0 16px; }
.empty-state { padding: 60px 0; display: flex; justify-content: center; align-items: center; min-height: 400px; }
</style>
