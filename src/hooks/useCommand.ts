import { computed } from 'vue'

import { useCommandStore } from '@/stores/command'

export function useCommand(){

    const store=useCommandStore()

    const total=computed(()=>store.kpis.length)

    return{

        store,

        total

    }

}