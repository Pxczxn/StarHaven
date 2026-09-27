<script lang="ts" setup>
import type { HouseCard } from '@/api/types/stay'
import { fetchFavorites } from '@/api/stay'
import { useTokenStore } from '@/store/token'

definePage({ style: { navigationBarTitleText: '我的收藏' } })

const tokenStore = useTokenStore()
const list = ref<HouseCard[]>([])
const loading = ref(false)
const countText = computed(() => list.value.length ? `${list.value.length} 个心动住处` : '把喜欢的住处留在这里')

async function load() {
  if (!tokenStore.updateNowTime().hasLogin) {
    list.value = []
    return
  }
  loading.value = true
  try {
    const res = await fetchFavorites(1, 50)
    list.value = res.list || []
  }
  finally { loading.value = false }
}

function goLogin() {
  uni.navigateTo({ url: '/pages/login/index' })
}
function goExplore() {
  uni.switchTab({ url: '/pages/index/index' })
}
onShow(load)
</script>

<template>
  <view class="favorites">
    <view class="favorites__hero">
      <view class="favorites__title">
        心愿住处
      </view>
      <view class="favorites__subtitle">
        {{ countText }}
      </view>
    </view>

    <view v-if="loading" class="favorites__list">
      <view v-for="index in 2" :key="index" class="skeleton">
        <view class="skeleton__image" />
        <view class="skeleton__line skeleton__line--title" />
        <view class="skeleton__line" />
      </view>
    </view>

    <view v-else-if="list.length" class="favorites__list">
      <view class="favorites__section-head">
        <text>全部收藏</text><text class="favorites__count">{{ list.length }} 个</text>
      </view>
      <fg-house-card v-for="item in list" :key="item.id" :item="item" />
    </view>

    <view v-else class="empty">
      <view class="empty__art">
        <view class="empty__circle empty__circle--back" />
        <view class="empty__circle">
          <view class="i-tabler-heart-search" />
        </view>
      </view>
      <view class="empty__title">
        {{ tokenStore.hasLogin ? '还没有心动收藏' : '登录后保存心动住处' }}
      </view>
      <view class="empty__desc">
        {{ tokenStore.hasLogin ? '看到喜欢的房源，点亮爱心就能随时回来查看' : '收藏喜欢的房源，下次预订更轻松' }}
      </view>
      <button class="empty__button" @click="tokenStore.hasLogin ? goExplore() : goLogin()">
        {{ tokenStore.hasLogin ? '去逛逛' : '立即登录' }}<view class="i-tabler-arrow-right" />
      </button>
    </view>
  </view>
</template>

<style scoped lang="scss">
.favorites {
  min-height: 100vh;
  padding: 18px 16px 100px;
  background: #f6f9fc;
  box-sizing: border-box;
}
.favorites__hero {
  padding: 20px 4px 14px;
}
.favorites__title {
  color: #17243b;
  font-size: 22px;
  font-weight: 700;
}
.favorites__subtitle {
  margin-top: 4px;
  color: #8a99ae;
  font-size: 12px;
}
.favorites__section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 4px 2px 12px;
  color: #17243b;
  font-size: 16px;
  font-weight: 700;
}
.favorites__count {
  color: #8a99ae;
  font-size: 12px;
  font-weight: 500;
}
.skeleton {
  padding-bottom: 14px;
  margin-bottom: 14px;
  overflow: hidden;
  background: #fff;
  border-radius: 16px;
}
.skeleton__image {
  height: 156px;
  background: #e9eef5;
}
.skeleton__line {
  width: 44%;
  height: 10px;
  margin: 9px 14px 0;
  background: #eef2f7;
  border-radius: 10px;
}
.skeleton__line--title {
  width: 68%;
  height: 14px;
  margin-top: 13px;
}
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 50px 24px 0;
  text-align: center;
}
.empty__art {
  position: relative;
  width: 112px;
  height: 100px;
}
.empty__circle {
  position: absolute;
  top: 10px;
  left: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 72px;
  color: #2377eb;
  font-size: 34px;
  background: #eaf3ff;
  border-radius: 28px;
  transform: rotate(-4deg);
}
.empty__circle--back {
  top: 1px;
  left: 36px;
  width: 58px;
  height: 58px;
  background: #fff0e7;
  transform: rotate(12deg);
}
.empty__title {
  margin-top: 8px;
  color: #17243b;
  font-size: 18px;
  font-weight: 700;
}
.empty__desc {
  max-width: 260px;
  margin-top: 8px;
  color: #8a99ae;
  font-size: 13px;
  line-height: 1.7;
}
.empty__button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  width: 174px;
  height: 46px;
  margin-top: 24px;
  color: #fff;
  font-size: 14px;
  font-weight: 650;
  background: #2377eb;
  border: 0;
  border-radius: 15px;
  box-shadow: 0 8px 18px rgba(35, 119, 235, 0.2);
}
.empty__button::after {
  border: 0;
}
</style>
