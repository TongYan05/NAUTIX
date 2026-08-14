<template>

<div class="page">

<h1>Fleet Mission Center</h1>

<FleetCard

v-for="item in fleetStore.fleets"

:key="item.id"

/>

</div>

</template>

<script setup lang="ts">

import { onMounted } from 'vue'

import FleetCard from '@/components/fleet/FleetCard.vue'

import { useFleetStore } from '@/stores/fleet'

const fleetStore=useFleetStore()

onMounted(()=>{

fleetStore.loadFleets()

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