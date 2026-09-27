<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage, NButton, NCheckbox, NForm, NFormItem, NInput } from 'naive-ui'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const message = useMessage()
const auth = useAuthStore()
const loading = ref(false)
const captchaCode = ref('')
const form = reactive({
  username: 'host',
  password: '123456',
  captcha: '',
  remember: true,
})

function refreshCaptcha() {
  captchaCode.value = String(Math.floor(1000 + Math.random() * 9000))
}

async function submit() {
  if (form.captcha !== captchaCode.value) {
    message.warning('验证码错误')
    refreshCaptcha()
    return
  }
  loading.value = true
  try {
    await auth.login(form.username, form.password)
    message.success('登录成功')
    await router.replace('/')
  }
  catch (error) {
    message.error(error instanceof Error ? error.message : '登录失败')
    refreshCaptcha()
  }
  finally {
    loading.value = false
  }
}

onMounted(refreshCaptcha)
</script>

<template>
  <div class="login">
    <div class="login__hero">
      <div class="login__hero-mask" />
      <div class="login__hero-text">
        <h1>星栖 StarHaven</h1>
        <p>让每一次停留，都成为星光下的相遇</p>
      </div>
    </div>
    <div class="login__panel">
      <div class="login__card">
        <div class="login__brand">
          <div class="login__logo">星</div>
          <div>
            <div class="login__brand-title">星栖 StarHaven</div>
            <div class="login__brand-sub">民宿预订平台 · 管理后台系统</div>
          </div>
        </div>
        <n-form :model="form" size="large" @submit.prevent="submit">
          <n-form-item label="管理员账号">
            <n-input v-model:value="form.username" placeholder="用户名 / 手机号" />
          </n-form-item>
          <n-form-item label="密码">
            <n-input v-model:value="form.password" type="password" show-password-on="click" placeholder="请输入密码" />
          </n-form-item>
          <n-form-item label="验证码">
            <div class="login__captcha">
              <n-input v-model:value="form.captcha" placeholder="请输入验证码" maxlength="4" />
              <button type="button" class="login__captcha-code" @click="refreshCaptcha">{{ captchaCode }}</button>
            </div>
          </n-form-item>
          <div class="login__extra">
            <n-checkbox v-model:checked="form.remember">记住密码</n-checkbox>
            <a href="javascript:void(0)">忘记密码？</a>
          </div>
          <n-button type="primary" block attr-type="submit" :loading="loading" class="login__submit">
            登录
          </n-button>
        </n-form>
        <p class="login__demo">演示账号：host / admin · 密码 123456</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
}
.login__hero {
  position: relative;
  background:
    linear-gradient(135deg, rgba(15, 23, 42, 0.35), rgba(40, 120, 255, 0.25)),
    url('/banners/banner-starhaven.png') center / cover no-repeat;
}
.login__hero-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(15, 23, 42, 0.15), rgba(15, 23, 42, 0.55));
}
.login__hero-text {
  position: relative;
  z-index: 1;
  color: #fff;
  padding: 64px;
  max-width: 520px;
}
.login__hero-text h1 {
  margin: 0 0 12px;
  font-size: 40px;
}
.login__hero-text p {
  margin: 0;
  font-size: 18px;
  opacity: 0.92;
}
.login__panel {
  display: grid;
  place-items: center;
  background: #f6f8fb;
  padding: 32px;
}
.login__card {
  width: min(420px, 100%);
  background: #fff;
  border-radius: 16px;
  padding: 36px 32px 28px;
  box-shadow: 0 12px 40px rgba(15, 23, 42, 0.08);
}
.login__brand {
  display: flex;
  gap: 14px;
  align-items: center;
  margin-bottom: 28px;
}
.login__logo {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, #2878ff, #73d5c5);
  color: #fff;
  display: grid;
  place-items: center;
  font-size: 22px;
  font-weight: 700;
}
.login__brand-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
}
.login__brand-sub {
  margin-top: 4px;
  color: #666;
  font-size: 13px;
}
.login__captcha {
  display: grid;
  grid-template-columns: 1fr 96px;
  gap: 12px;
  width: 100%;
}
.login__captcha-code {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f8fafc;
  color: #2878ff;
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 4px;
  cursor: pointer;
}
.login__extra {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 13px;
}
.login__extra a {
  color: #2878ff;
  text-decoration: none;
}
.login__submit {
  height: 44px;
  font-size: 16px;
}
.login__demo {
  margin: 18px 0 0;
  text-align: center;
  color: #999;
  font-size: 12px;
}
@media (max-width: 960px) {
  .login {
    grid-template-columns: 1fr;
  }
  .login__hero {
    min-height: 220px;
  }
}
</style>
