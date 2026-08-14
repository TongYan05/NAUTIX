<template>

<div
ref="chart"
class="chart"
>

</div>

</template>


<script setup lang="ts">

import {onMounted,ref} from 'vue'

import * as echarts from 'echarts'


const chart=ref()


onMounted(()=>{


const instance =
echarts.init(chart.value)


instance.setOption({

xAxis:{

type:'category',

data:[

'10:00',

'10:01',

'10:02'

]

},


yAxis:{

type:'value'

},


series:[{

type:'line',

data:[20,30,25]

}]


})


})


</script>



<style scoped>


.chart{

width:100%;

height:300px;


}


</style>