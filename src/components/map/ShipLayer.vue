<template>


<div class="ship-layer">


<div

v-for="ship in ships"

:key="ship.id"

class="ship-marker"

@click="selectShip(ship.id)"

>


🚢 {{ship.name}}


</div>


</div>


</template>



<script setup lang="ts">


interface Ship{


id:number

name:string


}



const ships:Ship[]=[


{
id:1,
name:'Nautix-001'
},


{
id:2,
name:'Nautix-002'
}


]



const emit = defineEmits<{

(
e:'select',
id:number
):void


}>()



function selectShip(id:number){


emit(
'select',
id
)


}



</script>



<style scoped>


.ship-layer{


position:absolute;

top:0;

left:0;


}



.ship-marker{


padding:8px;

margin:5px;

background:#0d1b2a;

color:white;

border-radius:8px;

cursor:pointer;


}


</style>