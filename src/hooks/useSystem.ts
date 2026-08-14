import { computed } from 'vue'

import { useSystemStore } from '@/stores/system'

export function useSystem(){

    const store=useSystemStore()

    const online=computed(()=>store.status?.simulator==="ONLINE")

    return{

        store,

        online

    }

}