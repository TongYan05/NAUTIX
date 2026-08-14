import request from './axios'

export function getFleetList(){

    return request({

        url:'/fleet',

        method:'get'

    })

}