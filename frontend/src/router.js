import Vue from 'vue'
import Router from 'vue-router'

import MainLayout from '@/components/MainLayout.vue'
import AuthLayout from '@/components/AuthLayout.vue'

import Home from '@/views/Home.vue'
import CasesList from '@/views/CasesList.vue'
import HearingListCards from '@/views/Hearings.vue'
import Categories from '@/views/Categories.vue'
import EvidenceList from '@/views/EvidenceList.vue'
import CaseDetail from '@/views/CaseDetail.vue'
import Dashboard from './views/Dashboard.vue'

import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'

import { Message } from 'element-ui'

Vue.use(Router)

const router = new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      component: AuthLayout,
      children: [
        { path: '', name: 'home', component: Home,meta:{public:true} },
        { path: 'login', name: 'login', component: Login, meta: { public: true } },
        { path: 'register', name: 'register', component: Register, meta: { public: true } },
      ]
    },

    {
      path: '/',
      component: MainLayout,
      children: [
        { path: 'cases', name: 'cases', component: CasesList },
        { path: 'cases/:caseNo', name: 'caseDetail', component: CaseDetail, props: true },
        { path: 'hearings', name: 'hearings', component: HearingListCards },
        { path: 'categories', name: 'categories', component: Categories },
        { path: 'evidence', name: 'evidence', component: EvidenceList },
        { path: 'dashboard', name:'dashboard', component:Dashboard}
      ]
    },

    { path: '*', redirect: '/' }
  ]
})

router.beforeEach((to, from, next) => {
  const isLoggedIn = !!localStorage.getItem('token')
  if (to.matched.some(r => r.meta.public)) return next()
  if (!isLoggedIn && to.path !== '/login'){
    Message.warning('You must login')
    return next('/login')
  }
  next()
})

export default router
