<script lang="ts" setup>
import { clearSearchHistory, fetchHotKeywords, fetchSearchHistory } from '@/api/stay'
import { useTokenStore } from '@/store/token'

definePage({
  style: { navigationBarTitleText: '搜索' },
})

const tokenStore = useTokenStore()
const keyword = ref('')
const hot = ref<string[]>([])
const history = ref<string[]>([])

onShow(async () => {
  try {
    hot.value = await fetchHotKeywords()
  }
  catch {
    hot.value = ['东京', '京都', '北海道', '海景房']
  }
  if (!tokenStore.updateNowTime().hasLogin) {
    history.value = []
    return
  }
  try {
    history.value = await fetchSearchHistory()
  }
  catch {
    history.value = []
  }
})

function go(word: string) {
  uni.navigateTo({ url: `/pages/listing/listing?keyword=${encodeURIComponent(word)}` })
}

function search() {
  if (!keyword.value.trim())
    return
  go(keyword.value.trim())
}

async function clearHistory() {
  if (!tokenStore.updateNowTime().hasLogin) {
    history.value = []
    return
  }
  await clearSearchHistory()
  history.value = []
}
</script>

<template>
  <view class="min-h-screen bg-page p-4">
    <view class="flex items-center rounded-full bg-white px-4 py-2">
      <input v-model="keyword" class="flex-1" placeholder="海边 / 温泉 / 民宿" confirm-type="search" @confirm="search">
      <view class="text-primary" @click="search">
        搜索
      </view>
    </view>
    <view class="mt-6">
      <view class="mb-2 text-sm text-muted">
        热门搜索
      </view>
      <view class="flex flex-wrap">
        <view v-for="item in hot" :key="item" class="mb-2 mr-2 rounded-full bg-white px-3 py-1 text-sm" @click="go(item)">
          {{ item }}
        </view>
      </view>
    </view>
    <view class="mt-6">
      <view class="mb-2 flex justify-between text-sm text-muted">
        <text>历史记录</text>
        <text @click="clearHistory">清空</text>
      </view>
      <view v-for="item in history" :key="item" class="py-2 text-ink" @click="go(item)">
        {{ item }}
      </view>
    </view>
  </view>
</template>
