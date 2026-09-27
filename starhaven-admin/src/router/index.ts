import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: () => import('@/views/LoginView.vue'), meta: { public: true } },
    {
      path: '/',
      component: () => import('@/views/ShellView.vue'),
      children: [
        { path: '', name: 'dashboard', component: () => import('@/views/DashboardView.vue'), meta: { title: '首页' } },
        { path: 'users', name: 'users', component: () => import('@/views/UsersView.vue'), meta: { title: '用户管理', admin: true } },
        { path: 'houses', name: 'houses', component: () => import('@/views/HousesView.vue'), meta: { title: '房源管理' } },
        { path: 'hosts', name: 'hosts', component: () => import('@/views/HostsView.vue'), meta: { title: '房东管理', admin: true } },
        { path: 'orders', name: 'orders', component: () => import('@/views/OrdersView.vue'), meta: { title: '订单管理' } },
        { path: 'orders/:id', name: 'order-detail', component: () => import('@/views/OrderDetailView.vue'), meta: { title: '订单详情' } },
        { path: 'comments', name: 'comments', component: () => import('@/views/CommentsView.vue'), meta: { title: '评论管理', admin: true } },
        { path: 'messages', name: 'messages', component: () => import('@/views/MessagesView.vue'), meta: { title: '消息管理', admin: true } },
        { path: 'finance', name: 'finance', component: () => import('@/views/FinanceView.vue'), meta: { title: '财务管理', admin: true } },
        { path: 'coupons', name: 'coupons', component: () => import('@/views/CouponsView.vue'), meta: { title: '优惠券管理', admin: true } },
        { path: 'settings', name: 'settings', component: () => import('@/views/SettingsView.vue'), meta: { title: '系统设置', admin: true } },
      ],
    },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.public)
    return true
  if (!auth.token)
    return '/login'
  if (to.meta.admin && !auth.isAdmin)
    return '/'
  return true
})

export default router
