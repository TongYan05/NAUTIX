import {defineStore} from 'pinia'

import {ref} from 'vue'


export const useMapStore =
    defineStore('map',()=>{


        const zoom=ref(3)


        const center=ref({

            longitude:0,

            latitude:0

        })


        function setCenter(lon:number,lat:number){


            center.value={

                longitude:lon,

                latitude:lat

            }


        }


        return{

            zoom,

            center,

            setCenter


        }


    })