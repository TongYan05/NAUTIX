<template>
  <el-container class="main-layout">
    <!-- ================= 侧边栏 ================= -->
    <el-aside :width="isCollapse ? '72px' : '228px'" class="aside">
      <div class="aside-bg"></div>

      <div class="logo">
        <div class="logo-mark">
          <el-icon :size="22"><Compass /></el-icon>
        </div>
        <transition name="logo-fade">
          <div v-if="!isCollapse" class="logo-text-wrap">
            <span class="logo-text">NautiX</span>
            <span class="logo-sub">Maritime Sensing Platform</span>
          </div>
        </transition>
      </div>

      <el-scrollbar class="menu-scroll">
        <el-menu
          :default-active="currentRoute"
          :collapse="isCollapse"
          :router="true"
          class="nx-menu"
        >
          <el-menu-item index="/dashboard">
            <el-icon><DataLine /></el-icon>
            <template #title>{{ lang.t('nav.dashboard') }}</template>
          </el-menu-item>

          <el-menu-item index="/ship">
            <el-icon><Ship /></el-icon>
            <template #title>{{ lang.t('nav.ship') }}</template>
          </el-menu-item>

          <el-sub-menu index="sensor">
            <template #title>
              <el-icon><Odometer /></el-icon>
              <span>{{ lang.t('nav.sensorCenter') }}</span>
            </template>
            <el-menu-item index="/sensor/realtime">{{ lang.t('nav.sensorRealtime') }}</el-menu-item>
            <el-menu-item index="/sensor/config">{{ lang.t('nav.sensorConfig') }}</el-menu-item>
            <el-menu-item index="/sensor/dict">{{ lang.t('nav.sensorDict') }}</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="alert">
            <template #title>
              <el-icon><Bell /></el-icon>
              <span>{{ lang.t('nav.alertCenter') }}</span>
            </template>
            <el-menu-item index="/alert/records">{{ lang.t('nav.alertRecords') }}</el-menu-item>
            <el-menu-item index="/alert/rules">{{ lang.t('nav.alertRules') }}</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/route">
            <el-icon><Guide /></el-icon>
            <template #title>{{ lang.t('nav.route') }}</template>
          </el-menu-item>

          <el-menu-item index="/port">
            <el-icon><OfficeBuilding /></el-icon>
            <template #title>{{ lang.t('nav.port') }}</template>
          </el-menu-item>

          <el-menu-item index="/weather">
            <el-icon><Sunny /></el-icon>
            <template #title>{{ lang.t('nav.weather') }}</template>
          </el-menu-item>

          <el-menu-item index="/analytics">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>{{ lang.t('nav.analytics') }}</template>
          </el-menu-item>

          <el-menu-item index="/system/logs">
            <el-icon><Document /></el-icon>
            <template #title>{{ lang.t('nav.logs') }}</template>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>

      <div class="aside-foot">
        <span v-if="!isCollapse" class="foot-text">NautiX · v1.0</span>
      </div>
    </el-aside>

    <!-- ================= 主体 ================= -->
    <el-container class="body-col">
      <el-header class="header" height="60px">
        <div class="header-left">
          <div class="collapse-btn" @click="isCollapse = !isCollapse">
            <el-icon v-if="!isCollapse"><Fold /></el-icon>
            <el-icon v-else><Expand /></el-icon>
          </div>
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <el-tag :type="backendStatus.type" effect="dark" round class="status-tag">
            <span class="status-dot" :class="backendOnline ? 'dot-ok' : 'dot-bad'"></span>
            {{ backendStatus.text }}
          </el-tag>

          <button class="lang-switch" :title="lang.t('header.langTip')" @click="lang.toggle()">
            <span :class="{ active: lang.lang === 'zh' }">中</span>
            <span class="lang-divider">/</span>
            <span :class="{ active: lang.lang === 'en' }">EN</span>
          </button>

          <span class="clock">{{ currentTime }}</span>
          <el-divider direction="vertical" />
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <span class="user-avatar"><el-icon><User /></el-icon></span>
              {{ authStore.username }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  {{ lang.t('header.logout') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="page" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>

    <!-- 嵌入式智能客服浮标：右下角常驻，点击召唤 -->
    <AIAssistantFloat />
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { pingBackend } from '@/api/dashboard'
import { useAuthStore } from '@/stores/auth'
import { useLang } from '@/stores/lang'
import AIAssistantFloat from '@/components/ai/AIAssistantFloat.vue'

const route = useRoute()
const authStore = useAuthStore()
const lang = useLang()
const isCollapse = ref(false)
const currentTime = ref('')
const backendOnline = ref(false)

const currentRoute = computed(() => route.path)

// 页头标题跟随当前语言：英文取 meta.title，中文取 meta.titleZh
const pageTitle = computed(() => {
  const meta = route.meta as { title?: string; titleZh?: string }
  if (lang.lang === 'zh') {
    return meta.titleZh || meta.title || 'NautiX'
  }
  return meta.title || 'NautiX'
})

const backendStatus = computed(() => {
  return backendOnline.value
    ? { type: 'success' as const, text: lang.t('header.online') }
    : { type: 'danger' as const, text: lang.t('header.offline') }
})

const checkBackendHealth = async () => {
  try {
    await pingBackend()
    backendOnline.value = true
  } catch {
    backendOnline.value = false
  }
}

const updateClock = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString(lang.lang === 'zh' ? 'zh-CN' : 'en-AU', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false,
  })
}

let clockInterval: number
let healthInterval: number

const handleCommand = (command: string) => {
  if (command === 'logout') {
    authStore.logout()
  }
}

onMounted(() => {
  updateClock()
  clockInterval = window.setInterval(updateClock, 1000)

  checkBackendHealth()
  healthInterval = window.setInterval(checkBackendHealth, 30000)
})

onUnmounted(() => {
  clearInterval(clockInterval)
  clearInterval(healthInterval)
})
</script>

<style scoped>
.main-layout {
  height: 100vh;
  overflow: hidden;
}

/* ---------- 侧边栏：深空玻璃 ---------- */
.aside {
  position: relative;
  transition: width 0.28s cubic-bezier(0.4, 0, 0.2, 1);
  background: linear-gradient(180deg, rgba(9, 17, 35, 0.96) 0%, rgba(7, 13, 28, 0.98) 100%);
  border-right: 1px solid rgba(56, 189, 248, 0.12);
  display: flex;
  flex-direction: column;
  z-index: 2;
}
.aside-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
  background:
    radial-gradient(420px 200px at 0% 0%, rgba(56, 189, 248, 0.10), transparent 65%),
    radial-gradient(360px 240px at 100% 100%, rgba(124, 58, 237, 0.10), transparent 60%);
}

.logo {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 18px;
  min-height: 64px;
  border-bottom: 1px solid rgba(56, 189, 248, 0.10);
}
.logo-mark {
  flex: none;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #e0f2fe;
  background: linear-gradient(135deg, #0ea5e9, #2563eb 60%, #7c3aed);
  box-shadow: 0 6px 18px rgba(37, 99, 235, 0.45), 0 0 0 1px rgba(125, 211, 252, 0.3) inset;
}
.logo-text-wrap {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}
.logo-text {
  font-size: 21px;
  font-weight: 800;
  letter-spacing: 2px;
  background: linear-gradient(90deg, #7dd3fc, #a78bfa);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  line-height: 1.1;
}
.logo-sub {
  font-size: 10.5px;
  color: #64748b;
  letter-spacing: 0.6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.logo-fade-enter-active,
.logo-fade-leave-active {
  transition: opacity 0.18s ease;
}
.logo-fade-enter-from,
.logo-fade-leave-to {
  opacity: 0;
}

.menu-scroll {
  position: relative;
  flex: 1;
}
.nx-menu {
  border-right: none;
  background: transparent;
  padding: 10px 10px 16px;
}
.nx-menu :deep(.el-menu-item),
.nx-menu :deep(.el-sub-menu__title) {
  height: 44px;
  line-height: 44px;
  border-radius: 10px;
  margin: 3px 0;
  color: #94a3b8;
  font-size: 13.5px;
  transition: all 0.18s ease;
}
.nx-menu :deep(.el-menu-item:hover),
.nx-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(56, 189, 248, 0.08);
  color: #e0f2fe;
}
.nx-menu :deep(.el-menu-item.is-active) {
  color: #fff;
  background: linear-gradient(90deg, rgba(37, 99, 235, 0.5), rgba(56, 189, 248, 0.18));
  box-shadow: 0 4px 14px rgba(37, 99, 235, 0.3), 0 0 0 1px rgba(125, 211, 252, 0.25) inset;
}
.nx-menu :deep(.el-menu-item.is-active)::before {
  content: '';
  position: absolute;
  left: 0;
  top: 22%;
  bottom: 22%;
  width: 3px;
  border-radius: 3px;
  background: linear-gradient(180deg, #7dd3fc, #a78bfa);
  box-shadow: 0 0 8px rgba(125, 211, 252, 0.9);
}
.nx-menu :deep(.el-sub-menu .el-menu) {
  background: transparent;
}
.nx-menu :deep(.el-sub-menu .el-menu-item) {
  height: 38px;
  line-height: 38px;
  min-width: auto;
}
.aside-foot {
  position: relative;
  padding: 12px 18px;
  border-top: 1px solid rgba(56, 189, 248, 0.08);
}
.foot-text {
  font-size: 11px;
  color: #475569;
  letter-spacing: 1px;
}

/* ---------- 顶栏 ---------- */
.body-col {
  min-width: 0;
}
.header {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: rgba(10, 18, 34, 0.8);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(56, 189, 248, 0.10);
  z-index: 1;
}
/* 顶栏底部品牌光带 */
.header::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: -1px;
  height: 1.5px;
  background: linear-gradient(90deg, transparent, rgba(56, 189, 248, 0.55) 30%, rgba(167, 139, 250, 0.55) 70%, transparent);
  pointer-events: none;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
  min-width: 0;
}
.collapse-btn {
  width: 34px;
  height: 34px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  cursor: pointer;
  transition: all 0.15s ease;
}
.collapse-btn:hover {
  background: rgba(56, 189, 248, 0.12);
  color: #7dd3fc;
}
.page-title {
  font-size: 17px;
  font-weight: 700;
  color: #f1f5f9;
  letter-spacing: 0.4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 14px;
}
.status-tag :deep(.el-tag__content) {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  display: inline-block;
}
.dot-ok {
  background: #34d399;
  box-shadow: 0 0 8px rgba(52, 211, 153, 0.9);
  animation: dotPulse 2s ease-in-out infinite;
}
.dot-bad {
  background: #fb7185;
  box-shadow: 0 0 8px rgba(251, 113, 133, 0.9);
}
@keyframes dotPulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.45; }
}

/* 语言切换按钮 */
.lang-switch {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  padding: 5px 12px;
  border-radius: 999px;
  border: 1px solid rgba(125, 211, 252, 0.35);
  background: rgba(13, 31, 49, 0.7);
  color: #64748b;
  font-size: 12.5px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.18s ease;
  user-select: none;
}
.lang-switch:hover {
  border-color: rgba(125, 211, 252, 0.65);
  box-shadow: 0 0 14px rgba(56, 189, 248, 0.25);
}
.lang-switch span {
  transition: color 0.18s ease;
}
.lang-switch span.active {
  color: #7dd3fc;
}
.lang-divider {
  color: #334155;
  margin: 0 1px;
}

.clock {
  color: #94a3b8;
  font-size: 13px;
  font-family: 'JetBrains Mono', Consolas, monospace;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #cbd5e1;
  cursor: pointer;
  font-size: 13.5px;
}
.user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #bfdbfe;
  background: linear-gradient(135deg, rgba(56, 189, 248, 0.3), rgba(124, 58, 237, 0.3));
  border: 1px solid rgba(125, 211, 252, 0.3);
  font-size: 14px;
}
.user-info:hover {
  color: #7dd3fc;
}

/* ---------- 内容区 ---------- */
.main-content {
  background: transparent;
  padding: 0;
  overflow-y: auto;
}

/* 页面切换过渡 */
.page-enter-active {
  transition: opacity 0.22s ease, transform 0.22s ease;
}
.page-leave-active {
  transition: opacity 0.12s ease;
}
.page-enter-from {
  opacity: 0;
  transform: translateY(10px);
}
.page-leave-to {
  opacity: 0;
}
</style>
