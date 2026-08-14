<template>

<div>

<SensorDashboard/>

<EngineChart/>

<FuelChart/>

<RPMChart/>


</div>


</template>


<script setup lang="ts">

import SensorDashboard from '@/components/sensor/SensorDashboard.vue'

import EngineChart from '@/components/sensor/EngineChart.vue'

import FuelChart from '@/components/sensor/FuelChart.vue'

import RPMChart from '@/components/sensor/RPMChart.vue'


</script>