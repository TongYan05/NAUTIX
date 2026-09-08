<template>
  <div class="nx-page">
    <div class="nx-page-header">
      <div>
        <h2 class="nx-page-title">{{ lang.t('ship.title') }}</h2>
        <p class="nx-page-desc">{{ lang.t('ship.desc') }}</p>
      </div>
      <el-button type="primary" round @click="openCreate">
        <el-icon><Plus /></el-icon>{{ lang.t('ship.add') }}
      </el-button>
    </div>

    <div class="nx-panel filter-bar">
      <el-input
        v-model="query.keyword"
        :placeholder="lang.t('ship.search')"
        clearable
        style="width: 300px"
        @keyup.enter="reload"
        @clear="reload"
      >
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-select v-model="query.shipType" :placeholder="lang.t('common.type')" clearable style="width: 150px" @change="reload">
        <el-option v-for="o in shipTypeOptions" :key="o.value" :label="lang.t(o.key)" :value="o.value" />
      </el-select>
      <el-select v-model="sortField" :placeholder="lang.t('common.sortBy')" clearable style="width: 140px" @change="reload">
        <el-option :label="lang.t('ship.sortName')" value="ship_name" />
        <el-option :label="lang.t('ship.sortYear')" value="build_year" />
        <el-option :label="lang.t('ship.sortLength')" value="length_overall" />
        <el-option :label="lang.t('ship.sortSpeed')" value="sailing_speed" />
      </el-select>
      <el-select v-model="sortOrder" style="width: 110px" @change="reload">
        <el-option :label="lang.t('common.asc')" value="asc" />
        <el-option :label="lang.t('common.desc')" value="desc" />
      </el-select>
      <el-button round @click="reload"><el-icon><Refresh /></el-icon>{{ lang.t('common.refresh') }}</el-button>
    </div>

    <div class="nx-panel table-card">
      <div v-if="!loading && rows.length === 0" class="empty-state">
        <el-empty :description="lang.t('ship.empty')">
          <template #image>
            <el-icon :size="60" color="#9ca3af"><Ship /></el-icon>
          </template>
          <el-button type="primary" round @click="openCreate">{{ lang.t('ship.addFirst') }}</el-button>
        </el-empty>
      </div>
      <template v-else>
        <div class="table-info">
          <span class="result-count">{{ lang.t('ship.countFound', { n: total }) }}</span>
        </div>
        <el-table v-loading="loading" :data="rows" stripe height="calc(100vh - 390px)">
          <el-table-column prop="id" :label="lang.t('col.id')" width="66" />
          <el-table-column prop="shipName" :label="lang.t('col.shipName')" min-width="130" show-overflow-tooltip />
          <el-table-column prop="imo" :label="lang.t('col.imo')" width="110" />
          <el-table-column prop="shipType" :label="lang.t('common.type')" width="120">
            <template #default="{ row }">
              <el-tag size="small" effect="plain" type="primary">{{ shipTypeLabel(row.shipType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="registryPort" :label="lang.t('col.registryPort')" width="120" show-overflow-tooltip />
          <el-table-column prop="buildYear" :label="lang.t('col.buildYear')" width="90" />
          <el-table-column prop="lengthOverall" :label="lang.t('col.loa')" width="90" />
          <el-table-column prop="beam" :label="lang.t('col.beam')" width="90" />
          <el-table-column prop="draft" :label="lang.t('col.draft')" width="90" />
          <el-table-column prop="sailingSpeed" :label="lang.t('col.speed')" width="90" />
          <el-table-column prop="operatingCompany" :label="lang.t('col.operator')" min-width="130" show-overflow-tooltip />
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

    <el-dialog v-model="dialogVisible" :title="editing ? lang.t('ship.editTitle') : lang.t('ship.add')" width="640px" destroy-on-close>
      <el-form :model="form" label-width="100px" label-position="right">
        <div class="form-grid">
          <el-form-item :label="lang.t('col.shipName')" required>
            <el-input v-model="form.shipName" :placeholder="lang.t('ship.namePh')" />
          </el-form-item>
          <el-form-item :label="lang.t('col.imo')">
            <el-input v-model="form.imo" :placeholder="lang.t('ship.imoPh')" />
          </el-form-item>
          <el-form-item :label="lang.t('common.type')">
            <el-select v-model="form.shipType" :placeholder="lang.t('common.type')" filterable allow-create style="width: 100%">
              <el-option v-for="o in shipTypeOptions" :key="o.value" :label="lang.t(o.key)" :value="o.value" />
            </el-select>
          </el-form-item>
          <el-form-item :label="lang.t('col.registryPort')">
            <el-input v-model="form.registryPort" />
          </el-form-item>
          <el-form-item :label="lang.t('col.buildYear')">
            <el-input-number v-model="form.buildYear" :min="1900" :max="2100" style="width: 100%" />
          </el-form-item>
          <el-form-item :label="lang.t('col.loa')">
            <el-input-number v-model="form.lengthOverall" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
          <el-form-item :label="lang.t('col.beam')">
            <el-input-number v-model="form.beam" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
          <el-form-item :label="lang.t('col.draft')">
            <el-input-number v-model="form.draft" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
          <el-form-item :label="lang.t('ship.displacement')">
            <el-input-number v-model="form.displacement" :min="0" :precision="1" style="width: 100%" />
          </el-form-item>
          <el-form-item :label="lang.t('col.speed')">
            <el-input-number v-model="form.sailingSpeed" :min="0" :precision="1" style="width: 100%" />
          </el-form-item>
          <el-form-item :label="lang.t('ship.engineModel')">
            <el-input v-model="form.mainEngineModel" />
          </el-form-item>
          <el-form-item :label="lang.t('ship.shipyard')">
            <el-input v-model="form.shipyardBuilder" />
          </el-form-item>
          <el-form-item :label="lang.t('col.operator')">
            <el-input v-model="form.operatingCompany" />
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
import {
  addShip,
  deleteShip,
  getShipPage,
  updateShip,
} from '@/api/ship'
import { useLang } from '@/stores/lang'
import type { MsgKey } from '@/i18n'
import type { ShipInfo } from '@/api/types'

const lang = useLang()

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

// 库中 ship_type 存中文值：查询/保存一律用中文 value，展示按语言翻译 label
const shipTypeOptions: { key: MsgKey; value: string }[] = [
  { key: 'ship.typeContainer', value: '集装箱船' },
  { key: 'ship.typeCargo', value: '散货船' },
  { key: 'ship.typeTanker', value: '油轮' },
  { key: 'ship.typeLng', value: 'LNG船' },
  { key: 'ship.typeRoRo', value: '滚装船' },
  { key: 'ship.typeTug', value: '拖轮' },
  { key: 'ship.typeFish', value: '渔船' },
  { key: 'ship.typeResearch', value: '科考船' },
]

const typeLabelMap = computed(() => {
  const m = new Map<string, string>()
  for (const o of shipTypeOptions) m.set(o.value, lang.t(o.key))
  return m
})

function shipTypeLabel(zhType?: string) {
  if (!zhType) return '-'
  if (lang.lang === 'zh') return zhType
  return typeLabelMap.value.get(zhType) || zhType
}

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

function openEdit(row: ShipInfo) {
  editing.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

async function save() {
  if (!form.value.shipName?.trim()) {
    ElMessage.warning(lang.t('ship.nameRequired'))
    return
  }
  saving.value = true
  try {
    if (editing.value) {
      await updateShip(form.value)
      ElMessage.success(lang.t('common.updated'))
    } else {
      await addShip(form.value)
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

async function confirmDelete(row: ShipInfo) {
  try {
    await ElMessageBox.confirm(
      lang.t('ship.deleteConfirm', { name: row.shipName || '' }),
      lang.t('common.confirmDeletion'),
      { type: 'warning', confirmButtonText: lang.t('common.delete'), cancelButtonText: lang.t('common.cancel') }
    )
  } catch {
    return
  }
  try {
    await deleteShip(row.id!)
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
