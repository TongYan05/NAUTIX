import { defineStore } from 'pinia'
import { getAllShips } from '@/api/ship'
import type { ShipInfo } from '@/api/types'

export const useShipStore = defineStore('ship', {
  state: () => ({
    ships: [] as ShipInfo[],
    loading: false,
    loaded: false
  }),
  
  getters: {
    shipMap: (state) => {
      const map = new Map<number, ShipInfo>()
      state.ships.forEach(ship => {
        if (ship.id != null) map.set(ship.id, ship)
      })
      return map
    }
  },
  
  actions: {
    async loadShips() {
      // 已加载过则直接返回
      if (this.loaded) return
      
      this.loading = true
      try {
        this.ships = await getAllShips() || []
        this.loaded = true
      } catch (error) {
        console.error('Failed to load ships:', error)
        this.ships = []
      } finally {
        this.loading = false
      }
    }
  }
})
