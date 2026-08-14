<template>

<div class="drawer">

<slot/>

</div>

</template>

<script setup lang="ts">
</script>

<style scoped>

.drawer{

position:fixed;

right:0;

top:0;

width:420px;

height:100%;

background:#10273d;

color:white;

padding:24px;

overflow:auto;

}

</style>