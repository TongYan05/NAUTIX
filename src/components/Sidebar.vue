<template>

<aside class="sidebar">


<h2>
Nautix
</h2>


<nav>


<RouterLink to="/dashboard">
Dashboard
</RouterLink>


<RouterLink to="/earth">
Digital Earth
</RouterLink>


<RouterLink to="/ship">
Ship Explorer
</RouterLink>


<RouterLink to="/sensor">
Sensor Intelligence
</RouterLink>


<RouterLink to="/route">
Route Explorer
</RouterLink>


<RouterLink to="/weather">
Weather Center
</RouterLink>


<RouterLink to="/alert">
Alert Center
</RouterLink>


<RouterLink to="/simulator">
Simulator
</RouterLink>


</nav>


</aside>


</template>



<style scoped>


.sidebar{


width:240px;


height:100vh;


padding:20px;


background:#0d1b2a;


color:white;


}



h2{

color:white;

}



a{

color:#9ecfff;

}





.router-link-active{


color:white;


font-weight:bold;


}



</style>