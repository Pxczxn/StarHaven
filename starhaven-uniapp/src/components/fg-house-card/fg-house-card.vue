<script lang="ts" setup>
import type { HouseCard } from '@/api/types/stay'

defineProps<{
  item: HouseCard
}>()

function openDetail(id: number) {
  uni.navigateTo({ url: `/pages/detail/detail?id=${id}` })
}
</script>

<template>
  <view class="card" @click="openDetail(item.id)">
    <view class="card__media">
      <image :src="item.coverImage" mode="aspectFill" class="card__img" />
    </view>
    <view class="card__body">
      <view class="card__title">
        {{ item.title }}
      </view>
      <view class="card__meta">
        {{ item.city }} · {{ item.address }}
      </view>
      <view class="card__row">
        <view class="card__score">
          ★ {{ item.avgScore || '新' }}
          <text class="card__reviews">
            · {{ item.commentCount || 0 }} 评价
          </text>
        </view>
        <view class="card__price">
          <text class="card__yen">¥{{ item.price }}</text>
          <text class="card__night">/晚</text>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
.card {
  margin-bottom: 14px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #edf2f7;
  border-radius: 16px;
  box-shadow: 0 6px 16px rgba(30, 41, 59, 0.045);
}

.card__media {
  position: relative;
}

.card__img {
  display: block;
  width: 100%;
  height: 156px;
}

.card__body {
  padding: 12px 14px 14px;
}

.card__title {
  font-size: 15px;
  font-weight: 650;
  color: #1e293b;
  line-height: 1.35;
}

.card__meta {
  margin-top: 4px;
  font-size: 12px;
  color: #64748b;
  line-height: 1.4;
}

.card__row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-top: 10px;
}

.card__score {
  font-size: 12px;
  color: #f59e0b;
}

.card__reviews {
  color: #999;
}

.card__yen {
  font-size: 18px;
  font-weight: 700;
  color: #2563c7;
}

.card__night {
  margin-left: 2px;
  font-size: 11px;
  color: #999;
}
</style>
