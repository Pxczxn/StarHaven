<script lang="ts" setup>
import { canConfirmStay, stayNights } from '@/pages/index/homeStay'
import dayjs from 'dayjs'

const props = defineProps<{
  start: string
  end: string
}>()

const emit = defineEmits<{
  'update:start': [value: string]
  'update:end': [value: string]
  confirm: [payload: { start: string, end: string, nights: number }]
  close: []
}>()

const month = ref(dayjs(props.start || undefined).startOf('month'))
const nightPicks = [1, 3, 5, 7, 9]
const weekdays = ['日', '一', '二', '三', '四', '五', '六']

const nights = computed(() => stayNights(props.start, props.end))
const ready = computed(() => canConfirmStay(props.start, props.end))
const startLabel = computed(() => (props.start ? dayjs(props.start).format('M月D日') : '入住日'))
const endLabel = computed(() => (props.end ? dayjs(props.end).format('M月D日') : '离店日'))
const days = computed(() => {
  const first = month.value.startOf('month')
  const gridStart = first.subtract(first.day(), 'day')
  return Array.from({ length: 42 }, (_, index) => {
    const day = gridStart.add(index, 'day')
    const value = day.format('YYYY-MM-DD')
    return {
      key: value,
      date: day.date(),
      muted: day.month() !== month.value.month(),
      start: value === props.start,
      end: Boolean(props.end) && value === props.end,
      range: Boolean(props.start && props.end && day.isAfter(dayjs(props.start)) && day.isBefore(dayjs(props.end))),
    }
  })
})

function shift(delta: number) {
  month.value = month.value.add(delta, 'month')
}

function pickDay(value: string) {
  if (!props.start || props.end) {
    emit('update:start', value)
    emit('update:end', '')
    return
  }
  if (dayjs(value).isBefore(dayjs(props.start))) {
    emit('update:end', props.start)
    emit('update:start', value)
    return
  }
  emit('update:end', value)
}

function pickNights(count: number) {
  const start = dayjs().add(2, 'day').format('YYYY-MM-DD')
  const end = dayjs().add(count + 2, 'day').format('YYYY-MM-DD')
  emit('confirm', { start, end, nights: count })
}

function confirm() {
  if (!ready.value)
    return
  emit('confirm', { start: props.start, end: props.end, nights: nights.value })
}
</script>

<template>
  <view class="stay">
    <view class="stay__handle" />
    <view class="stay__head">
      <view>
        <view class="stay__kicker">
          入住行程
        </view>
        <view class="stay__title">
          住几晚
        </view>
      </view>
      <view class="stay__close" hover-class="stay__close--on" @click="emit('close')">
        关闭
      </view>
    </view>

    <view class="stay__ticket">
      <view class="stay__field">
        <text class="stay__field-k">入住</text>
        <text class="stay__field-v">{{ startLabel }}</text>
      </view>
      <view class="stay__nights">
        {{ nights || '—' }}晚
      </view>
      <view class="stay__field stay__field--end">
        <text class="stay__field-k">离店</text>
        <text class="stay__field-v">{{ endLabel }}</text>
      </view>
    </view>

    <scroll-view scroll-x class="stay__pills" :show-scrollbar="false">
      <view
        v-for="item in nightPicks"
        :key="item"
        class="stay__pill"
        :class="{ 'stay__pill--on': nights === item && ready }"
        @click="pickNights(item)"
      >
        {{ item }}晚
      </view>
    </scroll-view>

    <view class="stay__month">
      <view class="i-carbon-chevron-left stay__nav" @click="shift(-1)" />
      <text>{{ month.format('YYYY年M月') }}</text>
      <view class="i-carbon-chevron-right stay__nav" @click="shift(1)" />
    </view>

    <view class="stay__week">
      <text v-for="item in weekdays" :key="item">{{ item }}</text>
    </view>
    <view class="stay__grid">
      <view
        v-for="day in days"
        :key="day.key"
        class="stay__day"
        :class="{
          'stay__day--muted': day.muted,
          'stay__day--range': day.range,
          'stay__day--start': day.start,
          'stay__day--end': day.end,
        }"
        @click="pickDay(day.key)"
      >
        <text>{{ day.date }}</text>
        <text v-if="day.start" class="stay__mark">住</text>
        <text v-else-if="day.end" class="stay__mark">离</text>
      </view>
    </view>

    <view class="stay__cta" :class="{ 'stay__cta--off': !ready }" @click="confirm">
      {{ ready ? `确认住 ${nights} 晚` : '请点选离店日' }}
    </view>
  </view>
</template>

<style scoped lang="scss">
.stay {
  padding-bottom: 4px;
}
.stay__handle {
  width: 36px;
  height: 4px;
  margin: 0 auto 14px;
  border-radius: 999px;
  background: #dbe4ee;
}
.stay__head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 14px;
}
.stay__kicker {
  color: #3882f6;
  font-size: 11px;
  letter-spacing: 0.16em;
}
.stay__title {
  margin-top: 4px;
  color: #0f172a;
  font-size: 22px;
  font-weight: 700;
}
.stay__close {
  height: 32px;
  padding: 0 12px;
  border-radius: 10px;
  background: #f1f5f9;
  color: #64748b;
  font-size: 13px;
  line-height: 32px;
}
.stay__close--on {
  opacity: 0.75;
}
.stay__ticket {
  display: flex;
  align-items: center;
  padding: 12px 14px;
  border-radius: 18px;
  background: linear-gradient(135deg, #1e3a8a 0%, #3882f6 100%);
  color: #fff;
}
.stay__field {
  flex: 1;
  min-width: 0;
}
.stay__field--end {
  text-align: right;
}
.stay__field-k {
  display: block;
  opacity: 0.72;
  font-size: 10px;
  letter-spacing: 0.12em;
}
.stay__field-v {
  display: block;
  margin-top: 4px;
  font-size: 16px;
  font-weight: 700;
}
.stay__nights {
  flex-shrink: 0;
  min-width: 54px;
  padding: 8px 0;
  border-left: 1px dashed rgba(255, 255, 255, 0.35);
  border-right: 1px dashed rgba(255, 255, 255, 0.35);
  text-align: center;
  font-size: 15px;
  font-weight: 700;
}
.stay__pills {
  margin: 14px 0 6px;
  white-space: nowrap;
}
.stay__pill {
  display: inline-flex;
  align-items: center;
  height: 32px;
  padding: 0 14px;
  margin-right: 8px;
  border-radius: 999px;
  background: #f1f5f9;
  color: #334155;
  font-size: 13px;
  font-weight: 600;
}
.stay__pill--on {
  background: #0f172a;
  color: #fff;
}
.stay__month {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 12px 0 8px;
  color: #0f172a;
  font-size: 15px;
  font-weight: 700;
}
.stay__nav {
  width: 32px;
  height: 32px;
  color: #64748b;
  font-size: 16px;
}
.stay__week,
.stay__grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
}
.stay__week {
  color: #94a3b8;
  font-size: 11px;
}
.stay__grid {
  margin-top: 8px;
  row-gap: 6px;
}
.stay__day {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 40px;
  color: #1e293b;
  font-size: 13px;
}
.stay__day--muted {
  color: #cbd5e1;
}
.stay__day--range {
  background: #eaf2ff;
  color: #2563c7;
}
.stay__day--start,
.stay__day--end {
  border-radius: 12px;
  background: #3882f6;
  color: #fff;
  font-weight: 700;
}
.stay__day--start {
  border-radius: 12px 0 0 12px;
}
.stay__day--end {
  border-radius: 0 12px 12px 0;
}
.stay__day--start.stay__day--end {
  border-radius: 12px;
}
.stay__mark {
  position: absolute;
  bottom: 2px;
  font-size: 8px;
  line-height: 1;
}
.stay__cta {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 48px;
  margin-top: 16px;
  border-radius: 16px;
  background: #3882f6;
  color: #fff;
  font-size: 15px;
  font-weight: 700;
}
.stay__cta--off {
  background: #cbd5e1;
}
</style>
