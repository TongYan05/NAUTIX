import request from './axios'

export function getReplay(shipId:number){

    return request({

        url:`/replay/${shipId}`,

        method:'get'

    })

}