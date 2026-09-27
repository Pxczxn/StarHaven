<script setup lang="ts">
import { computed } from 'vue'
import type { EChartsOption } from 'echarts'
import { NGrid, NGi, NCard } from 'naive-ui'
import StatCard from '@/components/StatCard.vue'
import EChart from '@/components/EChart.vue'
import { buildTrendLabels, buildTrendValues } from '@/mock/platform'

const labels = buildTrendLabels(7)

const incomeTrendOption = computed<EChartsOption>(() => ({
  color: ['#2878FF'],
  tooltip: { trigger: 'axis' },
  grid: { left: 50, right: 20, top: 30, bottom: 30 },
  xAxis: { type: 'category', data: labels, boundaryGap: false },
  yAxis: { type: 'value' },
  series: [{ name: '收入', type: 'line', smooth: true, areaStyle: { color: 'rgba(40,120,255,0.12)' }, data: buildTrendValues(48, 7) }],
}))

const categoryOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'item' },
  legend: { bottom: 0 },
  series: [{
    type: 'pie',
    radius: ['42%', '68%'],
    data: [
      { value: 80, name: '房费收入' },
      { value: 12, name: '服务费' },
      { value: 5, name: '佣金' },
      { value: 3, name: '其他' },
    ],
  }],
}))
</script>

<template>
  <div>
    <h2 class="page-title">财务管理</h2>
    <n-grid :cols="4" :x-gap="16" :y-gap="16">
      <n-gi><StatCard label="总收入" value="486,520" prefix="¥" :trend="12" /></n-gi>
      <n-gi><StatCard label="订单金额" value="512,860" prefix="¥" :trend="8" /></n-gi>
      <n-gi><StatCard label="退款金额" value="26,340" prefix="¥" :trend="-3" /></n-gi>
      <n-gi><StatCard label="平台佣金" value="48,652" prefix="¥" :trend="6" /></n-gi>
    </n-grid>
    <n-grid :cols="2" :x-gap="16" :y-gap="16" style="margin-top: 16px">
      <n-gi>
        <n-card title="收入趋势" :bordered="false" class="page-card">
          <EChart :option="incomeTrendOption" />
        </n-card>
      </n-gi>
      <n-gi>
        <n-card title="收入分类" :bordered="false" class="page-card">
          <EChart :option="categoryOption" />
        </n-card>
      </n-gi>
    </n-grid>
  </div>
</template>
