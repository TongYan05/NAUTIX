<template>
  <el-container class="main-layout">
    <el-aside :width="isCollapse ? '64px' : '200px'" class="aside">
      <div class="logo">
        <span v-if="!isCollapse" class="logo-text">NautiX</span>
        <span v-if="!isCollapse" class="logo-sub">Maritime Sensing Platform</span>
      </div>
      <el-menu
        :default-active="currentRoute"
        :collapse="isCollapse"
        :router="true"
        background-color="#1f2937"
        text-color="#9ca3af"
        active-text-color="#60a5fa"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <template #title>Dashboard</template>
        </el-menu-item>

        <el-menu-item index="/ship">
          <el-icon><Ship /></el-icon>
          <template #title>Ship Management</template>
        </el-menu-item>

        <el-sub-menu index="sensor">
          <template #title>
            <el-icon><Odometer /></el-icon>
            <span>Sensor Center</span>
          </template>
          <el-menu-item index="/sensor/realtime">Realtime Data</el-menu-item>
          <el-menu-item index="/sensor/config">Configuration</el-menu-item>
          <el-menu-item index="/sensor/dict">Dictionary</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="alert">
          <template #title>
            <el-icon><Bell /></el-icon>
            <span>Alert Center</span>
          </template>
          <el-menu-item index="/alert/records">Alert Records</el-menu-item>
          <el-menu-item index="/alert/rules">Alert Rules</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/route">
          <el-icon><Guide /></el-icon>
          <template #title>Route Management</template>
        </el-menu-item>

        <el-menu-item index="/port">
          <el-icon><OfficeBuilding /></el-icon>
          <template #title>Port Management</template>
        </el-menu-item>

        <el-menu-item index="/weather">
          <el-icon><Sunny /></el-icon>
          <template #title>Weather Monitor</template>
        </el-menu-item>

        <el-menu-item index="/analytics">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>Analytics Center</template>
        </el-menu-item>

        <el-menu-item index="/system/logs">
          <el-icon><Document /></el-icon>
          <template #title>Operation Logs</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <el-tag :type="backendStatus.type" effect="dark">
            {{ backendStatus.text }}
          </el-tag>
          <span class="clock">{{ currentTime }}</span>
          <el-divider direction="vertical" />
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ authStore.username }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  Logout
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { pingBackend } from '@/api/dashboard'
import { User, ArrowDown, SwitchButton } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const authStore = useAuthStore()
const isCollapse = ref(false)
const currentTime = ref('')
const backendOnline = ref(false)

const currentRoute = computed(() => route.path)

const pageTitle = computed(() => {
  return (route.meta?.title as string) || 'NautiX'
})

const backendStatus = computed(() => {
  return backendOnline.value
    ? { type: 'success' as const, text: 'Backend Online' }
    : { type: 'danger' as const, text: 'Backend Offline' }
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
  currentTime.value = now.toLocaleString('en-AU', {
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

// 登出处理
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
}

.aside {
  background-color: #1f2937;
  transition: width 0.3s;
  overflow-x: hidden;
}

.logo {
  height: 60px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #374151;
}

.logo-text {
  color: #60a5fa;
  font-size: 24px;
  font-weight: bold;
}

.logo-sub {
  color: #d1d5db;
  font-size: 12px;
  font-weight: 500;
}

.header {
  background-color: #1f2937;
  border-bottom: 1px solid #374151;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 20px;
  color: #d1d5db;
  cursor: pointer;
  transition: color 0.3s;
}

.collapse-btn:hover {
  color: #60a5fa;
}

.page-title {
  color: #f9fafb;
  font-size: 18px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.clock {
  color: #d1d5db;
  font-size: 14px;
  font-family: monospace;
  font-weight: 500;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #d1d5db;
  cursor: pointer;
  font-size: 14px;
}

.user-info:hover {
  color: #60a5fa;
}

.main-content {
  background-color: #111827;
  padding: 20px;
}
</style>
