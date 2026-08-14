import { defineStore } from 'pinia'

import { ref } from 'vue'

import { getFleetList } from '@/api/fleet'

import type { Fleet } from '@/types/fleet'

export const useFleetStore=defineStore('fleet',()=>{

    const fleets=ref<Fleet[]>([])

    async function loadFleets(){

        const res:any=await getFleetList()

        fleets.value=res.data??[]

    }

    return{

        fleets,

        loadFleets

    }

})