import {defineStore} from 'pinia'

import {ref, watch} from 'vue'

import {askAI, getAiSuggestions} from '@/api/ai'

import {useLang} from '@/stores/lang'

import type {AIMessage} from '@/types/ai'


export const useAIStore=defineStore('ai',()=>{


    const messages=ref<AIMessage[]>([])

    const suggestions=ref<string[]>([])

    const loading=ref(false)

    const error=ref('')

    const langStore=useLang()


    /**
     * 加载常见问题，供客服面板首次打开时展示快捷入口。
     * 后端不可用时静默降级为内置默认问题，不阻断对话功能。
     */
    async function loadSuggestions(force=false){

        if(suggestions.value.length>0&&!force){
            return
        }

        try{

            const res:any=await getAiSuggestions(langStore.lang)

            if(Array.isArray(res)&&res.length>0){
                suggestions.value=res
                return
            }

        }catch{
            // 忽略：使用下方默认问题
        }

        suggestions.value=[
            langStore.t('ai.defQ1'),
            langStore.t('ai.defQ2'),
            langStore.t('ai.defQ3'),
            langStore.t('ai.defQ4')
        ]

    }

    // 语言切换后，下一次打开面板会按新语言重新拉取建议
    watch(()=>langStore.lang,()=>{
        suggestions.value=[]
    })


    async function send(message:string){

        const text=message.trim()

        if(!text||loading.value){
            return
        }

        error.value=''

        messages.value.push({

            role:'user',

            content:text,

            time:new Date().toISOString()

        })

        loading.value=true

        try{

            // axios 拦截器已返回响应体本身，即 { data, intent, suggestions }
            const res:any=await askAI(text,langStore.lang)

            messages.value.push({

                role:'assistant',

                content:res?.data??langStore.t('ai.noReply'),

                time:new Date().toISOString(),

                intent:res?.intent

            })

            if(Array.isArray(res?.suggestions)&&res.suggestions.length>0){
                suggestions.value=res.suggestions
            }

        }catch(e:any){

            const status=e?.response?.status

            const hint=status===401||status===403
                ?langStore.t('ai.errLogin')
                :status===404
                    ?langStore.t('ai.errMissing')
                    :langStore.t('ai.errOffline')

            error.value=hint

            messages.value.push({

                role:'assistant',

                content:hint,

                time:new Date().toISOString(),

                intent:'error'

            })

        }finally{

            loading.value=false

        }

    }


    function clear(){

        messages.value=[]

        error.value=''

    }


    return{


        messages,

        suggestions,

        loading,

        error,

        send,

        loadSuggestions,

        clear


    }

})
