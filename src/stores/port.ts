import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getAllPorts } from '@/api/port'
import type { Port } from '@/types/port'

export const usePortStore = defineStore('port',()=>{

    const ports = ref<Port[]>([])

    const currentPort = ref<Port>()

    async function loadPorts(){

        const res:any = await getAllPorts()

        ports.value = res.data ?? []

    }

    function selectPort(port:Port){

        currentPort.value = port

    }

    return{

        ports,

        currentPort,

        loadPorts,

        selectPort

    }

})