<template>
  <div class="nx-page">
    <div class="nx-page-header">
      <div>
        <h2 class="nx-page-title">{{ lang.t('lg.title') }}</h2>
        <p class="nx-page-desc">{{ lang.t('lg.desc') }}</p>
      </div>
      <el-button round @click="reload"><el-icon><Refresh /></el-icon>{{ lang.t('common.refresh') }}</el-button>
    </div>

    <div class="nx-panel filter-bar">
      <el-input v-model="filterUser" :placeholder="lang.t('lg.userPh')" clearable style="width: 180px" @keyup.enter="reload" @clear="reload" />
      <el-select v-model="filterTable" :placeholder="lang.t('lg.tablePh')" clearable filterable style="width: 220px" @change="reload">
        <el-option v-for="t in tableOptions" :key="t" :label="t" :value="t" />
      </el-select>
    </div>

    <div class="nx-panel table-card">
      <div v-if="!loading && rows.length === 0" class="empty-state">
        <el-empty :description="lang.t('lg.empty')">
          <template #image>
            <el-icon :size="60" color="#9ca3af"><Document /></el-icon>
          </template>
        </el-empty>
      </div>
      <template v-else>
        <div class="table-info">
          <span class="result-count">{{ lang.t('lg.countFound', { n: total }) }}</span>
        </div>
        <el-table v-loading="loading" :data="rows" stripe height="calc(100vh - 320px)">
        <el-table-column prop="id" :label="lang.t('col.id')" width="70" />
        <el-table-column prop="userName" :label="lang.t('col.userName')" width="120" />
        <el-table-column prop="operationType" :label="lang.t('col.operation')" width="110">
          <template #default="{ row }">
            <el-tag :type="opTypeTag(row.operationType)" size="small" effect="dark">
              {{ row.operationType || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetTable" :label="lang.t('col.targetTable')" width="160" />
        <el-table-column prop="targetId" :label="lang.t('col.targetId')" width="90" />
        <el-table-column prop="oldValue" :label="lang.t('col.oldValue')" min-width="180" show-overflow-tooltip />
        <el-table-column prop="newValue" :label="lang.t('col.newValue')" min-width="180" show-overflow-tooltip />
        <el-table-column prop="createTime" :label="lang.t('col.timestamp')" width="170" />
        <el-table-column :label="lang.t('common.actions')" width="90" fixed="right">
          <template #default="{ row }">
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
import { deleteLog, getLogPage } from '@/api/system'
import { useLang } from '@/stores/lang'
import type { SysOperationLog } from '@/api/types'

const lang = useLang()

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
    ElMessage.error(lang.t('common.loadFailed'))
  } finally {
    loading.value = false
  }
}

async function confirmDelete(row: SysOperationLog) {
  try {
    await ElMessageBox.confirm(lang.t('lg.deleteConfirm'), lang.t('common.confirmDeletion'), { type: 'warning' })
  } catch {
    return
  }
  try {
    await deleteLog(row.id!)
    ElMessage.success(lang.t('common.deleted'))
    reload()
  } catch {
    ElMessage.error(lang.t('common.deleteFailed'))
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
