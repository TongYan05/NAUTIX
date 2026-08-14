<template>

<div class="page">

<h1>Alert Center</h1>

<AlertCard

v-for="item in alertStore.alerts"

:key="item.id"

/>

</div>

</template>

<script setup lang="ts">

import { onMounted } from 'vue'

import AlertCard from '@/components/alert/AlertCard.vue'

import { useAlertStore } from '@/stores/alert'

const alertStore=useAlertStore()

onMounted(()=>{

alertStore.loadAlerts()

})

</script>

<style scoped>

.page{

padding:30px;

display:grid;

grid-template-columns:repeat(auto-fill,minmax(320px,1fr));

gap:20px;

}

</style>