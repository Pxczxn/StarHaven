<script lang="ts" setup>
import type { OrderItem } from '@/api/types/stay'
import { createPay, fetchOrderDetail, mockPaySuccess } from '@/api/stay'

definePage({
  style: { navigationBarTitleText: '支付' },
})

const order = ref<OrderItem>()
const payType = ref('WECHAT')
const paying = ref(false)

onLoad(async (options) => {
  order.value = await fetchOrderDetail(Number(options?.id || 0))
})

async function pay() {
  if (!order.value)
    return
  paying.value = true
  try {
    await createPay(order.value.id, payType.value)
    await mockPaySuccess(order.value.id)
    uni.showToast({ title: '支付成功', icon: 'success' })
    setTimeout(() => {
      uni.redirectTo({ url: `/pages/order-detail/order-detail?id=${order.value!.id}` })
    }, 400)
  }
  finally {
    paying.value = false
  }
}
</script>

<template>
  <view v-if="order" class="min-h-screen bg-page p-4">
    <view class="rounded-3 bg-white p-6 text-center">
      <view class="text-faint">
        订单金额
      </view>
      <view class="mt-2 text-3xl text-primary font-semibold">
        ¥{{ order.totalAmount }}
      </view>
    </view>
    <view class="mt-4 rounded-3 bg-white">
      <view class="flex items-center justify-between border-b border-#F3F3F3 px-4 py-4" @click="payType = 'WECHAT'">
        <text>微信支付</text>
        <text v-if="payType === 'WECHAT'" class="text-primary">已选</text>
      </view>
      <view class="flex items-center justify-between px-4 py-4" @click="payType = 'ALIPAY'">
        <text>支付宝</text>
        <text v-if="payType === 'ALIPAY'" class="text-primary">已选</text>
      </view>
    </view>
    <view class="mt-8 center h-11 rounded-full bg-primary text-white" @click="pay">
      {{ paying ? '支付中...' : '立即支付' }}
    </view>
  </view>
</template>
