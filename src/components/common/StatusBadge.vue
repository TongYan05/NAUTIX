<template>

<span class="badge">

{{ status }}

</span>


</template>



<script setup lang="ts">


defineProps({

    status:{
        type:String,
        required:true
    }

})


</script>



<style scoped>


.badge{


padding:

5px 12px;


border-radius:

20px;


background:

rgba(0,212,255,0.15);



color:

#00d4ff;



font-size:

13px;


}


</style>