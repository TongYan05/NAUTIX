<template>
  <div class="nx-page">
    <div class="nx-page-header">
      <div>
        <h2 class="nx-page-title">Sensor Dictionary</h2>
        <p class="nx-page-desc">Define sensor type codes, default units and descriptions</p>
      </div>
      <el-button type="primary" @click="openCreate"><el-icon><Plus /></el-icon>Add Type</el-button>
    </div>

    <div class="nx-panel filter-bar">
      <el-input v-model="keyword" placeholder="Search type name / code" clearable style="width: 280px" @keyup.enter="reload" @clear="reload">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button @click="reload"><el-icon><Refresh /></el-icon>Refresh</el-button>
      <span class="result-count">{{ total }} sensor types found</span>
    </div>

    <div v-loading="loading" class="dict-wrap">
      <el-empty v-if="!loading && rows.length === 0" description="No sensor types found">
        <template #image>
          <el-icon :size="60" color="#9ca3af"><Collection /></el-icon>
        </template>
      </el-empty>

      <div v-else class="dict-grid">
        <div v-for="row in rows" :key="row.id" class="dict-card nx-panel">
          <div class="dict-head">
            <div class="dict-icon"><el-icon :size="22"><Odometer /></el-icon></div>
            <div class="dict-title">
              <span class="dict-name">{{ row.typeName || '-' }}</span>
              <el-tag size="small" effect="dark" type="primary">{{ row.typeCode }}</el-tag>
            </div>
          </div>
          <div class="dict-unit">
            <span class="unit-value">{{ row.defaultUnit || '—' }}</span>
            <span class="unit-label">Default Unit</span>
          </div>
          <p class="dict-desc">{{ row.description || 'No description provided.' }}</p>
          <div class="dict-foot">
            <span class="dict-id">#{{ row.id }}</span>
            <span class="dict-actions">
              <el-button link type="primary" size="small" @click="openEdit(row)">Edit</el-button>
              <el-button link type="danger" size="small" @click="confirmDelete(row)">Delete</el-button>
            </span>
          </div>
        </div>
      </div>

      <div class="pager">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[12, 24, 48]"
          layout="total, sizes, prev, pager, next"
          @size-change="reload"
          @current-change="reload"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="editing ? 'Edit Type' : 'Add Type'" width="500px" destroy-on-close>
      <el-form :model="form" label-width="100px">
        <el-form-item label="Type Code" required>
          <el-input v-model="form.typeCode" placeholder="e.g. TEMP_EXHAUST" />
        </el-form-item>
        <el-form-item label="Type Name" required>
          <el-input v-model="form.typeName" placeholder="e.g. Exhaust Temperature" />
        </el-form-item>
        <el-form-item label="Default Unit">
          <el-input v-model="form.defaultUnit" placeholder="e.g. ℃ / kn / bar" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
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
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  addSensorDict,
  deleteSensorDict,
  getSensorDictPage,
  updateSensorDict,
} from '@/api/sensor'
import type { SensorDict } from '@/api/types'

const loading = ref(false)
const saving = ref(false)
const rows = ref<SensorDict[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(12)
const keyword = ref('')

const dialogVisible = ref(false)
const editing = ref(false)
const form = ref<SensorDict>({})

async function reload() {
  loading.value = true
  try {
    const res = await getSensorDictPage({
      page: page.value,
      count: size.value,
      keyword: keyword.value || undefined,
    })
    rows.value = res?.records || []
    total.value = res?.total || 0
  } catch {
    ElMessage.error('Failed to load sensor dictionary. Please check the backend service.')
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editing.value = false
  form.value = {}
  dialogVisible.value = true
}

function openEdit(row: SensorDict) {
  editing.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

async function save() {
  if (!form.value.typeCode?.trim() || !form.value.typeName?.trim()) {
    ElMessage.warning('Type code and name are required')
    return
  }
  saving.value = true
  try {
    if (editing.value) {
      await updateSensorDict(form.value)
      ElMessage.success('Updated successfully')
    } else {
      await addSensorDict(form.value)
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

async function confirmDelete(row: SensorDict) {
  try {
    await ElMessageBox.confirm(`Are you sure to delete type "${row.typeName}"?`, 'Confirm Deletion', { type: 'warning' })
  } catch {
    return
  }
  try {
    await deleteSensorDict(row.id!)
    ElMessage.success('Deleted successfully')
    reload()
  } catch {
    ElMessage.error('Delete failed. Please check the backend service.')
  }
}

onMounted(reload)
</script>

<style scoped>
.filter-bar { display: flex; gap: 10px; padding: 14px 16px; align-items: center; }
.result-count { margin-left: auto; font-size: 14px; color: #9ca3af; font-weight: 500; }

.dict-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  margin-top: 14px;
}

.dict-card {
  padding: 18px 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.dict-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 28px rgba(2, 8, 23, 0.55);
}

.dict-head { display: flex; align-items: center; gap: 12px; }
.dict-icon {
  width: 44px; height: 44px; flex: none;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  color: #38bdf8;
  background: rgba(56, 189, 248, 0.12);
  border: 1px solid rgba(56, 189, 248, 0.25);
}
.dict-title { display: flex; flex-direction: column; gap: 4px; min-width: 0; }
.dict-name { font-size: 15px; font-weight: 600; color: #f9fafb; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.dict-unit {
  display: flex; flex-direction: column; align-items: center; gap: 2px;
  padding: 10px 0;
  border-radius: 10px;
  background: rgba(56, 189, 248, 0.06);
  border: 1px solid rgba(56, 189, 248, 0.12);
}
.unit-value { font-size: 22px; font-weight: 700; color: #38bdf8; font-variant-numeric: tabular-nums; }
.unit-label { font-size: 12px; color: #9ca3af; }

.dict-desc {
  margin: 0;
  font-size: 13px; color: #9ca3af; line-height: 1.55;
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
  min-height: 40px;
}

.dict-foot { display: flex; align-items: center; justify-content: space-between; }
.dict-id { font-size: 12px; color: #6b7280; }
.dict-actions { display: flex; gap: 4px; }

.pager { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
