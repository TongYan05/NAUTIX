import {defineStore} from 'pinia'

import {ref} from 'vue'

import {getAnalytics} from '@/api/analytics'

import type {AnalyticsData} from '@/types/analytics'


export const useAnalyticsStore=defineStore('analytics',()=>{


    const data=ref<AnalyticsData[]>([])


    async function load(){

        const res:any=await getAnalytics()

        data.value=res.data??[]

    }


    return{

        data,

        load

    }


})