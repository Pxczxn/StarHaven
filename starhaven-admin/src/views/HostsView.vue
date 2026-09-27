<script setup lang="ts">
import { h, ref } from 'vue'
import { NAvatar, NButton, NDataTable, NInput, NTag, useMessage } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { mockHosts, type HostRow } from '@/mock/platform'

const message = useMessage()
const keyword = ref('')
const list = ref(mockHosts)

const columns: DataTableColumns<HostRow> = [
  { title: 'ID', key: 'id', width: 70 },
  {
    title: '头像',
    key: 'avatar',
    width: 80,
    render: row => h(NAvatar, { round: true, size: 'small', src: row.avatar }),
  },
  { title: '姓名', key: 'nickname' },
  { title: '手机号', key: 'phone' },
  {
    title: '认证状态',
    key: 'verified',
    width: 100,
    render: row => h(NTag, { type: row.verified ? 'success' : 'warning', size: 'small' }, { default: () => row.verified ? '已认证' : '待认证' }),
  },
  { title: '房源数量', key: 'houseCount', width: 100 },
  { title: '订单数量', key: 'orderCount', width: 100 },
  { title: '收入', key: 'income', width: 120, render: row => `¥${row.income.toLocaleString()}` },
  {
    title: '操作',
    key: 'actions',
    width: 180,
    render: () => h(NButton, { size: 'small', onClick: () => message.info('演示数据，认证功能待接入') }, { default: () => '认证' }),
  },
]
</script>

<template>
  <div>
    <h2 class="page-title">房东管理</h2>
    <div class="page-card">
      <div class="page-toolbar">
        <n-input v-model:value="keyword" placeholder="姓名 / 手机号" style="width: 220px" />
        <n-button type="primary">查询</n-button>
        <n-button>重置</n-button>
      </div>
      <n-data-table :columns="columns" :data="list" :pagination="{ pageSize: 10 }" />
    </div>
  </div>
</template>
