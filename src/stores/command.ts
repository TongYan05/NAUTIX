import { defineStore } from 'pinia'

import { ref } from 'vue'

import { getCommandData } from '@/api/command'

import type { KPI } from '@/types/command'

export const useCommandStore=defineStore('command',()=>{

    const kpis=ref<KPI[]>([])

    async function load(){

        const res:any=await getCommandData()

        kpis.value=res.data??[]

    }

    return{

        kpis,

        load

    }

})