<script lang="ts" setup>
import type { BannerItem, HouseCard } from '@/api/types/stay'
import { fetchBanners, fetchRecommendHouses } from '@/api/stay'
import { formatDateTrigger, overlayTitle } from './homeStay'
import { ensureLogin } from '@/utils/toLoginPage'
import dayjs from 'dayjs'

defineOptions({ name: 'Home' })
definePage({
  type: 'home',
  style: {
    navigationStyle: 'custom',
    navigationBarTitleText: '首页',
    navigationBarTextStyle: 'white',
  },
})

const loading = ref(true)
const errorText = ref('')
const banners = ref<BannerItem[]>([])
const houses = ref<HouseCard[]>([])
const statusBarPx = ref(20)
const navRightPx = ref(16)
const navRowPx = ref(32)
const overlay = ref<'date' | 'filter' | ''>('')
const staySnapshot = {
  start: '',
  end: '',
  nights: 4,
}
const selectedDateRange = ref(4)
const filterSelection = reactive({ houseType: '', facility: '', sort: '' })
const calendarStart = ref(dayjs().add(2, 'day').format('YYYY-MM-DD'))
const calendarEnd = ref(dayjs().add(6, 'day').format('YYYY-MM-DD'))
const dateTriggerText = computed(() => formatDateTrigger(calendarStart.value, calendarEnd.value))
const panelTitle = computed(() => overlay.value ? overlayTitle(overlay.value) : '')

const categories = [
  { key: 'minsu', label: '民宿', icon: 'i-tabler-building-cottage', tone: 'blue' },
  { key: 'whole', label: '整套', icon: 'i-tabler-key', tone: 'rose' },
  { key: 'room', label: '单间', icon: 'i-tabler-building-skyscraper', tone: 'green' },
  { key: 'hotel', label: '套房', icon: 'i-tabler-building-estate', tone: 'purple' },
  { key: 'lake', label: '湖景', icon: 'i-tabler-ripple', tone: 'amber' },
]

onLoad(() => {
  const sys = uni.getSystemInfoSync()
  statusBarPx.value = sys.statusBarHeight || 20
  // #ifdef MP-WEIXIN
  try {
    const menu = uni.getMenuButtonBoundingClientRect()
    if (menu?.width) {
      statusBarPx.value = menu.top
      navRowPx.value = menu.height
      navRightPx.value = sys.windowWidth - menu.left + 8
    }
  }
  catch {
    // 非微信环境沿用系统状态栏高度
  }
  // #endif
})

async function load() {
  loading.value = true
  errorText.value = ''
  try {
    const [bannerRes, houseRes] = await Promise.allSettled([fetchBanners(), fetchRecommendHouses()])
    banners.value = bannerRes.status === 'fulfilled' ? (bannerRes.value || []) : []
    houses.value = houseRes.status === 'fulfilled' ? (houseRes.value || []) : []
    if (bannerRes.status === 'rejected' && houseRes.status === 'rejected') {
      errorText.value = '网络错误，换个网络试试'
    }
  }
  finally {
    loading.value = false
  }
}

function goSearch() {
  uni.navigateTo({ url: '/pages/search/search' })
}

function goMessages() {
  if (!ensureLogin()) {
    return
  }
  uni.navigateTo({ url: '/pages/messages/messages' })
}

function goListing(extra = '') {
  uni.navigateTo({ url: `/pages/listing/listing${extra ? `?${extra}` : ''}` })
}

function goTopic(key: string) {
  uni.navigateTo({ url: `/pages/topic/topic?key=${key}` })
}

function openOverlay(kind: 'date' | 'filter') {
  if (kind === 'date') {
    staySnapshot.start = calendarStart.value
    staySnapshot.end = calendarEnd.value
    staySnapshot.nights = selectedDateRange.value
  }
  overlay.value = kind
}

function dismissOverlay() {
  if (overlay.value === 'date') {
    calendarStart.value = staySnapshot.start
    calendarEnd.value = staySnapshot.end
    selectedDateRange.value = staySnapshot.nights
  }
  overlay.value = ''
}

function onStayConfirm(payload: { start: string, end: string, nights: number }) {
  calendarStart.value = payload.start
  calendarEnd.value = payload.end
  selectedDateRange.value = payload.nights
  overlay.value = ''
}

function applyFilter() {
  const query = [filterSelection.houseType && `houseType=${filterSelection.houseType}`, filterSelection.facility && `facility=${filterSelection.facility}`, filterSelection.sort && `sort=${filterSelection.sort}`].filter(Boolean).join('&')
  overlay.value = ''
  goListing(query)
}

function openFilter() {
  openOverlay('filter')
}

onShow(() => {
  load()
})
</script>

<template>
  <view class="home">
    <view class="home__sky" :style="{ paddingTop: `${statusBarPx}px`, paddingRight: `${navRightPx}px` }">
      <view class="home__nav">
        <view class="home__place">
          <view class="home__brand">
            <image src="/static/logo.svg" class="home__logo" mode="aspectFit" /> <text>星栖</text>
          </view>
        </view>
        <view class="home__tools home__tools--booking">
          <view class="home__selector home__selector--date" hover-class="home__hit--active" @click="openOverlay('date')">
            <view class="i-carbon-calendar home__selector-icon" />
            <text class="home__date-text">{{ dateTriggerText }}</text>
            <view class="i-carbon-chevron-down home__chev" />
          </view>
        </view>
      </view>

      <view class="home__search-row">
        <view class="home__search" @click="goSearch">
          <view class="i-carbon-search home__search-ico" />
          <text>搜索目的地 / 民宿 / 关键词</text>
        </view>
        <view class="home__bell" @click="goMessages">
          <view class="i-carbon-notification" />
        </view>
      </view>
    </view>

    <view class="home__sheet">
      <view v-if="!loading && banners.length" class="home__hero" @click="goListing()">
        <image :src="banners[0].imageUrl" mode="aspectFill" class="home__hero-img" />
        <view class="home__hero-overlay">
          <view class="home__hero-tag">
            星栖精选 · 秋季旅居
          </view>
          <view class="home__hero-title">
            {{ banners[0].title }}
          </view>
          <view class="home__hero-sub">
            {{ banners[0].subtitle || '寻觅山海间的静谧空间' }}
          </view>
          <view class="home__hero-cta">
            探索此行 <view class="i-carbon-arrow-right" />
          </view>
        </view>
      </view>

      <view class="home__categories">
        <view v-for="category in categories" :key="category.label" class="home__category" @click="goTopic(category.key)">
          <view class="home__category-icon" :class="`home__category-icon--${category.tone}`">
            <view :class="category.icon" />
          </view>
          <text>{{ category.label }}</text>
        </view>
      </view>

      <view v-if="loading" class="home__hint">
        正在找今晚的房间…
      </view>

      <view class="home__section">
        <view class="home__section-h">
          <view>
            <view class="home__kicker">
              星栖精选
            </view>
            <view class="home__h">
              推荐房源
            </view>
          </view>
          <view class="home__more" @click="openFilter">
            全部筛选 <view class="i-carbon-filter" />
          </view>
        </view>

        <fg-house-card v-for="item in houses" :key="item.id" :item="item" />
        <fg-empty
          v-if="!loading && !houses.length"
          :text="errorText || '暂无推荐房源'"
          :action="errorText ? 'retry' : 'none'"
          @retry="load"
        />
      </view>
    </view>

    <view v-if="overlay" class="home__overlay" @click="dismissOverlay">
      <view class="home__sheet-panel" @click.stop>
        <fg-stay-dates
          v-if="overlay === 'date'"
          :start="calendarStart"
          :end="calendarEnd"
          @update:start="calendarStart = $event"
          @update:end="calendarEnd = $event"
          @confirm="onStayConfirm"
          @close="dismissOverlay"
        />
        <template v-else>
          <view class="home__panel-head">
            <text>{{ panelTitle }}</text>
            <view class="i-carbon-close home__panel-close" @click="dismissOverlay" />
          </view>
          <view class="home__filter-summary">
            按你的偏好，快速找到更合适的房间
          </view>
          <view class="home__filter-group">
            <text>房型</text><view class="home__filter-row">
              <view v-for="item in [{ label: '全部房型', value: '' }, { label: '整套房', value: 'WHOLE' }, { label: '独立房间', value: 'ROOM' }]" :key="item.value || 'all'" class="home__option" :class="{ 'home__option--on': filterSelection.houseType === item.value }" @click="filterSelection.houseType = item.value">
                {{ item.label }}
              </view>
            </view>
          </view>
          <view class="home__filter-group">
            <text>排序</text><view class="home__filter-row">
              <view v-for="item in [{ label: '综合推荐', value: '' }, { label: '高分好评', value: 'SCORE_DESC' }, { label: '价格优先', value: 'PRICE_ASC' }]" :key="item.value || 'recommend'" class="home__option" :class="{ 'home__option--on': filterSelection.sort === item.value }" @click="filterSelection.sort = item.value">
                {{ item.label }}
              </view>
            </view>
          </view>
          <view class="home__filter-group">
            <text>设施偏好</text><view class="home__filter-row">
              <view v-for="item in [{ label: '可做饭', value: '厨房' }, { label: '停车', value: '停车' }, { label: 'WiFi', value: 'WiFi' }]" :key="item.value" class="home__option" :class="{ 'home__option--on': filterSelection.facility === item.value }" @click="filterSelection.facility = filterSelection.facility === item.value ? '' : item.value">
                {{ item.label }}
              </view>
            </view>
          </view>
          <view class="home__apply" @click="applyFilter">
            查看符合条件的房源 <view class="i-carbon-arrow-right" />
          </view>
        </template>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
.home {
  min-height: 100vh;
  padding-bottom: calc(50px + env(safe-area-inset-bottom) + 20px);
  background: #f6fafc;
  color: #1e293b;
}
.home__sky {
  padding: 0 16px 14px;
  background: #fff;
  border-bottom: 1px solid #e8eef5;
}
.home__nav {
  min-height: 66px;
  padding-top: 8px;
}
.home__place {
  display: flex;
  align-items: center;
  min-width: 0;
}
.home__brand {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #1e293b;
  font-size: 16px;
  font-weight: 700;
}
.home__logo {
  width: 22px;
  height: 22px;
}
.home__tools {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  min-height: 34px;
}
.home__tools--booking {
  justify-content: flex-end;
}
.home__selector {
  display: flex;
  align-items: center;
  gap: 5px;
  height: 32px;
  padding: 0 10px;
  border: 1px solid #e2eaf5;
  border-radius: 10px;
  background: #f8fbff;
  color: #1e293b;
  font-size: 12px;
  font-weight: 650;
}
.home__selector--city {
  flex: 0 0 auto;
}
.home__selector--date {
  flex: 0 0 auto;
  min-width: 0;
  max-width: 100%;
  color: #64748b;
  font-weight: 500;
  justify-content: flex-end;
}
.home__selector-icon {
  color: #3882f6;
  font-size: 15px;
}
.home__date-text {
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: right;
  white-space: nowrap;
}
.home__chev {
  margin-left: 2px;
  color: #94a3b8;
  font-size: 12px;
}
.home__hit--active {
  opacity: 0.7;
}
.home__search-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 12px;
}
.home__search {
  flex: 1;
  display: flex;
  align-items: center;
  height: 44px;
  padding: 0 14px;
  border-radius: 14px;
  background: #f1f5f9;
  color: #94a3b8;
  font-size: 13px;
}
.home__search-ico {
  margin-right: 8px;
  color: #3882f6;
  font-size: 17px;
}
.home__bell {
  width: 42px;
  height: 42px;
  border-radius: 13px;
  background: #eef4ff;
  color: #3882f6;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.home__sheet {
  padding: 14px 16px 8px;
}
.home__hero {
  position: relative;
  height: 150px;
  margin-bottom: 16px;
  overflow: hidden;
  border-radius: 18px;
  background: #3882f6;
  box-shadow: 0 8px 18px rgba(56, 130, 246, 0.14);
}
.home__hero-img {
  width: 100%;
  height: 100%;
  opacity: 0.72;
}
.home__hero-overlay {
  position: absolute;
  inset: 0;
  padding: 18px;
  color: #fff;
  background: linear-gradient(90deg, rgba(15, 57, 122, 0.82), rgba(56, 130, 246, 0.1));
}
.home__hero-tag {
  font-size: 10px;
  opacity: 0.82;
  letter-spacing: 0.08em;
}
.home__hero-title {
  margin-top: 9px;
  max-width: 74%;
  font-size: 19px;
  font-weight: 700;
  line-height: 1.3;
}
.home__hero-sub {
  margin-top: 4px;
  font-size: 11px;
  opacity: 0.85;
}
.home__hero-cta {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 13px;
  width: fit-content;
  padding: 6px 10px;
  border-radius: 9px;
  background: #fff;
  color: #2563c7;
  font-size: 11px;
  font-weight: 650;
}
.home__hero--fallback {
  background: transparent;
  box-shadow: none;
}
.home__hero-card {
  position: relative;
  height: 150px;
  overflow: hidden;
  border-radius: 18px;
}
.home__hero-mask {
  position: absolute;
  inset: auto 0 0;
  padding: 28px 14px 12px;
  background: linear-gradient(to top, rgba(16, 24, 40, 0.72), transparent);
  color: #fff;
}
.home__categories {
  display: flex;
  justify-content: space-between;
  margin: 2px -2px 18px;
}
.home__category {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  min-width: 48px;
  color: #475569;
  font-size: 11px;
}
.home__category-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 14px;
  font-size: 20px;
}
.home__category-symbol {
  font-size: 22px;
  font-weight: 700;
  line-height: 1;
}
.home__category-icon--blue {
  color: #2563eb;
  background: #eaf2ff;
}
.home__category-icon--green {
  color: #059669;
  background: #e8faf3;
}
.home__category-icon--amber {
  color: #d97706;
  background: #fff7df;
}
.home__category-icon--purple {
  color: #7c3aed;
  background: #f1eaff;
}
.home__category-icon--rose {
  color: #e11d48;
  background: #fff0f3;
}
.home__chips {
  white-space: nowrap;
  margin-bottom: 15px;
}
.home__chip {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 12px;
  margin-right: 7px;
  border: 1px solid #dce7f8;
  border-radius: 14px;
  background: #fff;
  color: #475569;
  font-size: 11px;
}
.home__hint {
  padding: 28px 0;
  text-align: center;
  color: #94a3b8;
  font-size: 13px;
}
.home__section-h {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 12px;
}
.home__kicker {
  font-size: 10px;
  letter-spacing: 0.14em;
  color: #3882f6;
}
.home__h {
  margin-top: 3px;
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
}
.home__more {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #64748b;
  font-size: 12px;
}
.home__overlay {
  position: fixed;
  inset: 0;
  z-index: 2000;
  display: flex;
  align-items: flex-end;
  background: rgba(15, 23, 42, 0.35);
}
.home__sheet-panel {
  width: 100%;
  max-height: calc(100vh - 16px);
  overflow-y: auto;
  padding: 18px 16px calc(20px + env(safe-area-inset-bottom));
  border-radius: 20px 20px 0 0;
  background: #fff;
}
.home__panel-close {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
}
.home__filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.home__option {
  min-width: 76px;
  padding: 10px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  color: #475569;
  font-size: 13px;
  text-align: center;
}
.home__option--on {
  border-color: #3882f6;
  background: #eaf2ff;
  color: #2563c7;
  font-weight: 650;
}
.home__filter-group {
  margin-bottom: 18px;
  color: #1e293b;
  font-size: 13px;
  font-weight: 650;
}
.home__filter-summary {
  margin-bottom: 18px;
  color: #94a3b8;
  font-size: 12px;
}
.home__filter-row {
  margin-top: 10px;
}
.home__apply {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  height: 44px;
  border-radius: 12px;
  background: #3882f6;
  color: #fff;
  font-size: 13px;
  font-weight: 650;
}
</style>
