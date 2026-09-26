<script lang="ts" setup>
import type { MessageItem } from '@/api/types/stay'
import { fetchMessages, readMessage } from '@/api/stay'
import { useTokenStore } from '@/store/token'

definePage({
  style: { navigationBarTitleText: '消息通知' },
})

const tokenStore = useTokenStore()
const list = ref<MessageItem[]>([])
const typeText: Record<string, string> = {
  ORDER: '订单通知',
  SYSTEM: '系统通知',
  ACTIVITY: '活动消息',
}

function goLogin() {
  uni.navigateTo({ url: '/pages/login/index' })
}

onShow(async () => {
  if (!tokenStore.updateNowTime().hasLogin) {
    list.value = []
    return
  }
  list.value = await fetchMessages()
})

async function open(item: MessageItem) {
  await readMessage(item.id)
  item.readStatus = 1
}
</script>

<template>
  <view class="min-h-screen bg-page p-4">
    <view v-for="item in list" :key="item.id" class="mb-3 rounded-3 bg-white p-4" @click="open(item)">
      <view class="flex justify-between">
        <text class="font-semibold">{{ item.title }}</text>
        <text class="text-xs text-faint">{{ typeText[item.type] }}</text>
      </view>
      <view class="mt-2 text-sm text-muted">
        {{ item.content }}
      </view>
      <view class="mt-2 text-xs text-faint">
        {{ item.createTime }} {{ item.readStatus ? '已读' : '未读' }}
      </view>
    </view>
    <fg-empty
      v-if="!list.length"
      :text="tokenStore.hasLogin ? '暂无消息' : '登录后查看消息'"
      :action="tokenStore.hasLogin ? 'none' : 'login'"
      @retry="goLogin"
    />
  </view>
</template>
