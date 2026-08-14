<template>
  <MainLayout v-if="!isAuthPage" />
  <router-view v-else />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'

const route = useRoute()
const isAuthPage = computed(() => {
  return route.path === '/login' || route.path === '/register'
})
</script>
