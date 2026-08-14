import request from './axios'


export function askAI(message:string){


    return request({


        url:'/ai/chat',


        method:'post',


        data:{message}


    })


}