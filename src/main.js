import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './styles/element-plus.less'
//将element-plus默认英文改为中文
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

//引入router
import router from './router'

//引入pinia状态管理
import pinia from './store'


const app = createApp(App)
app.use(ElementPlus,{
    locale:zhCn,
})

//使用router路由
app.use(router)
app.use(pinia)

app.mount('#app')