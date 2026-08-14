import './style.css'

import { createApp } from 'vue'

import { createPinia } from 'pinia'

import ElementPlus from 'element-plus'

import 'element-plus/dist/index.css'

import en from 'element-plus/es/locale/lang/en'

import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'

import router from './router'


const app = createApp(App)


for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}


app.use(createPinia())

app.use(ElementPlus, { locale: en })

app.use(router)


app.mount('#app')
