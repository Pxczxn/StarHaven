<script lang="ts" setup>
import { useTokenStore } from '@/store/token'
import { register } from '@/api/login'

definePage({
  style: {
    navigationBarTitleText: '登录',
  },
})

const tokenStore = useTokenStore()
const mode = ref<'login' | 'register'>('login')
const redirect = ref('')
const tabPages = ['/pages/index/index', '/pages/favorites/favorites', '/pages/orders/orders', '/pages/me/me']
const form = reactive({
  username: 'staruser',
  password: '123456',
  phone: '13800000001',
})
const submitting = ref(false)

onLoad((options) => {
  if (options?.redirect)
    redirect.value = decodeURIComponent(String(options.redirect))
})

function afterLogin() {
  const target = redirect.value
  if (!target) {
    uni.switchTab({ url: '/pages/index/index' })
    return
  }
  const path = target.split('?')[0]
  if (tabPages.includes(path)) {
    uni.switchTab({ url: path })
    return
  }
  uni.redirectTo({ url: target })
}

async function submit() {
  submitting.value = true
  try {
    if (mode.value === 'register') {
      await register({
        username: form.username,
        password: form.password,
        phone: form.phone,
        nickname: form.username,
      })
      uni.showToast({ title: '注册成功', icon: 'success' })
      mode.value = 'login'
      return
    }
    await tokenStore.login({ username: form.username, password: form.password })
    afterLogin()
  }
  finally {
    submitting.value = false
  }
}
</script>

<template>
  <view class="min-h-screen bg-page px-6 pt-16">
    <view class="text-3xl text-ink font-semibold">
      星栖 StarHaven
    </view>
    <view class="mt-2 text-sm text-muted">
      让每一次停留，都成为星光下的相遇
    </view>
    <view class="mt-10 rounded-3 bg-white p-5 shadow-sm">
      <input v-model="form.username" class="mb-4 border-b border-#EEE py-3" placeholder="用户名 / 手机号">
      <input v-model="form.password" password class="mb-4 border-b border-#EEE py-3" placeholder="密码">
      <input v-if="mode === 'register'" v-model="form.phone" class="mb-4 border-b border-#EEE py-3" placeholder="手机号">
      <view class="center mt-6 h-11 rounded-full bg-primary text-white" @click="submit">
        {{ submitting ? '请稍候...' : (mode === 'login' ? '登录' : '注册') }}
      </view>
      <view class="mt-4 text-center text-sm text-primary" @click="mode = mode === 'login' ? 'register' : 'login'">
        {{ mode === 'login' ? '没有账号？去注册' : '已有账号？去登录' }}
      </view>
      <view class="mt-3 text-center text-xs text-faint">
        演示账号 staruser / 123456
      </view>
    </view>
  </view>
</template>
