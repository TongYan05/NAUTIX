import request from './axios'

export function getCommandData(){

    return request({

        url:'/command',

        method:'get'

    })

}