<script lang="ts" setup>
import type { OrderItem } from '@/api/types/stay'
import { fetchMyOrders } from '@/api/stay'
import { useTokenStore } from '@/store/token'

definePage({
  style: { navigationBarTitleText: '我的订单' },
})

const tokenStore = useTokenStore()
const tabs = [
  { label: '全部', value: '' },
  { label: '待付款', value: 'WAIT_PAY' },
  { label: '待入住', value: 'PAID' },
  { label: '已完成', value: 'FINISHED' },
]
const current = ref('')
const list = ref<OrderItem[]>([])
const loading = ref(false)

const statusText: Record<string, string> = {
  WAIT_PAY: '待付款',
  PAID: '待入住',
  CHECK_IN: '入住中',
  FINISHED: '已完成',
  CANCEL: '已取消',
}

async function load() {
  if (!tokenStore.updateNowTime().hasLogin) {
    list.value = []
    return
  }
  loading.value = true
  try {
    const res = await fetchMyOrders(current.value || undefined)
    list.value = res.list || []
  }
  finally {
    loading.value = false
  }
}

function openDetail(id: number) {
  uni.navigateTo({ url: `/pages/order-detail/order-detail?id=${id}` })
}

function goLogin() {
  uni.navigateTo({ url: '/pages/login/index' })
}

onShow(load)
</script>

<template>
  <view class="min-h-screen bg-page pb-24">
    <view class="flex bg-white px-2 py-3 text-sm">
      <view
        v-for="tab in tabs" :key="tab.value"
        class="flex-1 text-center"
        :class="current === tab.value ? 'text-primary font-semibold' : 'text-muted'"
        @click="current = tab.value; load()"
      >
        {{ tab.label }}
      </view>
    </view>
    <view class="p-4">
      <view v-for="item in list" :key="item.id" class="mb-3 rounded-3 bg-white p-3" @click="openDetail(item.id)">
        <view class="flex">
          <image :src="item.houseCover" class="h-20 w-24 rounded-2" mode="aspectFill" />
          <view class="ml-3 flex-1">
            <view class="font-semibold">
              {{ item.houseTitle }}
            </view>
            <view class="mt-1 text-xs text-muted">
              {{ item.checkInDate }} 至 {{ item.checkOutDate }}
            </view>
            <view class="mt-2 flex justify-between text-sm">
              <text class="text-primary">{{ statusText[item.orderStatus] }}</text>
              <text>¥{{ item.totalAmount }}</text>
            </view>
          </view>
        </view>
      </view>
      <fg-empty
        v-if="!loading && !list.length"
        :text="tokenStore.hasLogin ? '暂无订单' : '登录后查看订单'"
        :action="tokenStore.hasLogin ? 'none' : 'login'"
        @retry="goLogin"
      />
    </view>
  </view>
</template>
