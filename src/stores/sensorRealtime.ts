import {defineStore} from 'pinia'

import {ref} from 'vue'


export const useSensorRealtimeStore=
    defineStore('sensorRealtime',()=>{


        const values=ref<any[]>([])



        function push(data:any){


            values.value.push(data)


            if(values.value.length>1000){


                values.value.shift()


            }


        }



        return{


            values,

            push


        }


    })