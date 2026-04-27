import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/home'
    },
    {
      path: '/login',
      component: () => import('../views/auth/LoginView.vue')
    },
    {
      path: '/register',
      component: () => import('../views/auth/RegisterView.vue')
    },
    {
      path: '/home',
      component: () => import('../views/item/HomeView.vue')
    },
    {
      path: '/item/:id',
      component: () => import('../views/item/ItemDetailView.vue')
    },
    {
      path: '/publish',
      component: () => import('../views/item/PublishView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/my-claims',
      component: () => import('../views/item/MyClaimsView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/stats',
      component: () => import('../views/stats/StatsView.vue')
    },
    {
      path: '/llm-query',
      component: () => import('../views/llm/LlmQueryView.vue')
    },
    {
      path: '/admin',
      component: () => import('../views/admin/AdminView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    }
  ]
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.userInfo) {
    next('/login')
  } else if (to.meta.requiresAdmin && !userStore.isAdmin()) {
    next('/home')
  } else {
    next()
  }
})

export default router
