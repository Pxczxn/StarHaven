<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  NAvatar,
  NBreadcrumb,
  NBreadcrumbItem,
  NDropdown,
  NIcon,
  NLayout,
  NLayoutContent,
  NLayoutHeader,
  NLayoutSider,
  NMenu,
  NSpace,
} from 'naive-ui'
import { NotificationsOutline } from '@vicons/ionicons5'
import { buildMenuOptions } from '@/constants/menu'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const collapsed = ref(false)

const menuOptions = computed(() => buildMenuOptions(!!auth.isAdmin))
const activeKey = computed(() => route.path || '/')
const breadcrumbs = computed(() => {
  const chain = [{ label: '首页', path: '/' }]
  if (route.path !== '/')
    chain.push({ label: (route.meta.title as string) || '页面', path: route.path })
  return chain
})

const userOptions = [
  { label: '退出登录', key: 'logout' },
]

function onMenu(key: string) {
  void router.push(key)
}

async function onUserAction(key: string) {
  if (key === 'logout') {
    await auth.logout()
    await router.replace('/login')
  }
}
</script>

<template>
  <n-layout has-sider class="shell">
    <n-layout-sider
      bordered
      collapse-mode="width"
      :collapsed-width="64"
      :width="220"
      :collapsed="collapsed"
      show-trigger
      @collapse="collapsed = true"
      @expand="collapsed = false"
    >
      <div class="shell__brand" :class="{ 'shell__brand--collapsed': collapsed }">
        <span class="shell__logo">星</span>
        <span v-if="!collapsed" class="shell__name">星栖 StarHaven</span>
      </div>
      <n-menu
        :value="activeKey"
        :collapsed="collapsed"
        :collapsed-width="64"
        :collapsed-icon-size="22"
        :options="menuOptions"
        @update:value="onMenu"
      />
    </n-layout-sider>
    <n-layout>
      <n-layout-header bordered class="shell__header">
        <n-breadcrumb>
          <n-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
            {{ item.label }}
          </n-breadcrumb-item>
        </n-breadcrumb>
        <n-space align="center" :size="16">
          <n-icon size="20" color="#666">
            <NotificationsOutline />
          </n-icon>
          <n-dropdown :options="userOptions" @select="onUserAction">
            <n-space align="center" :size="8" class="shell__user">
              <n-avatar round size="small" :src="auth.user?.avatar">
                {{ (auth.user?.nickname || auth.user?.username || '?').slice(0, 1) }}
              </n-avatar>
              <span>{{ auth.user?.nickname || auth.user?.username }}</span>
            </n-space>
          </n-dropdown>
        </n-space>
      </n-layout-header>
      <n-layout-content class="shell__content">
        <router-view />
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>

<style scoped>
.shell {
  min-height: 100vh;
}
.shell__brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 18px 20px 14px;
  color: #fff;
}
.shell__brand--collapsed {
  justify-content: center;
  padding-inline: 0;
}
.shell__logo {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: linear-gradient(135deg, #2878ff, #73d5c5);
  display: grid;
  place-items: center;
  font-weight: 700;
}
.shell__name {
  font-size: 16px;
  font-weight: 700;
  white-space: nowrap;
}
.shell__header {
  height: 60px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
}
.shell__user {
  cursor: pointer;
  color: #1a1a1a;
}
.shell__content {
  padding: 20px 24px 32px;
  background: #f6f8fb;
}
</style>
