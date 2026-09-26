<script lang="ts" setup>
import type { CouponItem } from '@/api/types/stay'
import { fetchMyCoupons } from '@/api/stay'
import { useTokenStore } from '@/store/token'

definePage({
  style: { navigationBarTitleText: '优惠券' },
})

const tokenStore = useTokenStore()
const list = ref<CouponItem[]>([])

function goLogin() {
  uni.navigateTo({ url: '/pages/login/index' })
}

onShow(async () => {
  if (!tokenStore.updateNowTime().hasLogin) {
    list.value = []
    return
  }
  list.value = await fetchMyCoupons()
})
</script>

<template>
  <view class="min-h-screen bg-page p-4">
    <view v-for="item in list" :key="item.couponUserId" class="mb-3 flex items-center justify-between rounded-3 bg-white p-4">
      <view>
        <view class="text-lg font-semibold">
          {{ item.name }}
        </view>
        <view class="text-xs text-muted">
          满 {{ item.conditionAmount }} 可用
        </view>
      </view>
      <view class="text-primary">
        ¥{{ item.discount }}
      </view>
    </view>
    <fg-empty
      v-if="!list.length"
      :text="tokenStore.hasLogin ? '暂无优惠券' : '登录后查看优惠券'"
      :action="tokenStore.hasLogin ? 'none' : 'login'"
      @retry="goLogin"
    />
  </view>
</template>
