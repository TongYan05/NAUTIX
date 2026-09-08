import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { messages, type MsgKey } from '@/i18n'

export type Lang = 'zh' | 'en'

const STORAGE_KEY = 'nautix_lang'

/**
 * 全站语言状态：zh / en。
 *
 * 持久化到 localStorage，刷新后保持；所有页面与 AI 客服浮标共用此状态。
 */
export const useLang = defineStore('lang', () => {
  const saved = localStorage.getItem(STORAGE_KEY)
  const lang = ref<Lang>(saved === 'zh' || saved === 'en' ? saved : 'en')

  const dict = computed(() => messages[lang.value])

  function t(key: MsgKey, params?: Record<string, string | number>): string {
    let text: string = dict.value[key] ?? key
    if (params) {
      for (const [k, v] of Object.entries(params)) {
        text = text.replace(new RegExp(`\\{${k}\\}`, 'g'), String(v))
      }
    }
    return text
  }

  function setLang(next: Lang) {
    lang.value = next
    localStorage.setItem(STORAGE_KEY, next)
    document.documentElement.lang = next === 'zh' ? 'zh-CN' : 'en'
  }

  function toggle() {
    setLang(lang.value === 'zh' ? 'en' : 'zh')
  }

  return { lang, t, setLang, toggle }
})
