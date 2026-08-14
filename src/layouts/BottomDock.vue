<template>

<div class="dock">

<slot/>

</div>

</template>

<script setup lang="ts">
</script>

<style scoped>

.dock{

position:fixed;

bottom:20px;

left:50%;

transform:translateX(-50%);

padding:14px 24px;

background:#17324d;

border-radius:20px;

color:white;

}

</style>