<template>

<div class="panel">

<h2>Ship Detail</h2>

<p>Name : {{ship?.shipName}}</p>

<p>MMSI : {{ship?.mmsi}}</p>

<p>IMO : {{ship?.imo}}</p>

<p>Latitude : {{ship?.latitude}}</p>

<p>Longitude : {{ship?.longitude}}</p>

</div>

</template>

<script setup lang="ts">

import { computed } from 'vue'

import { useShipStore } from '@/stores/ship'

const store = useShipStore()

const ship = computed(()=>store.currentShip)

</script>

<style scoped>

.panel{

padding:20px;

background:#0f2235;

color:white;

border-radius:12px;

}

</style>