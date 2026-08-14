import {defineStore} from 'pinia'

import {ref} from 'vue'


export const useSensorDataStore =
    defineStore('sensorData',()=>{


        const data=ref<any[]>([])



        function push(value:any){

            data.value.push(value)


            if(data.value.length>500){

                data.value.shift()

            }


        }


        return{

            data,

            push

        }


    })