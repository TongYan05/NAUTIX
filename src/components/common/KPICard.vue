<template>

<div class="kpi-card">


<div class="title">

{{ title }}

</div>


<div class="value">

{{ value }}

</div>


<div class="description">

{{ description }}

</div>


</div>

</template>



<script setup lang="ts">


defineProps({

    title:{
        type:String,
        required:true
    },


    value:{
        type:[String,Number],
        required:true
    },


    description:{
        type:String,
        default:''
    }

})


</script>



<style scoped>


.kpi-card{


background:

rgba(255,255,255,0.08);


border:

1px solid rgba(255,255,255,0.15);


backdrop-filter:

blur(12px);



border-radius:16px;


padding:25px;


min-width:220px;


color:white;



}



.title{

color:#9bb8d8;

font-size:14px;

}



.value{

font-size:36px;

font-weight:700;


color:#00d4ff;


margin:15px 0;


}



.description{

font-size:13px;

color:#c8d6e5;

}


</style>