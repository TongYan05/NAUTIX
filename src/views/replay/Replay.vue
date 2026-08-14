<template>

<div class="page">

<ReplayPlayer/>

<PlaybackControl/>

<TimelineSlider/>

</div>

</template>

<script setup lang="ts">

import ReplayPlayer from '@/components/replay/ReplayPlayer.vue'

import PlaybackControl from '@/components/replay/PlaybackControl.vue'

import TimelineSlider from '@/components/replay/TimelineSlider.vue'

</script>

<style scoped>

.page{

padding:30px;

display:flex;

flex-direction:column;

gap:20px;

}

</style>