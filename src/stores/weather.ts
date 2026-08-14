import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getAllWeatherRegions } from '@/api/weather'
import type { Weather } from '@/types/weather'

export const useWeatherStore = defineStore('weather',()=>{

    const weatherList=ref<Weather[]>([])

    async function loadWeather(){

        const res:any=await getAllWeatherRegions()

        weatherList.value=res.data??[]

    }

    return{

        weatherList,

        loadWeather

    }

})