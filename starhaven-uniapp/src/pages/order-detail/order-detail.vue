<script lang="ts" setup>
import type { OrderItem } from '@/api/types/stay'
import { cancelOrder, fetchOrderDetail } from '@/api/stay'

definePage({
  style: { navigationBarTitleText: '订单详情' },
})

const order = ref<OrderItem>()

async function load(id: number) {
  order.value = await fetchOrderDetail(id)
}

onLoad((options) => {
  load(Number(options?.id || 0))
})

const statusText: Record<string, string> = {
  WAIT_PAY: '待付款',
  PAID: '待入住',
  CHECK_IN: '入住中',
  FINISHED: '已完成',
  CANCEL: '已取消',
}

async function cancel() {
  if (!order.value)
    return
  await cancelOrder(order.value.id)
  await load(order.value.id)
}

function pay() {
  uni.navigateTo({ url: `/pages/payment/payment?id=${order.value?.id}` })
}

function review() {
  uni.navigateTo({ url: `/pages/reviews/reviews?houseId=${order.value?.houseId}&orderId=${order.value?.id}` })
}
</script>

<template>
  <view v-if="order" class="min-h-screen bg-page p-4">
    <view class="rounded-3 bg-white p-4">
      <view class="text-primary">
        {{ statusText[order.orderStatus] }}
      </view>
      <view class="mt-2 font-semibold">
        {{ order.houseTitle }}
      </view>
      <image :src="order.houseCover" class="mt-3 h-36 w-full rounded-2" mode="aspectFill" />
      <view class="mt-3 text-sm text-muted">
        {{ order.checkInDate }} 至 {{ order.checkOutDate }}
      </view>
      <view class="mt-1 text-sm text-muted">
        订单号 {{ order.orderNo }}
      </view>
      <view class="mt-3 text-lg text-primary">
        ¥{{ order.totalAmount }}
      </view>
    </view>
    <view class="mt-4 flex justify-end">
      <view v-if="order.orderStatus === 'WAIT_PAY'" class="mr-3 rounded-full border px-4 py-2" @click="cancel">
        取消订单
      </view>
      <view v-if="order.orderStatus === 'WAIT_PAY'" class="rounded-full bg-primary px-4 py-2 text-white" @click="pay">
        立即支付
      </view>
      <view v-if="order.orderStatus === 'FINISHED'" class="rounded-full bg-primary px-4 py-2 text-white" @click="review">
        评价
      </view>
    </view>
  </view>
</template>
