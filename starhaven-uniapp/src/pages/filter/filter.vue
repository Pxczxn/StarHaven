<script lang="ts" setup>
definePage({
  style: { navigationBarTitleText: '筛选' },
})

const minPrice = ref(200)
const maxPrice = ref(2000)
const houseType = ref('')
const facilities = ref<string[]>([])
const options = ['WiFi', '停车', '厨房', '泳池', '空调', '早餐']
const types = [
  { label: '整套房源', value: 'WHOLE' },
  { label: '独立房间', value: 'ROOM' },
  { label: '酒店房间', value: 'HOTEL' },
]

function toggle(name: string) {
  const index = facilities.value.indexOf(name)
  if (index >= 0)
    facilities.value.splice(index, 1)
  else
    facilities.value.push(name)
}

function confirm() {
  const query = [
    `minPrice=${minPrice.value}`,
    `maxPrice=${maxPrice.value}`,
    houseType.value ? `houseType=${houseType.value}` : '',
    facilities.value.length ? `facility=${facilities.value.join(',')}` : '',
  ].filter(Boolean).join('&')
  uni.redirectTo({ url: `/pages/listing/listing?${query}` })
}
</script>

<template>
  <view class="min-h-screen bg-page p-4">
    <view class="rounded-3 bg-white p-4">
      <view class="font-semibold">
        价格区间
      </view>
      <view class="mt-3 text-sm text-muted">
        ¥{{ minPrice }} - ¥{{ maxPrice }}
      </view>
      <slider :value="maxPrice" :min="200" :max="3000" active-color="#2878FF" @change="e => maxPrice = Number(e.detail.value)" />
    </view>
    <view class="mt-3 rounded-3 bg-white p-4">
      <view class="font-semibold">
        房型
      </view>
      <view class="mt-3 flex">
        <view
          v-for="item in types" :key="item.value"
          class="mr-2 rounded-full px-3 py-1 text-sm"
          :class="houseType === item.value ? 'bg-primary text-white' : 'bg-#F2F6FF'"
          @click="houseType = item.value"
        >
          {{ item.label }}
        </view>
      </view>
    </view>
    <view class="mt-3 rounded-3 bg-white p-4">
      <view class="font-semibold">
        设施
      </view>
      <view class="mt-3 flex flex-wrap">
        <view
          v-for="item in options" :key="item"
          class="mb-2 mr-2 rounded-full px-3 py-1 text-sm"
          :class="facilities.includes(item) ? 'bg-primary text-white' : 'bg-#F2F6FF'"
          @click="toggle(item)"
        >
          {{ item }}
        </view>
      </view>
    </view>
    <view class="mt-8 center h-11 rounded-full bg-primary text-white" @click="confirm">
      确认筛选
    </view>
  </view>
</template>
