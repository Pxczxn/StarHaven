<script lang="ts" setup>
import type { HouseDetail } from '@/api/types/stay'
import { fetchHouseDetail } from '@/api/stay'
import dayjs from 'dayjs'

definePage({
  style: { navigationBarTitleText: '预约入住' },
})

const houseId = ref(0)
const detail = ref<HouseDetail>()
const checkIn = ref(dayjs().add(2, 'day').format('YYYY-MM-DD'))
const checkOut = ref(dayjs().add(4, 'day').format('YYYY-MM-DD'))
const guestCount = ref(2)
const roomCount = ref(1)
const loading = ref(true)

const nights = computed(() => Math.max(1, dayjs(checkOut.value).diff(dayjs(checkIn.value), 'day')))
const housePrice = computed(() => Number(detail.value?.price || 0) * nights.value * roomCount.value)
const serviceFee = computed(() => Number((housePrice.value * 0.06).toFixed(2)))
const total = computed(() => Number((housePrice.value + serviceFee.value).toFixed(2)))

onLoad(async (options) => {
  houseId.value = Number(options?.id || 0)
  try {
    detail.value = await fetchHouseDetail(houseId.value)
  }
  finally {
    loading.value = false
  }
})

function confirm() {
  const query = [
    `id=${houseId.value}`,
    `checkIn=${checkIn.value}`,
    `checkOut=${checkOut.value}`,
    `guestCount=${guestCount.value}`,
    `roomCount=${roomCount.value}`,
  ].join('&')
  uni.navigateTo({ url: `/pages/order-confirm/order-confirm?${query}` })
}
</script>

<template>
  <view class="booking">
    <view v-if="loading" class="booking__loading">
      正在加载房源信息…
    </view>
    <fg-empty v-else-if="!detail" text="房源信息暂时无法获取" />
    <template v-else>
      <view class="booking__intro">
        <view class="booking__eyebrow">
          预约入住
        </view>
        <view class="booking__title">
          {{ detail.title }}
        </view>
        <view class="booking__location">
          <view class="i-carbon-location" /> {{ detail.city }} · {{ detail.address }}
        </view>
      </view>
      <view class="booking__card">
        <view class="booking__section-title">
          入住时间
        </view>
        <view class="booking__dates">
          <view><text>入住</text><text class="booking__date-value">{{ checkIn.slice(5) }}</text></view>
          <view class="booking__arrow">
            <view class="i-carbon-arrow-right" /><text>{{ nights }} 晚</text>
          </view>
          <view><text>离店</text><text class="booking__date-value">{{ checkOut.slice(5) }}</text></view>
        </view>
        <view class="booking__line">
          <text>入住人数</text>
          <view class="booking__stepper">
            <view @click="guestCount = Math.max(1, guestCount - 1)">
              −
            </view><text>{{ guestCount }}</text><view @click="guestCount += 1">
              ＋
            </view>
          </view>
        </view>
        <view class="booking__line">
          <text>房间数量</text>
          <view class="booking__stepper">
            <view @click="roomCount = Math.max(1, roomCount - 1)">
              −
            </view><text>{{ roomCount }}</text><view @click="roomCount += 1">
              ＋
            </view>
          </view>
        </view>
      </view>
      <view class="booking__card booking__price-card">
        <view class="booking__section-title">
          费用明细
        </view>
        <view class="booking__price-line">
          <text>房费</text><text>¥{{ housePrice }}</text>
        </view>
        <view class="booking__price-line">
          <text>服务费</text><text>¥{{ serviceFee }}</text>
        </view>
        <view class="booking__price-total">
          <text>总价</text><text class="booking__total-value">¥{{ total }}</text>
        </view>
      </view>
      <view class="booking__bar">
        <view><text>合计</text><text class="booking__bar-total">¥{{ total }}</text></view>
        <view class="booking__confirm" @click="confirm">
          确认订单 <view class="i-carbon-arrow-right" />
        </view>
      </view>
    </template>
  </view>
</template>

<style scoped lang="scss">
.booking {
  min-height: 100vh;
  padding: 16px 16px 94px;
  background: #f6fafc;
  color: #1e293b;
}
.booking__loading {
  padding-top: 36vh;
  color: #94a3b8;
  font-size: 13px;
  text-align: center;
}
.booking__intro {
  padding: 10px 4px 18px;
}
.booking__eyebrow {
  color: #3882f6;
  font-size: 11px;
  letter-spacing: 0.12em;
}
.booking__title {
  margin-top: 6px;
  color: #0f172a;
  font-size: 21px;
  font-weight: 700;
  line-height: 1.3;
}
.booking__location {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 8px;
  color: #64748b;
  font-size: 12px;
}
.booking__card {
  margin-bottom: 12px;
  padding: 16px;
  border: 1px solid #e8eef5;
  border-radius: 16px;
  background: #fff;
}
.booking__section-title {
  color: #0f172a;
  font-size: 14px;
  font-weight: 650;
}
.booking__dates {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 18px 0;
}
.booking__dates view:not(.booking__arrow) {
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.booking__dates text {
  color: #94a3b8;
  font-size: 11px;
}
.booking__date-value {
  display: block;
  margin-top: 5px;
  color: #1e293b;
  font-size: 21px;
  font-weight: 700;
}
.booking__arrow {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
  color: #3882f6;
  font-size: 11px;
}
.booking__arrow text {
  color: #3882f6;
}
.booking__line,
.booking__price-line,
.booking__price-total {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 15px;
  font-size: 13px;
}
.booking__stepper {
  display: flex;
  align-items: center;
  gap: 14px;
}
.booking__stepper view {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 25px;
  height: 25px;
  border-radius: 8px;
  background: #eef4ff;
  color: #2563c7;
  font-size: 18px;
}
.booking__stepper text {
  min-width: 12px;
  color: #1e293b;
  text-align: center;
}
.booking__price-card {
  color: #64748b;
}
.booking__price-total {
  margin-top: 20px;
  padding-top: 14px;
  border-top: 1px solid #edf2f7;
  color: #1e293b;
}
.booking__total-value {
  color: #2563c7;
  font-size: 20px;
  font-weight: 700;
}
.booking__bar {
  position: fixed;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px calc(12px + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.96);
  border-top: 1px solid #e8eef5;
  backdrop-filter: blur(10px);
}
.booking__bar text {
  display: block;
  color: #94a3b8;
  font-size: 10px;
}
.booking__bar-total {
  display: block;
  margin-top: 2px;
  color: #2563c7;
  font-size: 20px;
  font-weight: 700;
}
.booking__confirm {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 12px 20px;
  border-radius: 12px;
  background: #3882f6;
  color: #fff;
  font-size: 13px;
  font-weight: 650;
  box-shadow: 0 6px 14px rgba(56, 130, 246, 0.2);
}
</style>
