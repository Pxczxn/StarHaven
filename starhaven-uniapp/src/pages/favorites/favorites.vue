<script lang="ts" setup>
import type { HouseCard } from '@/api/types/stay'
import { fetchFavorites, toggleFavorite } from '@/api/stay'
import { useTokenStore } from '@/store/token'
import { ensureLogin } from '@/utils/toLoginPage'

definePage({
  style: { navigationBarTitleText: '我的收藏' },
})

const tokenStore = useTokenStore()
const list = ref<HouseCard[]>([])
const loading = ref(false)

async function load() {
  if (!tokenStore.updateNowTime().hasLogin) {
    list.value = []
    return
  }
  loading.value = true
  try {
    const res = await fetchFavorites(1, 50)
    list.value = res.list || []
  }
  finally {
    loading.value = false
  }
}

async function onFavorite(item: HouseCard) {
  if (!ensureLogin())
    return
  await toggleFavorite(item.id)
  await load()
}

function goLogin() {
  uni.navigateTo({ url: '/pages/login/index' })
}

onShow(load)
</script>

<template>
  <view class="min-h-screen bg-page p-4 pb-24">
    <fg-house-card v-for="item in list" :key="item.id" :item="item" @favorite="onFavorite" />
    <fg-empty
      v-if="!loading && !list.length"
      :text="tokenStore.hasLogin ? '还没有收藏' : '登录后查看收藏'"
      :action="tokenStore.hasLogin ? 'none' : 'login'"
      @retry="goLogin"
    />
  </view>
</template>
