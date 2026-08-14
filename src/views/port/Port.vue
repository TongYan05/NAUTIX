<template>
  <div class="nx-page">
    <div class="nx-page-header">
      <div>
        <h2 class="nx-page-title">Port Management</h2>
        <p class="nx-page-desc">Maintain global port registry with country and type filters</p>
      </div>
      <el-button type="primary" @click="openCreate"><el-icon><Plus /></el-icon>Add Port</el-button>
    </div>

    <div class="nx-panel filter-bar">
      <el-input v-model="query.keyword" placeholder="Search port name" clearable style="width: 240px" @keyup.enter="reload" @clear="reload">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-select v-model="query.country" placeholder="Country" clearable filterable style="width: 180px" @change="reload">
        <el-option v-for="c in countryOptions" :key="c" :label="c" :value="c" />
      </el-select>
      <el-select v-model="sortField" placeholder="Sort By" clearable style="width: 150px" @change="reload">
        <el-option label="Port Name" value="port_name" />
        <el-option label="Country" value="country" />
        <el-option label="Max Draft" value="max_draft" />
      </el-select>
      <el-select v-model="sortOrder" style="width: 110px" @change="reload">
        <el-option label="Ascending" value="asc" />
        <el-option label="Descending" value="desc" />
      </el-select>
      <el-button @click="reload"><el-icon><Refresh /></el-icon>Refresh</el-button>
    </div>

    <div class="nx-panel table-card">
      <div v-if="!loading && rows.length === 0" class="empty-state">
        <el-empty description="No ports found">
          <template #image>
            <el-icon :size="60" color="#9ca3af"><Location /></el-icon>
          </template>
        </el-empty>
      </div>
      <template v-else>
        <div class="table-info">
          <span class="result-count">{{ total }} ports found</span>
        </div>
        <el-table v-loading="loading" :data="rows" stripe height="calc(100vh - 340px)">
        <el-table-column prop="id" label="ID" width="66" />
        <el-table-column prop="portName" label="Port Name" min-width="160" show-overflow-tooltip />
        <el-table-column prop="country" label="Country" width="110" />
        <el-table-column prop="countryCode" label="Country Code" width="100" />
        <el-table-column prop="portCode" label="Port Code" width="100" />
        <el-table-column prop="portType" label="Type" width="100">
          <template #default="{ row }">
            <el-tag size="small" effect="plain" type="primary">{{ row.portType || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Latitude" width="100">
          <template #default="{ row }">{{ row.latitude?.toFixed(4) ?? '-' }}</template>
        </el-table-column>
        <el-table-column label="Longitude" width="100">
          <template #default="{ row }">{{ row.longitude?.toFixed(4) ?? '-' }}</template>
        </el-table-column>
        <el-table-column prop="maxShipLength" label="Max LOA (m)" width="110" />
        <el-table-column prop="maxDraft" label="Max Draft (m)" width="110" />
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

    <el-dialog v-model="dialogVisible" :title="editing ? 'Edit Port' : 'Add Port'" width="620px" destroy-on-close>
      <el-form :model="form" label-width="100px">
        <div class="form-grid">
          <el-form-item label="Port Name" required>
            <el-input v-model="form.portName" />
          </el-form-item>
          <el-form-item label="Country">
            <el-input v-model="form.country" />
          </el-form-item>
          <el-form-item label="Country Code">
            <el-input v-model="form.countryCode" placeholder="e.g. AU" />
          </el-form-item>
          <el-form-item label="Port Code">
            <el-input v-model="form.portCode" />
          </el-form-item>
          <el-form-item label="Type">
            <el-input v-model="form.portType" placeholder="e.g. Seaport / River" />
          </el-form-item>
          <el-form-item label="Latitude">
            <el-input-number v-model="form.latitude" :precision="6" :min="-90" :max="90" style="width: 100%" />
          </el-form-item>
          <el-form-item label="Longitude">
            <el-input-number v-model="form.longitude" :precision="6" :min="-180" :max="180" style="width: 100%" />
          </el-form-item>
          <el-form-item label="Max LOA (m)">
            <el-input-number v-model="form.maxShipLength" :min="0" :precision="1" style="width: 100%" />
          </el-form-item>
          <el-form-item label="Max Draft (m)">
            <el-input-number v-model="form.maxDraft" :min="0" :precision="1" style="width: 100%" />
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
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addPort, deletePort, getPortPage, updatePort } from '@/api/port'
import type { Port } from '@/api/types'

const loading = ref(false)
const saving = ref(false)
const rows = ref<Port[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(20)

const query = reactive({ keyword: '', country: '' })
const sortField = ref('')
const sortOrder = ref('asc')

const countrySet = ref<Set<string>>(new Set())
const countryOptions = computed(() => Array.from(countrySet.value).sort())

const dialogVisible = ref(false)
const editing = ref(false)
const form = ref<Port>({})

async function reload() {
  loading.value = true
  try {
    const res = await getPortPage({
      page: page.value,
      count: size.value,
      keyword: query.keyword || undefined,
      country: query.country || undefined,
      sortField: sortField.value || undefined,
      sortOrder: sortField.value ? sortOrder.value : undefined,
    })
    rows.value = res?.records || []
    total.value = res?.total || 0
    for (const p of rows.value) {
      if (p.country) countrySet.value.add(p.country)
    }
  } catch {
    ElMessage.error('Failed to load port data. Please check the backend service.')
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editing.value = false
  form.value = {}
  dialogVisible.value = true
}

function openEdit(row: Port) {
  editing.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

async function save() {
  if (!form.value.portName?.trim()) {
    ElMessage.warning('Port name is required')
    return
  }
  saving.value = true
  try {
    if (editing.value) {
      await updatePort(form.value)
      ElMessage.success('Updated successfully')
    } else {
      await addPort(form.value)
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

async function confirmDelete(row: Port) {
  try {
    await ElMessageBox.confirm(`Are you sure to delete port "${row.portName}"?`, 'Confirm Deletion', { type: 'warning' })
  } catch {
    return
  }
  try {
    await deletePort(row.id!)
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
