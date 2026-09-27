<script lang="ts" setup>
import type { OrderItem } from '@/api/types/stay'
import { fetchMyOrders } from '@/api/stay'
import { useTokenStore } from '@/store/token'

definePage({ style: { navigationBarTitleText: '我的订单' } })

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
const statusText: Record<string, string> = { WAIT_PAY: '待付款', PAID: '待入住', CHECK_IN: '入住中', FINISHED: '已完成', CANCEL: '已取消' }
const emptyText = computed(() => tabs.find(item => item.value === current.value)?.label || '全部')

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
  finally { loading.value = false }
}

function selectTab(value: string) {
  if (current.value === value)
    return
  current.value = value
  load()
}
function openDetail(id: number) {
  uni.navigateTo({ url: `/pages/order-detail/order-detail?id=${id}` })
}
function goLogin() {
  uni.navigateTo({ url: '/pages/login/index' })
}
function goHome() {
  uni.switchTab({ url: '/pages/index/index' })
}
function statusClass(status: string) {
  return `order__status--${status.toLowerCase()}`
}
onShow(load)
</script>

<template>
  <view class="orders">
    <view class="orders__heading">
      <view class="orders__title">
        我的行程
      </view>
      <view class="orders__subtitle">
        从预订到入住，每一步都清晰可见
      </view>
    </view>

    <scroll-view class="tabs" scroll-x :show-scrollbar="false">
      <view class="tabs__inner">
        <view v-for="tab in tabs" :key="tab.value" class="tabs__item" :class="{ 'tabs__item--active': current === tab.value }" @click="selectTab(tab.value)">
          {{ tab.label }}
        </view>
      </view>
    </scroll-view>

    <view class="orders__content">
      <template v-if="loading">
        <view v-for="index in 2" :key="index" class="order order--loading">
          <view class="order__loading-head" />
          <view class="order__loading-body">
            <view class="order__loading-image" /><view class="order__loading-lines" />
          </view>
        </view>
      </template>

      <view v-for="item in list" v-else :key="item.id" class="order" @click="openDetail(item.id)">
        <view class="order__top">
          <view class="order__number">
            <view class="i-tabler-receipt" />订单 {{ item.orderNo }}
          </view>
          <view class="order__status" :class="statusClass(item.orderStatus)">
            {{ statusText[item.orderStatus] || item.orderStatus }}
          </view>
        </view>
        <view class="order__main">
          <image :src="item.houseCover" class="order__cover" mode="aspectFill" />
          <view class="order__info">
            <view class="order__name">
              {{ item.houseTitle }}
            </view>
            <view class="order__date">
              <view class="i-tabler-calendar-event" />{{ item.checkInDate }} — {{ item.checkOutDate }}
            </view>
            <view class="order__meta">
              <text><view class="i-tabler-users" />{{ item.guestCount }} 位住客</text>
              <text><view class="i-tabler-door" />{{ item.roomCount }} 间</text>
            </view>
          </view>
        </view>
        <view class="order__bottom">
          <view class="order__amount">
            <text>订单金额</text><strong>¥{{ item.totalAmount }}</strong>
          </view>
          <view class="order__detail">
            查看详情 <view class="i-tabler-chevron-right" />
          </view>
        </view>
      </view>

      <view v-if="!loading && !list.length" class="empty">
        <view class="empty__icon">
          <view :class="tokenStore.hasLogin ? 'i-tabler-calendar-smile' : 'i-tabler-user-circle'" />
        </view>
        <view class="empty__title">
          {{ tokenStore.hasLogin ? `${emptyText}订单还是空的` : '登录后查看全部行程' }}
        </view>
        <view class="empty__desc">
          {{ tokenStore.hasLogin ? '选一间喜欢的住处，开始下一段旅程吧' : '订单状态、入住日期和费用明细都在这里' }}
        </view>
        <button class="empty__button" @click="tokenStore.hasLogin ? goHome() : goLogin()">
          {{ tokenStore.hasLogin ? '去预订民宿' : '立即登录' }}
        </button>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
.orders {
  min-height: 100vh;
  padding-bottom: 100px;
  background: #f6f9fc;
}
.orders__heading {
  padding: 20px 20px 14px;
}
.orders__title {
  color: #17243b;
  font-size: 22px;
  font-weight: 700;
}
.orders__subtitle {
  margin-top: 4px;
  color: #8a99ae;
  font-size: 12px;
}
.tabs {
  position: relative;
  z-index: 1;
  width: 100%;
  background: #fff;
  border-bottom: 1px solid #eef2f7;
  white-space: nowrap;
}
.tabs__inner {
  display: flex;
  padding: 10px 12px 12px;
}
.tabs__item {
  flex: 1;
  min-width: 70px;
  padding: 10px 5px;
  color: #8290a3;
  font-size: 13px;
  text-align: center;
  border-radius: 12px;
}
.tabs__item--active {
  color: #196ce0;
  font-weight: 700;
  background: #eaf3ff;
}
.orders__content {
  padding: 16px;
}
.order {
  margin-bottom: 14px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #eaf0f6;
  border-radius: 18px;
  box-shadow: 0 8px 24px rgba(31, 51, 78, 0.055);
}
.order__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 13px 14px;
  border-bottom: 1px solid #f0f3f7;
}
.order__number {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #8592a5;
  font-size: 11px;
}
.order__status {
  padding: 4px 8px;
  color: #66758a;
  font-size: 11px;
  font-weight: 650;
  background: #f1f4f7;
  border-radius: 8px;
}
.order__status--wait_pay {
  color: #d86a20;
  background: #fff2e7;
}
.order__status--paid,
.order__status--check_in {
  color: #1769df;
  background: #eaf3ff;
}
.order__status--finished {
  color: #17835b;
  background: #e9f8f2;
}
.order__main {
  display: flex;
  padding: 14px;
}
.order__cover {
  flex: none;
  width: 100px;
  height: 88px;
  background: #edf2f7;
  border-radius: 13px;
}
.order__info {
  min-width: 0;
  padding-left: 12px;
}
.order__name {
  overflow: hidden;
  color: #17243b;
  font-size: 15px;
  font-weight: 700;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.order__date {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-top: 10px;
  color: #53647a;
  font-size: 11px;
}
.order__meta {
  display: flex;
  gap: 13px;
  margin-top: 9px;
  color: #8a99ae;
  font-size: 11px;
}
.order__meta text {
  display: flex;
  align-items: center;
  gap: 4px;
}
.order__bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  background: #fbfcfe;
  border-top: 1px solid #f0f3f7;
}
.order__amount {
  display: flex;
  align-items: baseline;
  gap: 7px;
  color: #98a4b4;
  font-size: 11px;
}
.order__amount strong {
  color: #17243b;
  font-size: 17px;
}
.order__detail {
  display: flex;
  align-items: center;
  color: #2676e5;
  font-size: 12px;
}
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 58px 24px 0;
  text-align: center;
}
.empty__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 84px;
  height: 84px;
  color: #2878df;
  font-size: 39px;
  background: #eaf3ff;
  border-radius: 30px;
  transform: rotate(-3deg);
}
.empty__icon > view {
  transform: rotate(3deg);
}
.empty__title {
  margin-top: 21px;
  color: #17243b;
  font-size: 18px;
  font-weight: 700;
}
.empty__desc {
  max-width: 270px;
  margin-top: 8px;
  color: #8a99ae;
  font-size: 13px;
  line-height: 1.7;
}
.empty__button {
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
.order--loading {
  padding: 14px;
}
.order__loading-head {
  width: 38%;
  height: 10px;
  background: #edf2f7;
  border-radius: 8px;
}
.order__loading-body {
  display: flex;
  margin-top: 14px;
}
.order__loading-image {
  width: 100px;
  height: 88px;
  background: #e9eef5;
  border-radius: 13px;
}
.order__loading-lines {
  width: 55%;
  height: 56px;
  margin: 4px 0 0 12px;
  background: linear-gradient(
    #edf2f7 0 12px,
    transparent 12px 25px,
    #f2f5f8 25px 35px,
    transparent 35px 46px,
    #f2f5f8 46px 56px
  );
  border-radius: 6px;
}
</style>
