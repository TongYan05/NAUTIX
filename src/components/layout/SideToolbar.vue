<template>

<div class="toolbar">

<slot/>

</div>

</template>

<script setup lang="ts">
</script>

<style scoped>

.toolbar{

position:fixed;

left:20px;

top:120px;

display:flex;

flex-direction:column;

gap:12px;

}

</style>