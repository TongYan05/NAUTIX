<template>

<div class="screen">

<CommandScreen/>

<KPIWall/>

<AlertTicker/>

</div>

</template>

<script setup lang="ts">

import CommandScreen from '@/components/command/CommandScreen.vue'

import KPIWall from '@/components/command/KPIWall.vue'

import AlertTicker from '@/components/command/AlertTicker.vue'

</script>

<style scoped>

.screen{

padding:20px;

display:grid;

gap:20px;

}

</style>