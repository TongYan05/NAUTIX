<template>

<div class="status">


<h3>

Runtime Status

</h3>


<p>
Speed:
{{runtime?.speed}}

</p>


<p>
Fuel:
{{runtime?.fuel}}

</p>


<p>
Engine:
{{runtime?.engineLoad}}

</p>


</div>


</template>



<script setup lang="ts">


import {computed} from 'vue'

import {useRuntimeStore} from '@/stores/runtime'


const store=useRuntimeStore()



const runtime=computed(()=>


store.runtime[0]


)


</script>


<style scoped>


.status{


padding:20px;

background:#10273d;

color:white;

border-radius:12px;


}



</style>