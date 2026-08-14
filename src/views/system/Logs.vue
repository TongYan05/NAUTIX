<template>
  <div class="nx-page">
    <div class="nx-page-header">
      <div>
        <h2 class="nx-page-title">Operation Logs</h2>
        <p class="nx-page-desc">Track system data modification history</p>
      </div>
    </div>

    <div class="nx-panel filter-bar">
      <el-input v-model="filterUser" placeholder="User Name" clearable style="width: 180px" @keyup.enter="reload" @clear="reload" />
      <el-select v-model="filterTable" placeholder="Target Table" clearable filterable style="width: 220px" @change="reload">
        <el-option v-for="t in tableOptions" :key="t" :label="t" :value="t" />
      </el-select>
      <el-button @click="reload"><el-icon><Refresh /></el-icon>Refresh</el-button>
    </div>

    <div class="nx-panel table-card">
      <div v-if="!loading && rows.length === 0" class="empty-state">
        <el-empty description="No operation logs found">
          <template #image>
            <el-icon :size="60" color="#9ca3af"><Document /></el-icon>
          </template>
        </el-empty>
      </div>
      <template v-else>
        <div class="table-info">
          <span class="result-count">{{ total }} log entries found</span>
        </div>
        <el-table v-loading="loading" :data="rows" stripe height="calc(100vh - 320px)">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="userName" label="User" width="120" />
        <el-table-column prop="operationType" label="Operation" width="110">
          <template #default="{ row }">
            <el-tag :type="opTypeTag(row.operationType)" size="small" effect="dark">
              {{ row.operationType || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetTable" label="Target Table" width="160" />
        <el-table-column prop="targetId" label="Target ID" width="90" />
        <el-table-column prop="oldValue" label="Old Value" min-width="180" show-overflow-tooltip />
        <el-table-column prop="newValue" label="New Value" min-width="180" show-overflow-tooltip />
        <el-table-column prop="createTime" label="Timestamp" width="170" />
        <el-table-column label="Actions" width="90" fixed="right">
          <template #default="{ row }">
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
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteLog, getLogPage } from '@/api/system'
import type { SysOperationLog } from '@/api/types'

const loading = ref(false)
const rows = ref<SysOperationLog[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(20)
const filterUser = ref('')
const filterTable = ref('')

const tableSet = ref<Set<string>>(new Set())
const tableOptions = computed(() => Array.from(tableSet.value).sort())

function opTypeTag(type?: string) {
  const t = (type || '').toUpperCase()
  if (t.includes('INSERT') || t.includes('CREATE')) return 'success'
  if (t.includes('UPDATE')) return 'warning'
  if (t.includes('DELETE')) return 'danger'
  return 'info'
}

async function reload() {
  loading.value = true
  try {
    const res = await getLogPage({
      page: page.value,
      size: size.value,
      userName: filterUser.value || undefined,
      targetTable: filterTable.value || undefined,
    })
    rows.value = res?.records || []
    total.value = res?.total || 0
    for (const r of rows.value) {
      if (r.targetTable) tableSet.value.add(r.targetTable)
    }
  } catch {
    ElMessage.error('Failed to load operation logs. Please check the backend service.')
  } finally {
    loading.value = false
  }
}

async function confirmDelete(row: SysOperationLog) {
  try {
    await ElMessageBox.confirm('Are you sure to delete this log entry?', 'Confirm Deletion', { type: 'warning' })
  } catch {
    return
  }
  try {
    await deleteLog(row.id!)
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
.table-card { padding: 10px 14px 14px; }
.table-info { padding: 12px 0; display: flex; align-items: center; }
.result-count { font-size: 14px; color: #9ca3af; font-weight: 500; }
.pager { display: flex; justify-content: flex-end; margin-top: 12px; }
.empty-state { padding: 60px 0; display: flex; justify-content: center; align-items: center; min-height: 400px; }
</style>
