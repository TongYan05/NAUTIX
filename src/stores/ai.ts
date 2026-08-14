import {defineStore} from 'pinia'

import {ref} from 'vue'

import {askAI} from '@/api/ai'

import type {AIMessage} from '@/types/ai'


export const useAIStore=defineStore('ai',()=>{


    const messages=ref<AIMessage[]>([])



    async function send(message:string){


        messages.value.push({

            role:'user',

            content:message,

            time:new Date().toISOString()

        })


        const res:any=await askAI(message)


        messages.value.push({

            role:'assistant',

            content:res.data,

            time:new Date().toISOString()

        })


    }



    return{


        messages,

        send


    }


})