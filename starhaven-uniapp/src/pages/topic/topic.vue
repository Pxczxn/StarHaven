<script lang="ts" setup>
import type { HouseCard } from '@/api/types/stay'
import { fetchHousePage } from '@/api/stay'

interface TopicCategory {
  title: string
  desc: string
  icon: string
  tone: string
  query: Record<string, any>
}

// 与首页分类宫格一一对应（key 由首页传入）
const TOPICS: Record<string, TopicCategory> = {
  minsu: { title: '民宿', desc: '精选民宿专场，安放每一段旅程', icon: 'i-tabler-building-cottage', tone: 'blue', query: {} },
  whole: { title: '整套', desc: '整租独享，把整个家和朋友一起包下来', icon: 'i-tabler-key', tone: 'rose', query: { houseType: 'WHOLE' } },
  room: { title: '单间', desc: '独立房间，轻装出行更自在', icon: 'i-tabler-building-skyscraper', tone: 'green', query: { houseType: 'ROOM' } },
  hotel: { title: '套房', desc: '套房规格，把度假住出酒店感', icon: 'i-tabler-building-estate', tone: 'purple', query: { houseType: 'HOTEL' } },
  lake: { title: '湖景', desc: '推窗见湖，把风景留在房间里', icon: 'i-tabler-ripple', tone: 'amber', query: { keyword: '湖景' } },
}

const category = ref<TopicCategory>(TOPICS.minsu)
const list = ref<HouseCard[]>([])
const page = ref(1)
const total = ref(0)
const loading = ref(false)
const hasMore = computed(() => list.value.length < total.value)

async function load(reset = false) {
  if (loading.value || (!reset && !hasMore.value)) {
    return
  }
  loading.value = true
  try {
    if (reset) {
      page.value = 1
    }
    const res = await fetchHousePage({ ...category.value.query, page: page.value, size: 10 })
    total.value = res.total || 0
    const items = res.list || []
    list.value = reset ? items : [...list.value, ...items]
    page.value += 1
  }
  finally {
    loading.value = false
  }
}

onLoad((options) => {
  category.value = TOPICS[options?.key || ''] || TOPICS.minsu
  uni.setNavigationBarTitle({ title: `${category.value.title}专项` })
  load(true)
})

onReachBottom(() => load())
</script>

<template>
  <view class="min-h-screen bg-page pb-10">
    <view class="flex items-center bg-white px-4 py-4">
      <view class="topic-icon" :class="`topic-icon--${category.tone}`">
        <view :class="category.icon" />
      </view>
      <view class="ml-3">
        <view class="text-lg text-ink font-semibold">
          {{ category.title }}专项
        </view>
        <view class="mt-0.5 text-xs text-muted">
          {{ category.desc }}
        </view>
      </view>
    </view>

    <view v-if="total" class="px-4 pb-2 pt-3 text-xs text-faint">
      共 {{ total }} 个房源
    </view>

    <view class="px-4">
      <fg-house-card v-for="item in list" :key="item.id" :item="item" />

      <view v-if="loading" class="center py-8 text-sm text-faint">
        加载中...
      </view>
      <view v-else-if="list.length && !hasMore" class="center py-8 text-xs text-faint">
        没有更多了
      </view>

      <fg-empty
        v-if="!loading && !list.length"
        text="这个专项暂时没有房源"
        action="none"
      />
    </view>
  </view>
</template>

<style>
.topic-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 15px;
  font-size: 22px;
}
.topic-icon--blue {
  color: #2563eb;
  background: #eaf2ff;
}
.topic-icon--rose {
  color: #e11d48;
  background: #fff0f3;
}
.topic-icon--green {
  color: #059669;
  background: #e8faf3;
}
.topic-icon--purple {
  color: #7c3aed;
  background: #f1eaff;
}
.topic-icon--amber {
  color: #d97706;
  background: #fff7df;
}
</style>
