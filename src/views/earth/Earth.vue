<template>


<FullScreenLayout>


<div class="earth">


<CesiumViewer

@shipSelected="openShip"

/>



<div class="title">


Digital Earth


</div>



<div class="center-panel">


Ocean Digital Twin


</div>



<ShipDetailDrawer

:ship="selectedShip"

/>



</div>


</FullScreenLayout>


</template>



<script setup lang="ts">


import {ref} from 'vue'


import FullScreenLayout

from '@/layouts/FullScreenLayout.vue'


import CesiumViewer

from '@/components/map/CesiumViewer.vue'


import ShipDetailDrawer

from '@/components/ship/ShipDetailDrawer.vue'



const selectedShip = ref<any>()



function openShip(id:number){


selectedShip.value={


id,


name:"Nautix Explorer",


imo:"IMO-"+id,


speed:18.5,


fuel:75,


engineLoad:42


}


}



</script>



<style scoped>


.earth{


width:100%;


height:100%;


position:relative;


overflow:hidden;


background:


radial-gradient(

circle,

#16304a,

#06101c

);


}



/* 左上角标题 */

.title{


position:absolute;


top:30px;


left:40px;


z-index:10;


font-size:40px;


font-weight:bold;


color:white;


text-shadow:

0 0 15px rgba(0,212,255,.8);


}



/* 中间信息 */

.center-panel{


position:absolute;


top:50%;


left:50%;


transform:

translate(-50%,-50%);


z-index:5;


padding:40px;


border-radius:20px;


background:

rgba(255,255,255,0.08);


border:

1px solid rgba(255,255,255,0.15);


backdrop-filter:

blur(20px);


font-size:28px;


color:#00d4ff;


}



</style>