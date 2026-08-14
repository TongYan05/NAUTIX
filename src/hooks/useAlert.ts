import { computed } from 'vue'

import { useAlertStore } from '@/stores/alert'

export function useAlert(){

    const store=useAlertStore()

    const alertCount=computed(()=>store.alerts.length)

    return{

        store,

        alertCount

    }

}