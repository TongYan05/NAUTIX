<template>

<div class="page">

<ServerStatus/>

<DatabaseMonitor/>

<CacheMonitor/>

<ApiLatencyChart/>

<SimulatorMonitor/>

</div>

</template>

<script setup lang="ts">

import ServerStatus from '@/components/system/ServerStatus.vue'
import DatabaseMonitor from '@/components/system/DatabaseMonitor.vue'
import CacheMonitor from '@/components/system/CacheMonitor.vue'
import ApiLatencyChart from '@/components/system/ApiLatencyChart.vue'
import SimulatorMonitor from '@/components/system/SimulatorMonitor.vue'

</script>

<style scoped>

.page{

display:grid;

grid-template-columns:repeat(2,1fr);

gap:20px;

padding:30px;

}

</style>