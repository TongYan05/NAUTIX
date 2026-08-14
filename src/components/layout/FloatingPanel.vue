<template>

<div class="floating">


<slot />


</div>


</template>



<style scoped>


.floating{


position:absolute;


background:

rgba(10,25,45,0.75);



backdrop-filter:

blur(20px);



border:

1px solid rgba(255,255,255,0.15);



border-radius:20px;



padding:20px;



color:white;



}



</style>