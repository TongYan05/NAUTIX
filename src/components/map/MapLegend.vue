<template>
  <div class="legend">
    <div>🟢 Ship</div>
    <div>🔵 Route</div>
    <div>🟠 Port</div>
    <div>🟣 Weather</div>
    <div>🔴 Alert</div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.legend{
  position:absolute;
  left:20px;
  bottom:20px;
  background:rgba(10,20,30,.8);
  color:white;
  padding:16px;
  border-radius:12px;
  z-index:1000;
}
</style>