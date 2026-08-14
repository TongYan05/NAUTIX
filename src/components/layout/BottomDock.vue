<template>

<div class="dock">


<button>
🌐 Earth
</button>


<button>
🚢 Ships
</button>


<button>
🛰 Sensors
</button>


<button>
⚠ Alerts
</button>


<button>
📊 Analytics
</button>


</div>

</template>


<style scoped>


.dock{


position:fixed;


bottom:20px;


left:50%;


transform:translateX(-50%);



display:flex;



gap:20px;



padding:15px 25px;



background:

rgba(10,25,45,0.75);



backdrop-filter:

blur(20px);



border:

1px solid rgba(255,255,255,0.15);



border-radius:30px;



}



button{


background:

transparent;



border:none;



color:white;



font-size:15px;



cursor:pointer;



padding:10px 15px;



}



button:hover{


color:#00d4ff;


}


</style>