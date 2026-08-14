import {onMounted,onUnmounted} from 'vue'


let socket:WebSocket|null=null


export function useWebSocket(
    url:string,
    callback:(data:any)=>void
){


    function connect(){


        socket=new WebSocket(url)



        socket.onmessage=(event)=>{


            const message=JSON.parse(event.data)


            callback(message)


        }


    }



    function close(){


        socket?.close()


    }



    onMounted(()=>{


        connect()


    })



    onUnmounted(()=>{


        close()


    })


    return{


        connect,

        close


    }


}