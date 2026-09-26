<script lang="ts" setup>
import { CITY_LETTERS, CITY_PRESET, filterCities, HOT_CITIES } from '@/pages/index/homeStay'

defineProps<{
  current: string
}>()

const emit = defineEmits<{
  select: [name: string]
  close: []
}>()

const keyword = ref('')
const letter = ref('全部')
const list = computed(() => filterCities(CITY_PRESET, keyword.value, letter.value))

function pick(name: string) {
  emit('select', name)
}
</script>

<template>
  <view class="city">
    <view class="city__handle" />
    <view class="city__head">
      <view>
        <view class="city__kicker">
          下一站
        </view>
        <view class="city__title">
          去哪住
        </view>
      </view>
      <view class="city__close" hover-class="city__close--on" @click="emit('close')">
        关闭
      </view>
    </view>

    <view class="city__search">
      <view class="i-carbon-location city__pin" />
      <input v-model="keyword" class="city__input" placeholder="搜城市，例如杭州、京都" confirm-type="search">
    </view>

    <view class="city__label">
      热门目的地
    </view>
    <scroll-view scroll-x class="city__hots" :show-scrollbar="false">
      <view
        v-for="item in HOT_CITIES"
        :key="item"
        class="city__chip"
        :class="{ 'city__chip--on': current === item }"
        @click="pick(item)"
      >
        {{ item }}
      </view>
    </scroll-view>

    <scroll-view scroll-x class="city__letters" :show-scrollbar="false">
      <view
        v-for="item in CITY_LETTERS"
        :key="item"
        class="city__letter"
        :class="{ 'city__letter--on': letter === item }"
        @click="letter = item"
      >
        {{ item }}
      </view>
    </scroll-view>

    <view class="city__grid">
      <view
        v-for="item in list"
        :key="item.name"
        class="city__tile"
        :class="{ 'city__tile--on': current === item.name }"
        @click="pick(item.name)"
      >
        <text class="city__name">{{ item.name }}</text>
        <text class="city__initial">{{ item.initial }}</text>
      </view>
      <view v-if="!list.length" class="city__empty">
        没有找到这座城，换个字再搜
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
.city {
  padding-bottom: 8px;
}
.city__handle {
  width: 36px;
  height: 4px;
  margin: 0 auto 14px;
  border-radius: 999px;
  background: #dbe4ee;
}
.city__head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 16px;
}
.city__kicker {
  color: #3882f6;
  font-size: 11px;
  letter-spacing: 0.16em;
}
.city__title {
  margin-top: 4px;
  color: #0f172a;
  font-size: 22px;
  font-weight: 700;
}
.city__close {
  height: 32px;
  padding: 0 12px;
  border-radius: 10px;
  background: #f1f5f9;
  color: #64748b;
  font-size: 13px;
  line-height: 32px;
}
.city__close--on {
  opacity: 0.75;
}
.city__search {
  display: flex;
  align-items: center;
  height: 46px;
  padding: 0 14px;
  border-radius: 16px;
  background: #f4f8fc;
}
.city__pin {
  margin-right: 8px;
  color: #3882f6;
  font-size: 18px;
}
.city__input {
  flex: 1;
  height: 46px;
  color: #0f172a;
  font-size: 14px;
}
.city__label {
  margin: 18px 0 10px;
  color: #64748b;
  font-size: 12px;
}
.city__hots {
  white-space: nowrap;
  margin: 0 -4px 4px;
}
.city__chip {
  display: inline-flex;
  align-items: center;
  height: 34px;
  padding: 0 14px;
  margin: 0 6px 0 0;
  border-radius: 999px;
  background: #eef4ff;
  color: #2563c7;
  font-size: 13px;
  font-weight: 600;
}
.city__chip--on {
  background: #3882f6;
  color: #fff;
}
.city__letters {
  margin: 14px 0 12px;
  white-space: nowrap;
}
.city__letter {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 30px;
  height: 28px;
  margin-right: 4px;
  border-radius: 8px;
  color: #64748b;
  font-size: 11px;
}
.city__letter--on {
  background: #0f172a;
  color: #fff;
  font-weight: 650;
}
.city__grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  max-height: 280px;
  overflow-y: auto;
}
.city__tile {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: calc((100% - 20px) / 3);
  min-height: 64px;
  padding: 10px 10px 8px;
  border: 1px solid #e8eef5;
  border-radius: 14px;
  background: #fff;
}
.city__tile--on {
  border-color: #3882f6;
  background: linear-gradient(180deg, #eef5ff 0%, #fff 70%);
}
.city__name {
  color: #0f172a;
  font-size: 14px;
  font-weight: 650;
}
.city__initial {
  color: #94a3b8;
  font-size: 10px;
  letter-spacing: 0.08em;
}
.city__empty {
  width: 100%;
  padding: 28px 0;
  color: #94a3b8;
  font-size: 13px;
  text-align: center;
}
</style>
