<script setup lang="ts">
import { computed, h, onMounted, ref } from 'vue'
import type { EChartsOption } from 'echarts'
import { NGrid, NGi, NCard, NDataTable, NImage, NSpin, useMessage } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { fetchDashboard } from '@/api/admin'
import { useAuthStore } from '@/stores/auth'
import StatCard from '@/components/StatCard.vue'
import EChart from '@/components/EChart.vue'
import { buildTrendLabels, buildTrendValues } from '@/mock/platform'
import type { DashboardData, HouseCard } from '@/types'

const message = useMessage()
const auth = useAuthStore()
const loading = ref(false)
const data = ref<DashboardData>()

const labels = buildTrendLabels(7)

const orderTrendOption = computed<EChartsOption>(() => ({
  color: ['#2878FF'],
  grid: { left: 40, right: 20, top: 30, bottom: 30 },
  tooltip: { trigger: 'axis' },
  xAxis: { type: 'category', data: labels, boundaryGap: false },
  yAxis: { type: 'value' },
  series: [{
    name: '订单数',
    type: 'line',
    smooth: true,
    areaStyle: { color: 'rgba(40,120,255,0.12)' },
    data: buildTrendValues(data.value?.todayOrderCount || 12, 7),
  }],
}))

const incomeTrendOption = computed<EChartsOption>(() => ({
  color: ['#73D5C5'],
  grid: { left: 50, right: 20, top: 30, bottom: 30 },
  tooltip: { trigger: 'axis' },
  xAxis: { type: 'category', data: labels, boundaryGap: false },
  yAxis: { type: 'value' },
  series: [{
    name: '收入',
    type: 'line',
    smooth: true,
    areaStyle: { color: 'rgba(115,213,197,0.18)' },
    data: buildTrendValues(Number(data.value?.tradeAmount || 1200) / 100, 7),
  }],
}))

const houseColumns: DataTableColumns<HouseCard> = [
  { title: '排名', key: 'rank', width: 70, render: (_row, index) => index + 1 },
  {
    title: '图片',
    key: 'coverImage',
    width: 88,
    render: row => h(NImage, { src: row.coverImage, width: 64, height: 48, objectFit: 'cover', style: 'border-radius: 8px' }),
  },
  { title: '房源名称', key: 'title', minWidth: 160 },
  { title: '订单数', key: 'commentCount', width: 90, render: row => row.commentCount * 3 },
  { title: '收入', key: 'price', width: 100, render: row => `¥${Number(row.price) * 12}` },
  { title: '评分', key: 'avgScore', width: 80 },
]

onMounted(async () => {
  loading.value = true
  try {
    data.value = await fetchDashboard()
  }
  catch (error) {
    message.error(error instanceof Error ? error.message : '加载失败')
  }
  finally {
    loading.value = false
  }
})
</script>

<template>
  <n-spin :show="loading">
    <n-grid :cols="auth.isAdmin ? 4 : 3" :x-gap="16" :y-gap="16">
      <n-gi v-if="auth.isAdmin">
        <StatCard label="用户数量" :value="data?.userCount ?? 0" :trend="12" />
      </n-gi>
      <n-gi>
        <StatCard label="房源数量" :value="data?.houseCount ?? 0" :trend="8" />
      </n-gi>
      <n-gi>
        <StatCard label="订单数量" :value="data?.orderCount ?? 0" :trend="15" />
      </n-gi>
      <n-gi>
        <StatCard label="交易金额" :value="Number(data?.tradeAmount ?? 0).toLocaleString()" prefix="¥" :trend="6" />
      </n-gi>
    </n-grid>

    <n-grid :cols="2" :x-gap="16" :y-gap="16" style="margin-top: 16px">
      <n-gi>
        <n-card title="订单趋势" :bordered="false" class="page-card">
          <EChart :option="orderTrendOption" />
        </n-card>
      </n-gi>
      <n-gi>
        <n-card title="收入趋势" :bordered="false" class="page-card">
          <EChart :option="incomeTrendOption" />
        </n-card>
      </n-gi>
    </n-grid>

    <n-card title="热门房源排行" :bordered="false" class="page-card" style="margin-top: 16px">
      <n-data-table :columns="houseColumns" :data="data?.hotHouses || []" :bordered="false" :pagination="false" />
    </n-card>
  </n-spin>
</template>
