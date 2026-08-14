<template>

<div class="cesium-container">

</div>

</template>


<script setup lang="ts">

import { onMounted } from 'vue'


const emit = defineEmits<{

  (
    e:'ready',
    viewer:any
  ):void

}>()


onMounted(()=>{


  const viewer = {

    name:'Cesium Viewer',

    status:'initialized'

  }


  emit(
    'ready',
    viewer
  )


})


</script>



<style scoped>

.cesium-container{

width:100%;

height:100%;

background:#06101c;

}


</style>