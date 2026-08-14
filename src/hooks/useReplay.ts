import { computed } from 'vue'

import { useReplayStore } from '@/stores/replay'

export function useReplay(){

    const store=useReplayStore()

    const totalFrames=computed(()=>store.frames.length)

    return{

        store,

        totalFrames

    }

}