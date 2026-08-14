import { computed } from 'vue'
import { useSensorStore } from '@/stores/sensor'

export function useSensor(){

    const store=useSensorStore()

    const sensorCount=computed(()=>store.sensors.length)

    return{

        store,

        sensorCount

    }

}