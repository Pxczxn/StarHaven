<script setup lang="ts">
import { h, ref } from 'vue'
import { NButton, NDataTable, NTag, useMessage } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { mockCoupons, type CouponRow } from '@/mock/platform'

const message = useMessage()
const list = ref(mockCoupons)

const columns: DataTableColumns<CouponRow> = [
  { title: 'ID', key: 'id', width: 70 },
  { title: '优惠券名称', key: 'name', minWidth: 140 },
  { title: '优惠金额', key: 'amount', width: 100, render: row => `¥${row.amount}` },
  { title: '使用条件', key: 'minSpend', width: 120, render: row => `满 ¥${row.minSpend}` },
  { title: '有效时间', key: 'validRange', minWidth: 180 },
  { title: '领取数量', key: 'claimed', width: 100 },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render: row => h(NTag, { type: row.status === '进行中' ? 'success' : 'default', size: 'small' }, { default: () => row.status }),
  },
  {
    title: '操作',
    key: 'actions',
    width: 160,
    render: () => h(NButton, { size: 'small', onClick: () => message.info('演示数据，编辑功能待接入') }, { default: () => '编辑' }),
  },
]
</script>

<template>
  <div>
    <h2 class="page-title">优惠券管理</h2>
    <div class="page-card">
      <div class="page-toolbar">
        <n-button type="primary">新增优惠券</n-button>
      </div>
      <n-data-table :columns="columns" :data="list" :pagination="{ pageSize: 10 }" />
    </div>
  </div>
</template>
