import request from './axios'


export function getRuntime(shipId:number){


    return request({


        url:`/runtime/${shipId}`,


        method:'get'


    })


}


export function getShipRuntime(id:number){

    return request({

        url:`/runtime/${id}`,

        method:'GET'

    })


}