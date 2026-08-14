<template>

<div class="card">

<h3>{{ship.shipName}}</h3>

<p>MMSI：{{ship.mmsi}}</p>

<p>Speed：{{ship.speed}} kn</p>

<p>Heading：{{ship.heading}}°</p>

</div>

</template>

<script setup lang="ts">

import type { Ship } from '@/types/ship'

defineProps<{

ship:Ship

}>()

</script>

<style scoped>

.card{

padding:20px;

background:#14263a;

border-radius:12px;

color:white;

}

</style>