<script setup lang="ts">
import { h, ref } from 'vue'
import { NButton, NDataTable, NTag, useMessage } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { mockMessages, type MessageRow } from '@/mock/platform'

const message = useMessage()
const list = ref(mockMessages)

const columns: DataTableColumns<MessageRow> = [
  { title: 'ID', key: 'id', width: 70 },
  { title: '发送对象', key: 'target', width: 120 },
  { title: '类型', key: 'type', width: 110 },
  { title: '标题', key: 'title', width: 160 },
  { title: '内容', key: 'content', minWidth: 220 },
  { title: '时间', key: 'time', width: 160 },
  {
    title: '状态',
    key: 'read',
    width: 90,
    render: row => h(NTag, { type: row.read ? 'default' : 'info', size: 'small' }, { default: () => row.read ? '已读' : '未读' }),
  },
  {
    title: '操作',
    key: 'actions',
    width: 120,
    render: () => h(NButton, { size: 'small', onClick: () => message.info('演示数据，发送功能待接入') }, { default: () => '发送' }),
  },
]
</script>

<template>
  <div>
    <h2 class="page-title">消息管理</h2>
    <div class="page-card">
      <div class="page-toolbar">
        <n-button type="primary">发送消息</n-button>
      </div>
      <n-data-table :columns="columns" :data="list" :pagination="{ pageSize: 10 }" />
    </div>
  </div>
</template>
