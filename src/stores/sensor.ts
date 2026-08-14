import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getSensorDataList } from '@/api/sensor'
import type { Sensor } from '@/types/sensor'

export const useSensorStore=defineStore('sensor',()=>{

    const sensors=ref<Sensor[]>([])

    const currentSensor=ref<Sensor>()

    async function loadSensors(){

        const res:any=await getSensorDataList()

        sensors.value=res.data??[]

    }

    function selectSensor(sensor:Sensor){

        currentSensor.value=sensor

    }

    return{

        sensors,

        currentSensor,

        loadSensors,

        selectSensor

    }

})