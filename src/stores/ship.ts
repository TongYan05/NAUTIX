import { defineStore } from 'pinia'

import { ref } from 'vue'

import { getAllShips } from '@/api/ship'

import type { Ship } from '@/types/ship'

export const useShipStore = defineStore('ship',()=>{

    const ships = ref<Ship[]>([])

    const currentShip = ref<Ship>()

    async function loadShips(){

        const res:any = await getAllShips()

        ships.value = res.data ?? []

    }

    function selectShip(ship:Ship){

        currentShip.value = ship

    }

    return{

        ships,

        currentShip,

        loadShips,

        selectShip

    }

})