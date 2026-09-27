<script lang="ts" setup>
import type { HouseCard } from '@/api/types/stay'
import { clearBrowseHistory, fetchBrowseHistory } from '@/api/stay'
import { useTokenStore } from '@/store/token'

definePage({
  style: { navigationBarTitleText: '浏览记录' },
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
    list.value = await fetchBrowseHistory()
  }
  finally {
    loading.value = false
  }
}

async function onClear() {
  const confirmed = await uni.showModal({
    title: '清空浏览记录',
    content: '确定要清空全部浏览记录吗？',
  }).then(res => res.confirm).catch(() => false)
  if (!confirmed) {
    return
  }
  await clearBrowseHistory()
  list.value = []
  uni.showToast({ title: '已清空', icon: 'none' })
}

function goExplore() {
  uni.switchTab({ url: '/pages/index/index' })
}

function goLogin() {
  uni.navigateTo({ url: '/pages/login/index' })
}

onShow(load)
</script>

<template>
  <view class="min-h-screen bg-page p-4 pb-28">
    <view v-if="loading" class="center py-20 text-faint">
      加载中...
    </view>
    <template v-else-if="list.length">
      <view class="mb-3 flex items-center justify-between">
        <text class="text-sm text-ink font-medium">
          最近浏览（{{ list.length }}个）
        </text>
        <text class="text-xs text-faint" @click="onClear">
          清空
        </text>
      </view>
      <fg-house-card v-for="item in list" :key="item.id" :item="item" />
    </template>
    <view v-else class="pt-10">
      <fg-empty text="还没有浏览记录" action="none" />
      <view class="mt-4 center rounded-full bg-primary px-6 py-2 text-sm text-white" @click="tokenStore.hasLogin ? goExplore() : goLogin()">
        {{ tokenStore.hasLogin ? '去逛逛' : '立即登录' }}
      </view>
    </view>
  </view>
</template>
