import { computed } from 'vue'

import { useFleetStore } from '@/stores/fleet'

export function useFleet(){

    const store=useFleetStore()

    const fleetCount=computed(()=>store.fleets.length)

    return{

        store,

        fleetCount

    }

}