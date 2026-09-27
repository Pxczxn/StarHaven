<script lang="ts" setup>
import { fetchBrowseCount, fetchMyCoupons, fetchMyOrders } from '@/api/stay'
import { useTokenStore } from '@/store/token'
import { useUserStore } from '@/store/user'

definePage({
  style: { navigationBarTitleText: '我的' },
})

const userStore = useUserStore()
const tokenStore = useTokenStore()
const orderCount = ref(0)
const couponCount = ref(0)
const browseCount = ref(0)

onShow(async () => {
  if (!tokenStore.updateNowTime().hasLogin) {
    orderCount.value = 0
    couponCount.value = 0
    browseCount.value = 0
    return
  }
  try {
    await userStore.fetchUserInfo()
    const orders = await fetchMyOrders()
    orderCount.value = orders.total
    const coupons = await fetchMyCoupons()
    couponCount.value = coupons.filter(item => !item.used).length
    browseCount.value = await fetchBrowseCount()
  }
  catch {
    // 未登录时拦截器会处理
  }
})

function goOrders() {
  uni.switchTab({ url: '/pages/orders/orders' })
}

function goFavorites() {
  uni.switchTab({ url: '/pages/favorites/favorites' })
}

function onAvatar() {
  if (!tokenStore.updateNowTime().hasLogin) {
    uni.navigateTo({ url: '/pages/login/index' })
  }
}

function needLogin(url: string) {
  if (!tokenStore.updateNowTime().hasLogin) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  uni.navigateTo({ url })
}

async function logout() {
  await tokenStore.logout()
  orderCount.value = 0
  couponCount.value = 0
  browseCount.value = 0
  uni.showToast({ title: '已退出', icon: 'none' })
}
</script>

<template>
  <view class="min-h-screen bg-page pb-24">
    <view class="from-#EAF3FF to-page bg-gradient-to-br px-4 pb-8 pt-8">
      <view class="flex items-center" @click="onAvatar">
        <image :src="userStore.userInfo.avatar" class="h-16 w-16 rounded-full" />
        <view class="ml-4">
          <view class="text-xl font-semibold">
            {{ tokenStore.hasLogin ? userStore.userInfo.nickname : '点击登录' }}
          </view>
          <view class="mt-1 text-sm text-muted">
            {{ tokenStore.hasLogin ? (userStore.userInfo.phone || userStore.userInfo.username) : '登录后同步订单与收藏' }}
          </view>
        </view>
      </view>
      <view class="mt-6 flex rounded-3 bg-white py-4 text-center shadow-sm">
        <view class="flex-1" @click="goOrders">
          <view class="text-lg font-semibold">
            {{ orderCount }}
          </view>
          <view class="text-xs text-faint">
            订单
          </view>
        </view>
        <view class="flex-1" @click="goFavorites">
          <view class="text-lg font-semibold">
            {{ userStore.userInfo.favoriteCount || 0 }}
          </view>
          <view class="text-xs text-faint">
            收藏
          </view>
        </view>
        <view class="flex-1" @click="needLogin('/pages/coupons/coupons')">
          <view class="text-lg font-semibold">
            {{ couponCount }}
          </view>
          <view class="text-xs text-faint">
            优惠券
          </view>
        </view>
      </view>
    </view>
    <view class="mx-4 overflow-hidden rounded-3 bg-white">
      <view class="flex items-center justify-between px-4 py-4" @click="needLogin('/pages/history/history')">
        <text>浏览记录</text>
        <view class="flex items-center">
          <text v-if="tokenStore.hasLogin" class="mr-2 text-xs text-faint">
            {{ browseCount }} 条
          </text>
          <text class="i-carbon-chevron-right text-faint" />
        </view>
      </view>
      <view class="flex items-center justify-between px-4 py-4" @click="goOrders">
        <text>我的订单</text><text class="i-carbon-chevron-right text-faint" />
      </view>
      <view class="flex items-center justify-between px-4 py-4" @click="needLogin('/pages/messages/messages')">
        <text>消息通知</text><text class="i-carbon-chevron-right text-faint" />
      </view>
      <view class="flex items-center justify-between px-4 py-4" @click="needLogin('/pages/coupons/coupons')">
        <text>优惠券</text><text class="i-carbon-chevron-right text-faint" />
      </view>
      <view v-if="tokenStore.hasLogin" class="flex items-center justify-between px-4 py-4 text-primary" @click="logout">
        <text>退出登录</text>
      </view>
    </view>
  </view>
</template>
