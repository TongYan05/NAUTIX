import { defineStore } from 'pinia'

import { ref } from 'vue'

import { getReplay } from '@/api/replay'

import type { ReplayFrame } from '@/types/replay'

export const useReplayStore=defineStore('replay',()=>{

    const frames=ref<ReplayFrame[]>([])

    const currentIndex=ref(0)

    async function loadReplay(shipId:number){

        const res:any=await getReplay(shipId)

        frames.value=res.data??[]

    }

    function next(){

        if(currentIndex.value<frames.value.length-1){

            currentIndex.value++

        }

    }

    function previous(){

        if(currentIndex.value>0){

            currentIndex.value--

        }

    }

    return{

        frames,

        currentIndex,

        loadReplay,

        next,

        previous

    }

})