import { defineStore } from 'pinia'

import { ref } from 'vue'

import { getAllAlertRecords } from '@/api/alert'

import type { Alert } from '@/types/alert'

export const useAlertStore=defineStore('alert',()=>{

    const alerts=ref<Alert[]>([])

    const currentAlert=ref<Alert>()

    async function loadAlerts(){

        const res:any=await getAllAlertRecords()

        alerts.value=res.data??[]

    }

    function selectAlert(alert:Alert){

        currentAlert.value=alert

    }

    return{

        alerts,

        currentAlert,

        loadAlerts,

        selectAlert

    }

})