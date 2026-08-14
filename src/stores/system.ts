import { defineStore } from 'pinia'

import { ref } from 'vue'

import { getAllLogs } from '@/api/system'

import type { SystemStatus } from '@/types/system'

export const useSystemStore=defineStore('system',()=>{

    const status=ref<SystemStatus>()

    async function load(){

        const res:any=await getAllLogs()

        status.value=res.data

    }

    return{

        status,

        load

    }

})