<template>
  <div class="nx-page">
    <div class="nx-page-header">
      <div>
        <h2 class="nx-page-title">{{ lang.t('sd.title') }}</h2>
        <p class="nx-page-desc">{{ lang.t('sd.desc') }}</p>
      </div>
      <el-button type="primary" round @click="openCreate"><el-icon><Plus /></el-icon>{{ lang.t('sd.add') }}</el-button>
    </div>

    <div class="nx-panel filter-bar">
      <el-input v-model="keyword" :placeholder="lang.t('sd.search')" clearable style="width: 280px" @keyup.enter="reload" @clear="reload">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button round @click="reload"><el-icon><Refresh /></el-icon>{{ lang.t('common.refresh') }}</el-button>
      <span class="result-count">{{ lang.t('sd.countFound', { n: total }) }}</span>
    </div>

    <div v-loading="loading" class="dict-wrap">
      <el-empty v-if="!loading && rows.length === 0" :description="lang.t('sd.empty')">
        <template #image>
          <el-icon :size="60" color="#9ca3af"><Collection /></el-icon>
        </template>
      </el-empty>

      <div v-else class="dict-grid">
        <div v-for="row in rows" :key="row.id" class="dict-card nx-panel">
          <div class="dict-head">
            <div class="dict-icon"><el-icon :size="22"><Odometer /></el-icon></div>
            <div class="dict-title">
              <span class="dict-name">{{ typeNameOf(row) }}</span>
              <el-tag size="small" effect="dark" type="primary">{{ row.typeCode }}</el-tag>
            </div>
          </div>
          <div class="dict-unit">
            <span class="unit-value">{{ row.defaultUnit || '—' }}</span>
            <span class="unit-label">{{ lang.t('sd.defaultUnit') }}</span>
          </div>
          <p class="dict-desc">{{ row.description || lang.t('sd.noDesc') }}</p>
          <div class="dict-foot">
            <span class="dict-id">#{{ row.id }}</span>
            <span class="dict-actions">
              <el-button link type="primary" size="small" @click="openEdit(row)">{{ lang.t('common.edit') }}</el-button>
              <el-button link type="danger" size="small" @click="confirmDelete(row)">{{ lang.t('common.delete') }}</el-button>
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

    <el-dialog v-model="dialogVisible" :title="editing ? lang.t('sd.editTitle') : lang.t('sd.add')" width="500px" destroy-on-close>
      <el-form :model="form" label-width="100px">
        <el-form-item :label="lang.t('sd.typeCode')" required>
          <el-input v-model="form.typeCode" :placeholder="lang.t('sd.codePh')" />
        </el-form-item>
        <el-form-item :label="lang.t('sd.typeName')" required>
          <el-input v-model="form.typeName" :placeholder="lang.t('sd.namePh')" />
        </el-form-item>
        <el-form-item :label="lang.t('sd.defaultUnit')">
          <el-input v-model="form.defaultUnit" :placeholder="lang.t('sd.unitPh')" />
        </el-form-item>
        <el-form-item :label="lang.t('col.description')">
          <el-input v-model="form.description" type="textarea" :rows="3" />
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
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  addSensorDict,
  deleteSensorDict,
  getSensorDictPage,
  updateSensorDict,
} from '@/api/sensor'
import { useLang } from '@/stores/lang'
import { sensorTypeEn } from '@/i18n'
import type { SensorDict } from '@/api/types'

const lang = useLang()

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

// 英文模式下按 typeCode 翻译成英文名；中文模式直接用库中 typeName
function typeNameOf(row: SensorDict) {
  if (lang.lang === 'en' && row.typeCode && sensorTypeEn[row.typeCode]) {
    return sensorTypeEn[row.typeCode]
  }
  return row.typeName || '-'
}

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

function openEdit(row: SensorDict) {
  editing.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

async function save() {
  if (!form.value.typeCode?.trim() || !form.value.typeName?.trim()) {
    ElMessage.warning(lang.t('sd.codeRequired'))
    return
  }
  saving.value = true
  try {
    if (editing.value) {
      await updateSensorDict(form.value)
      ElMessage.success(lang.t('common.updated'))
    } else {
      await addSensorDict(form.value)
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

async function confirmDelete(row: SensorDict) {
  try {
    await ElMessageBox.confirm(lang.t('sd.deleteConfirm', { name: row.typeName || '' }), lang.t('common.confirmDeletion'), { type: 'warning' })
  } catch {
    return
  }
  try {
    await deleteSensorDict(row.id!)
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
