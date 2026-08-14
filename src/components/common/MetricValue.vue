<template>


<div class="metric">


<div class="label">

{{label}}

</div>


<div class="number">

{{value}}

</div>


</div>


</template>



<script setup lang="ts">


defineProps({

label:String,

value:[String,Number]


})


</script>



<style scoped>


.metric{

color:white;

}



.label{

color:#8fa9c7;

font-size:14px;

}



.number{

font-size:28px;

font-weight:bold;

color:#00d4ff;

}



</style>