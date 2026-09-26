<script lang="ts" setup>
const props = defineProps<{
  text?: string
  action?: string
}>()

const emit = defineEmits<{
  (e: 'retry'): void
}>()

function onAction() {
  if (props.action === 'retry' || props.action === 'login') {
    emit('retry')
    return
  }
  uni.switchTab({ url: '/pages/index/index' })
}
</script>

<template>
  <view class="center flex-col py-20 text-faint">
    <view class="mb-3 h-24 w-24 rounded-full bg-#E8F1FF" />
    <view>{{ text || '暂时没有内容' }}</view>
    <view v-if="action !== 'none'" class="mt-4 rounded-full bg-primary px-6 py-2 text-white" @click="onAction">
      {{ action === 'retry' ? '重新加载' : (action === 'login' ? '去登录' : '返回首页') }}
    </view>
  </view>
</template>
