import request from './axios'

/**
 * 向智能客服发送一次提问。
 *
 * 后端返回结构为 { data, intent, suggestions }，
 * 其中 data 为回复正文，suggestions 为推荐的后续追问。
 * lang 指定回复语言（zh / en），与前端当前语言一致。
 */
export function askAI(message: string, lang: string) {
  return request({
    url: '/ai/chat',
    method: 'post',
    data: { message, lang }
  })
}

/**
 * 获取客服面板的常见问题列表，用于首次打开时展示快捷入口。
 */
export function getAiSuggestions(lang: string) {
  return request({
    url: '/ai/suggestions',
    method: 'get',
    params: { lang }
  })
}
