<script lang="ts" setup>
import type { HouseCard } from '@/api/types/stay'
import { fetchHousePage, searchHouses } from '@/api/stay'

definePage({
  style: {
    navigationBarTitleText: '房源列表',
    enablePullDownRefresh: true,
  },
})

const query = reactive({
  page: 1,
  size: 10,
  city: '',
  keyword: '',
  sort: 'RECOMMEND',
  minPrice: undefined as number | undefined,
  maxPrice: undefined as number | undefined,
  facility: '',
  houseType: '',
  guestCount: undefined as number | undefined,
})
const list = ref<HouseCard[]>([])
const total = ref(0)
const loading = ref(false)

async function load(reset = false) {
  if (reset)
    query.page = 1
  loading.value = true
  try {
    const fetcher = query.keyword.trim() ? searchHouses : fetchHousePage
    const res = await fetcher(query)
    total.value = res.total
    list.value = reset ? res.list : [...list.value, ...res.list]
  }
  finally {
    loading.value = false
  }
}

function goFilter() {
  uni.navigateTo({ url: '/pages/filter/filter' })
}

function goBack() {
  uni.navigateBack()
}

function goSearch() {
  uni.navigateTo({ url: '/pages/search/search' })
}

onLoad((options) => {
  query.city = options?.city || ''
  query.keyword = options?.keyword || ''
  query.minPrice = options?.minPrice ? Number(options.minPrice) : undefined
  query.maxPrice = options?.maxPrice ? Number(options.maxPrice) : undefined
  query.facility = options?.facility || ''
  query.houseType = options?.houseType || ''
  load(true)
})

onPullDownRefresh(async () => {
  await load(true)
  uni.stopPullDownRefresh()
})
onReachBottom(() => {
  if (list.value.length < total.value) {
    query.page += 1
    load()
  }
})
</script>

<template>
  <view class="listing">
    <view class="listing__head">
      <view class="listing__back" @click="goBack">
        <view class="i-carbon-arrow-left" />
      </view>
      <view class="listing__title">
        {{ query.city || '精选房源' }} <text class="listing__count">{{ total ? `${total} 间` : '' }}</text>
      </view>
      <view class="listing__search" @click="goSearch">
        <view class="i-carbon-search" />
      </view>
    </view>
    <view class="listing__tools">
      <view class="listing__tool" :class="{ 'listing__tool--on': query.sort === 'RECOMMEND' }" @click="query.sort = 'RECOMMEND'; load(true)">
        推荐
      </view>
      <view class="listing__tool" :class="{ 'listing__tool--on': query.sort === 'PRICE_ASC' }" @click="query.sort = 'PRICE_ASC'; load(true)">
        价格最低
      </view>
      <view class="listing__tool" :class="{ 'listing__tool--on': query.sort === 'SCORE_DESC' }" @click="query.sort = 'SCORE_DESC'; load(true)">
        评分最高
      </view>
      <view class="listing__filter" @click="goFilter">
        <view class="i-carbon-filter" /> 筛选
      </view>
    </view>
    <view class="listing__body">
      <fg-house-card v-for="item in list" :key="item.id" :item="item" />
      <fg-empty v-if="!loading && !list.length" text="没有找到符合条件的房源" />
      <view v-if="loading" class="listing__loading">
        正在为你寻找合适的房间…
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
.listing {
  min-height: 100vh;
  padding-bottom: 32px;
  background: #f6fafc;
}
.listing__head {
  display: flex;
  align-items: center;
  height: 52px;
  padding: 0 16px;
  background: #fff;
  border-bottom: 1px solid #e8eef5;
}
.listing__back,
.listing__search {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  color: #334155;
  font-size: 18px;
}
.listing__title {
  flex: 1;
  color: #0f172a;
  font-size: 16px;
  font-weight: 700;
  text-align: center;
}
.listing__count {
  margin-left: 4px;
  color: #94a3b8;
  font-size: 11px;
  font-weight: 400;
}
.listing__tools {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: #fff;
  border-bottom: 1px solid #edf2f7;
}
.listing__tool {
  padding: 6px 10px;
  border-radius: 8px;
  color: #64748b;
  font-size: 11px;
}
.listing__tool--on {
  background: #eaf2ff;
  color: #2563c7;
  font-weight: 650;
}
.listing__filter {
  display: flex;
  align-items: center;
  gap: 3px;
  margin-left: auto;
  padding: 6px 0 6px 6px;
  color: #2563c7;
  font-size: 11px;
}
.listing__body {
  padding: 14px 16px;
}
.listing__loading {
  padding: 20px 0;
  color: #94a3b8;
  font-size: 12px;
  text-align: center;
}
</style>
