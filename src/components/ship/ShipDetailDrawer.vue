<template>

<div
v-if="ship"
class="drawer"
>


<h2>

{{ship.name}}

</h2>


<p>
IMO:
{{ship.imo}}
</p>


<p>
Speed:
{{ship.speed}} knots
</p>


<p>
Fuel:
{{ship.fuel}} %
</p>


<p>
Engine Load:
{{ship.engineLoad}} %
</p>


</div>

</template>



<script setup lang="ts">


defineProps<{

ship?:any


}>()


</script>



<style scoped>


.drawer{


position:absolute;


right:20px;


top:80px;


width:300px;


padding:20px;


background:

rgba(10,30,50,.85);



color:white;


border-radius:15px;


backdrop-filter:blur(15px);


}



</style>