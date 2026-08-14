<template>


<div class="fullscreen">


<CommandBar />


<div class="content">

<slot />


</div>


<BottomDock />


</div>


</template>



<script setup lang="ts">


import CommandBar
from '@/components/layout/CommandBar.vue'


import BottomDock
from '@/components/layout/BottomDock.vue'


</script>



<style scoped>


.fullscreen{


width:100%;


height:100vh;


background:#06101c;



overflow:hidden;


}



.content{


height:calc(100vh - 60px);


position:relative;


}



</style>