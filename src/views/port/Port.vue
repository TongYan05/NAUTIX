<template>
  <div class="nx-page">
    <div class="nx-page-header">
      <div>
        <h2 class="nx-page-title">{{ lang.t('port.title') }}</h2>
        <p class="nx-page-desc">{{ lang.t('port.desc') }}</p>
      </div>
      <el-button type="primary" round @click="openCreate"><el-icon><Plus /></el-icon>{{ lang.t('port.add') }}</el-button>
    </div>

    <div class="nx-panel filter-bar">
      <el-input v-model="query.keyword" :placeholder="lang.t('port.search')" clearable style="width: 240px" @keyup.enter="reload" @clear="reload">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-select v-model="query.country" :placeholder="lang.t('col.country')" clearable filterable style="width: 180px" @change="reload">
        <el-option v-for="c in countryOptions" :key="c" :label="c" :value="c" />
      </el-select>
      <el-select v-model="sortField" :placeholder="lang.t('common.sortBy')" clearable style="width: 150px" @change="reload">
        <el-option :label="lang.t('port.sortName')" value="port_name" />
        <el-option :label="lang.t('port.sortCountry')" value="country" />
        <el-option :label="lang.t('port.sortDraft')" value="max_draft" />
      </el-select>
      <el-select v-model="sortOrder" style="width: 110px" @change="reload">
        <el-option :label="lang.t('common.asc')" value="asc" />
        <el-option :label="lang.t('common.desc')" value="desc" />
      </el-select>
      <el-button round @click="reload"><el-icon><Refresh /></el-icon>{{ lang.t('common.refresh') }}</el-button>
    </div>

    <div class="nx-panel table-card">
      <div v-if="!loading && rows.length === 0" class="empty-state">
        <el-empty :description="lang.t('port.empty')">
          <template #image>
            <el-icon :size="60" color="#9ca3af"><Location /></el-icon>
          </template>
        </el-empty>
      </div>
      <template v-else>
        <div class="table-info">
          <span class="result-count">{{ lang.t('port.countFound', { n: total }) }}</span>
        </div>
        <el-table v-loading="loading" :data="rows" stripe height="calc(100vh - 340px)">
        <el-table-column prop="id" :label="lang.t('col.id')" width="66" />
        <el-table-column prop="portName" :label="lang.t('col.portName')" min-width="160" show-overflow-tooltip />
        <el-table-column prop="country" :label="lang.t('col.country')" width="110" />
        <el-table-column prop="countryCode" :label="lang.t('col.countryCode')" width="100" />
        <el-table-column prop="portCode" :label="lang.t('col.portCode')" width="100" />
        <el-table-column prop="portType" :label="lang.t('common.type')" width="100">
          <template #default="{ row }">
            <el-tag size="small" effect="plain" type="primary">{{ row.portType || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="lang.t('col.latitude')" width="100">
          <template #default="{ row }">{{ row.latitude?.toFixed(4) ?? '-' }}</template>
        </el-table-column>
        <el-table-column :label="lang.t('col.longitude')" width="100">
          <template #default="{ row }">{{ row.longitude?.toFixed(4) ?? '-' }}</template>
        </el-table-column>
        <el-table-column prop="maxShipLength" :label="lang.t('col.maxLoa')" width="110" />
        <el-table-column prop="maxDraft" :label="lang.t('col.maxDraft')" width="110" />
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

    <el-dialog v-model="dialogVisible" :title="editing ? lang.t('port.editTitle') : lang.t('port.add')" width="620px" destroy-on-close>
      <el-form :model="form" label-width="100px">
        <div class="form-grid">
          <el-form-item :label="lang.t('col.portName')" required>
            <el-input v-model="form.portName" />
          </el-form-item>
          <el-form-item :label="lang.t('col.country')">
            <el-input v-model="form.country" />
          </el-form-item>
          <el-form-item :label="lang.t('col.countryCode')">
            <el-input v-model="form.countryCode" :placeholder="lang.t('port.countryPh')" />
          </el-form-item>
          <el-form-item :label="lang.t('col.portCode')">
            <el-input v-model="form.portCode" />
          </el-form-item>
          <el-form-item :label="lang.t('common.type')">
            <el-input v-model="form.portType" :placeholder="lang.t('port.typePh')" />
          </el-form-item>
          <el-form-item :label="lang.t('col.latitude')">
            <el-input-number v-model="form.latitude" :precision="6" :min="-90" :max="90" style="width: 100%" />
          </el-form-item>
          <el-form-item :label="lang.t('col.longitude')">
            <el-input-number v-model="form.longitude" :precision="6" :min="-180" :max="180" style="width: 100%" />
          </el-form-item>
          <el-form-item :label="lang.t('col.maxLoa')">
            <el-input-number v-model="form.maxShipLength" :min="0" :precision="1" style="width: 100%" />
          </el-form-item>
          <el-form-item :label="lang.t('col.maxDraft')">
            <el-input-number v-model="form.maxDraft" :min="0" :precision="1" style="width: 100%" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button round @click="dialogVisible = false">{{ lang.t('common.cancel') }}</el-button>
        <el-button type="primary" round :loading="saving" @click="save">{{ lang.t('common.save') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addPort, deletePort, getPortPage, updatePort } from '@/api/port'
import { useLang } from '@/stores/lang'
import type { Port } from '@/api/types'

const lang = useLang()

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

function openEdit(row: Port) {
  editing.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

async function save() {
  if (!form.value.portName?.trim()) {
    ElMessage.warning(lang.t('port.nameRequired'))
    return
  }
  saving.value = true
  try {
    if (editing.value) {
      await updatePort(form.value)
      ElMessage.success(lang.t('common.updated'))
    } else {
      await addPort(form.value)
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

async function confirmDelete(row: Port) {
  try {
    await ElMessageBox.confirm(lang.t('port.deleteConfirm', { name: row.portName || '' }), lang.t('common.confirmDeletion'), { type: 'warning' })
  } catch {
    return
  }
  try {
    await deletePort(row.id!)
    ElMessage.success(lang.t('common.deleted'))
    reload()
  } catch {
    ElMessage.error(lang.t('common.deleteFailed'))
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
