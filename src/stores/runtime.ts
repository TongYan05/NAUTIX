import {defineStore} from 'pinia'

import {ref} from 'vue'


export const useRuntimeStore=

    defineStore('runtime',()=>{


        const runtime=ref<any>()



        function update(data:any){

            runtime.value=data

        }



        return{

            runtime,

            update

        }


    })