import {useMapStore} from '@/stores/map'


export function useMap(){


    const store=useMapStore()


    return{


        center:store.center,

        zoom:store.zoom,

        setCenter:store.setCenter


    }


}