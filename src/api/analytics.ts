import request from './axios'


export function getAnalytics(){

    return request({

        url:'/analytics',

        method:'get'

    })

}