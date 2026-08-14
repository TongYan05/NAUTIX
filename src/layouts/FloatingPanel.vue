<template>

<div class="floating">

<slot/>

</div>

</template>

<script setup lang="ts">
</script>

<style scoped>

.floating{

position:absolute;

right:20px;

top:20px;

padding:16px;

background:rgba(20,40,60,.85);

backdrop-filter:blur(10px);

border-radius:12px;

color:white;

}

</style>