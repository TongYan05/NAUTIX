<template>

<div class="replay-player">


<div class="title">

Replay Player

</div>



<div class="controls">


<button @click="play">

▶ Play

</button>


<button @click="pause">

Ⅱ Pause

</button>


</div>



</div>

</template>



<script setup lang="ts">


import {ref} from 'vue'


const playing = ref(false)



function play(){

    playing.value=true

}



function pause(){

    playing.value=false

}


</script>



<style scoped>


.replay-player{


padding:20px;


background:rgba(10,30,50,0.8);


border-radius:15px;


color:white;


}



.title{


font-size:20px;


margin-bottom:15px;


}



button{


margin-right:10px;


padding:8px 15px;


border-radius:8px;


border:none;


cursor:pointer;


}



</style>