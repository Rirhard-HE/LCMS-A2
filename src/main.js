import Vue from 'vue'
import App from './App.vue'
import router from './router'
import 'element-ui/lib/theme-chalk/index.css'
import ElementUI from 'element-ui'
import Sidebar from '@/components/Sidebar.vue'
import './assets/global.css'
import locale from 'element-ui/lib/locale/lang/en' 

Vue.config.productionTip = false
Vue.use(ElementUI,{locale})
Vue.component('Sidebar', Sidebar)

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
