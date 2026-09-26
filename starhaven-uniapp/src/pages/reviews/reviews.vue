<script lang="ts" setup>
import type { CommentItem } from '@/api/types/stay'
import { createComment, fetchComments } from '@/api/stay'
import { ensureLogin } from '@/utils/toLoginPage'

definePage({
  style: { navigationBarTitleText: '用户评价' },
})

const houseId = ref(0)
const orderId = ref(0)
const list = ref<CommentItem[]>([])
const content = ref('')
const score = ref(5)

async function load() {
  list.value = await fetchComments(houseId.value)
}

onLoad((options) => {
  houseId.value = Number(options?.houseId || 0)
  orderId.value = Number(options?.orderId || 0)
  load()
})

async function submit() {
  if (!ensureLogin())
    return
  if (!orderId.value) {
    uni.showToast({ title: '请从已完成订单进入评价', icon: 'none' })
    return
  }
  await createComment({ orderId: orderId.value, score: score.value, content: content.value })
  content.value = ''
  await load()
}
</script>

<template>
  <view class="min-h-screen bg-page p-4 pb-28">
    <view v-for="item in list" :key="item.id" class="mb-3 rounded-3 bg-white p-4">
      <view class="flex items-center">
        <image :src="item.avatar" class="h-8 w-8 rounded-full" />
        <view class="ml-2">
          <view class="text-sm">
            {{ item.nickname }}
          </view>
          <view class="text-xs text-faint">
            {{ item.createTime }}
          </view>
        </view>
      </view>
      <view class="mt-2 text-secondary">
        ★ {{ item.score }}
      </view>
      <view class="mt-1 text-sm text-muted">
        {{ item.content }}
      </view>
    </view>
    <fg-empty v-if="!list.length" text="还没有评价" action="none" />
    <view v-if="orderId" class="fixed bottom-0 left-0 right-0 bg-white p-3 pb-safe">
      <input v-model="content" class="mb-2 rounded-full bg-#F7FAFD px-4 py-2" placeholder="写下你的入住感受">
      <view class="center h-10 rounded-full bg-primary text-white" @click="submit">
        发表评论
      </view>
    </view>
  </view>
</template>
