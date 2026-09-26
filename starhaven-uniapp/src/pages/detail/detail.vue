<script lang="ts" setup>
import type { HouseDetail } from '@/api/types/stay'
import { fetchHouseDetail, toggleFavorite } from '@/api/stay'
import { ensureLogin } from '@/utils/toLoginPage'

definePage({
  style: { navigationBarTitleText: '房源详情' },
})

const id = ref(0)
const detail = ref<HouseDetail>()
const loading = ref(true)

async function load() {
  loading.value = true
  try {
    detail.value = await fetchHouseDetail(id.value)
  }
  finally {
    loading.value = false
  }
}

async function favorite() {
  if (!detail.value || !ensureLogin()) {
    return
  }
  const res = await toggleFavorite(detail.value.id)
  detail.value.favorited = res.favorited
}

function preview(img: string) {
  if (!detail.value)
    return
  uni.previewImage({ urls: detail.value.images, current: img })
}

function goReviews() {
  if (!detail.value)
    return
  uni.navigateTo({ url: `/pages/reviews/reviews?houseId=${detail.value.id}` })
}

function book() {
  if (!ensureLogin()) {
    return
  }
  uni.navigateTo({ url: `/pages/booking/booking?id=${id.value}` })
}

onLoad((options) => {
  id.value = Number(options?.id || 0)
  load()
})
</script>

<template>
  <view v-if="detail" class="min-h-screen bg-page pb-28">
    <swiper class="h-56" indicator-dots circular>
      <swiper-item v-for="(img, index) in (detail.images?.length ? detail.images : [detail.coverImage])" :key="index">
        <image :src="img" mode="aspectFill" class="h-full w-full" @click="preview(img)" />
      </swiper-item>
    </swiper>
    <view class="relative -mt-4 rounded-t-3 bg-white p-4">
      <view class="text-xl text-ink font-semibold">
        {{ detail.title }}
      </view>
      <view class="mt-2 text-sm text-secondary">
        ★ {{ detail.avgScore }} · {{ detail.commentCount }} 条评价
      </view>
      <view class="mt-1 text-sm text-muted">
        {{ detail.city }} {{ detail.address }}
      </view>
      <view class="mt-4 text-sm leading-6 text-muted">
        {{ detail.description }}
      </view>
      <view class="mt-4 grid grid-cols-4 gap-2 text-center text-xs text-muted">
        <view>{{ detail.guestNumber }}人</view>
        <view>{{ detail.roomNumber }}室</view>
        <view>{{ detail.bedNumber }}床</view>
        <view>{{ detail.bathroomNumber }}卫</view>
      </view>
      <view class="mt-4 flex flex-wrap">
        <view v-for="name in detail.facilities" :key="name" class="mb-2 mr-2 rounded-full bg-#E8F8F4 px-3 py-1 text-xs text-secondary">
          {{ name }}
        </view>
      </view>
      <view class="mt-4 flex items-center justify-between rounded-2 bg-#F7FAFD p-3">
        <view class="flex items-center">
          <image :src="detail.hostAvatar" class="h-10 w-10 rounded-full" />
          <view class="ml-3">
            <view class="text-sm text-ink">
              {{ detail.hostNickname }}
            </view>
            <view class="text-xs text-secondary">
              {{ detail.hostCertified ? '认证房东' : '房东' }}
            </view>
          </view>
        </view>
        <view class="text-xs text-primary">
          联系房东
        </view>
      </view>
      <view class="mt-4 text-sm text-primary" @click="goReviews">
        查看全部评价
      </view>
    </view>
    <view class="fixed bottom-0 left-0 right-0 flex items-center justify-between border-t border-#EEE bg-white px-4 py-3 pb-safe">
      <view>
        <text class="text-xl text-primary font-semibold">¥{{ detail.price }}</text>
        <text class="text-xs text-faint"> /晚</text>
      </view>
      <view class="flex">
        <view class="mr-3 rounded-full border border-primary px-4 py-2 text-primary" @click="favorite">
          {{ detail.favorited ? '已收藏' : '收藏' }}
        </view>
        <view class="rounded-full bg-primary px-6 py-2 text-white" @click="book">
          立即预订
        </view>
      </view>
    </view>
  </view>
  <view v-else class="center py-20 text-faint">
    {{ loading ? '加载中...' : '房源不存在' }}
  </view>
</template>
