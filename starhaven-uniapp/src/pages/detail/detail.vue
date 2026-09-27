<script lang="ts" setup>
import type { CommentItem, HouseDetail } from '@/api/types/stay'
import { fetchComments, fetchHouseDetail, recordBrowse, toggleFavorite } from '@/api/stay'
import { ensureLogin } from '@/utils/toLoginPage'
import { useTokenStore } from '@/store/token'

definePage({
  style: { navigationBarTitleText: '房源详情' },
})

const id = ref(0)
const detail = ref<HouseDetail>()
const comments = ref<CommentItem[]>([])
const tokenStore = useTokenStore()
const showAllReviews = ref(false)
const loading = ref(true)

async function load() {
  loading.value = true
  try {
    const [detailRes, commentRes] = await Promise.all([
      fetchHouseDetail(id.value),
      fetchComments(id.value).catch(() => [] as CommentItem[]),
    ])
    detail.value = detailRes
    comments.value = commentRes
    // 登录状态下记录一次浏览，失败不影响详情展示
    if (tokenStore.updateNowTime().hasLogin) {
      recordBrowse(id.value).catch(() => {})
    }
  }
  finally {
    loading.value = false
  }
}

const previewComments = computed(() => comments.value.slice(0, 2))
// 房源的 avgScore/commentCount 是后端冗余字段，可能与真实评论不一致，这里一律以评论列表为准
const realCommentCount = computed(() => comments.value.length)
const realAvgScore = computed(() => {
  if (!comments.value.length)
    return ''
  const avg = comments.value.reduce((sum, item) => sum + (item.score || 0), 0) / comments.value.length
  return avg.toFixed(1)
})

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
  showAllReviews.value = true
}

function closeReviews() {
  showAllReviews.value = false
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
    <view class="relative rounded-t-3 bg-white p-4 -mt-4">
      <view class="text-xl text-ink font-semibold">
        {{ detail.title }}
      </view>
      <view class="mt-2 text-sm text-secondary">
        <template v-if="realCommentCount">
          ★ {{ realAvgScore }} · {{ realCommentCount }} 条评价
        </template>
        <template v-else>
          暂无评价
        </template>
      </view>
      <view class="mt-1 text-sm text-muted">
        {{ detail.city }} {{ detail.address }}
      </view>
      <view class="mt-4 text-sm text-muted leading-6">
        {{ detail.description }}
      </view>
      <view class="grid grid-cols-4 mt-4 gap-2 text-center text-xs text-muted">
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
      <view class="mt-4">
        <view class="mb-2 text-sm text-ink font-medium">
          用户评价（{{ comments.length }}条）
        </view>
        <template v-if="comments.length">
          <view v-for="item in previewComments" :key="item.id" class="mb-2 rounded-2 bg-#F7FAFD p-3">
            <view class="flex items-center">
              <image :src="item.avatar" class="h-8 w-8 rounded-full" />
              <view class="ml-2">
                <view class="text-sm text-ink">
                  {{ item.nickname }}
                </view>
                <view class="text-xs text-faint">
                  {{ item.createTime }}
                </view>
              </view>
              <view class="ml-auto text-xs text-primary">
                ★ {{ item.score }}
              </view>
            </view>
            <view class="mt-2 text-sm text-muted">
              {{ item.content }}
            </view>
          </view>
          <view v-if="comments.length > previewComments.length" class="center py-1 text-sm text-primary" @click="goReviews">
            查看全部评价（{{ comments.length }}条）
          </view>
        </template>
        <view v-else class="center rounded-2 bg-#F7FAFD py-6 text-sm text-faint">
          暂时没有评价
        </view>
      </view>
    </view>
    <view class="fixed bottom-0 left-0 right-0 flex items-center justify-between border-t border-#EEE bg-white px-4 py-3 pb-safe">
      <view>
        <text class="text-xl text-primary font-semibold">¥{{ detail.price }}</text>
        <text class="text-xs text-faint"> /晚</text>
      </view>
      <view class="flex">
        <view class="mr-4 w-12 flex flex-col items-center justify-center" @click="favorite">
          <view
            class="h-5 w-5"
            :class="detail.favorited ? 'i-carbon-favorite-filled text-#E11D48' : 'i-carbon-favorite text-#64748B'"
          />
          <text
            class="mt-1 text-xs leading-none"
            :class="detail.favorited ? 'text-#E11D48' : 'text-#64748B'"
          >
            {{ detail.favorited ? '已收藏' : '收藏' }}
          </text>
        </view>
        <view class="rounded-full bg-primary px-8 py-2 text-white" @click="book">
          立即预订
        </view>
      </view>
    </view>
  </view>
  <view v-else class="center py-20 text-faint">
    {{ loading ? '加载中...' : '房源不存在' }}
  </view>
  <view v-if="showAllReviews" class="detail__reviews-mask" @click="closeReviews" @touchmove.stop.prevent>
    <view class="detail__reviews-sheet" @click.stop>
      <view class="detail__reviews-head">
        <text>全部评价（{{ comments.length }}条）</text>
        <view class="i-carbon-close detail__reviews-close" @click="closeReviews" />
      </view>
      <view class="detail__reviews-list">
        <view v-for="item in comments" :key="item.id" class="mb-2 rounded-2 bg-#F7FAFD p-3">
          <view class="flex items-center">
            <image :src="item.avatar" class="h-8 w-8 rounded-full" />
            <view class="ml-2">
              <view class="text-sm text-ink">
                {{ item.nickname }}
              </view>
              <view class="text-xs text-faint">
                {{ item.createTime }}
              </view>
            </view>
            <view class="ml-auto text-xs text-primary">
              ★ {{ item.score }}
            </view>
          </view>
          <view class="mt-2 text-sm text-muted">
            {{ item.content }}
          </view>
        </view>
        <view v-if="!comments.length" class="center py-10 text-sm text-faint">
          暂时没有评价
        </view>
      </view>
    </view>
  </view>
</template>

<style>
.detail__reviews-mask {
  position: fixed;
  inset: 0;
  z-index: 2000;
  display: flex;
  align-items: flex-end;
  background: rgba(15, 23, 42, 0.35);
}
.detail__reviews-sheet {
  display: flex;
  flex-direction: column;
  width: 100%;
  max-height: 70vh;
  padding: 18px 16px calc(20px + env(safe-area-inset-bottom));
  border-radius: 20px 20px 0 0;
  background: #fff;
}
.detail__reviews-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  color: #1e293b;
  font-size: 15px;
  font-weight: 650;
}
.detail__reviews-close {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
}
.detail__reviews-list {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
}
</style>
