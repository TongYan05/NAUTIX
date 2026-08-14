<template>

<div class="glass">

<slot/>

</div>

</template>

<script setup lang="ts">
</script>

<style scoped>

.glass{

background:rgba(255,255,255,.08);

backdrop-filter:blur(12px);

border-radius:16px;

padding:20px;

color:white;

}

</style>