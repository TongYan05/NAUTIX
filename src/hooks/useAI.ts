import {useAIStore} from '@/stores/ai'


export function useAI(){


    const store=useAIStore()


    return{


        store,


        send:store.send


    }


}