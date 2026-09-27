<script setup lang="ts">
import { h, ref } from 'vue'
import { NButton, NDataTable, NPopconfirm, NRate, NTag, useMessage } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { mockComments, type CommentRow } from '@/mock/platform'

const message = useMessage()
const list = ref(mockComments)

const columns: DataTableColumns<CommentRow> = [
  { title: 'ID', key: 'id', width: 70 },
  { title: '用户', key: 'user', width: 100 },
  { title: '房源', key: 'house', minWidth: 140 },
  {
    title: '评分',
    key: 'score',
    width: 120,
    render: row => h(NRate, { readonly: true, defaultValue: row.score, size: 'small' }),
  },
  { title: '评论内容', key: 'content', minWidth: 220 },
  { title: '时间', key: 'time', width: 160 },
  {
    title: '状态',
    key: 'status',
    width: 90,
    render: row => h(NTag, { type: 'success', size: 'small' }, { default: () => row.status }),
  },
  {
    title: '操作',
    key: 'actions',
    width: 160,
    render: () => h(NPopconfirm, {
      onPositiveClick: () => message.success('已删除'),
    }, {
      trigger: () => h(NButton, { size: 'small', type: 'error', ghost: true }, { default: () => '删除' }),
      default: () => '确认删除该评论？',
    }),
  },
]
</script>

<template>
  <div>
    <h2 class="page-title">评论管理</h2>
    <div class="page-card">
      <n-data-table :columns="columns" :data="list" :pagination="{ pageSize: 10 }" />
    </div>
  </div>
</template>
