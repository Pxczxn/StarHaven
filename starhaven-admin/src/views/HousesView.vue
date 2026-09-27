<script setup lang="ts">
import { h, onMounted, reactive, ref } from 'vue'
import { NButton, NDataTable, NImage, NInput, NPopconfirm, NSelect, NSpace, NTag, useMessage } from 'naive-ui'
import type { DataTableColumns, SelectOption } from 'naive-ui'
import { auditHouse, deleteHouse, fetchHouses, updateHouseStatus } from '@/api/admin'
import { useAuthStore } from '@/stores/auth'
import type { HouseCard } from '@/types'

const message = useMessage()
const auth = useAuthStore()
const loading = ref(false)
const keyword = ref('')
const auditStatus = ref<number | ''>('')
const page = reactive({ page: 1, pageSize: 10, itemCount: 0 })
const list = ref<HouseCard[]>([])

const auditOptions: SelectOption[] = [
  { label: '全部审核', value: '' },
  { label: '已通过', value: 1 },
  { label: '待审核', value: 0 },
]

async function load() {
  loading.value = true
  try {
    const res = await fetchHouses({
      page: page.page,
      size: page.pageSize,
      keyword: keyword.value || undefined,
      auditStatus: auditStatus.value === '' ? undefined : auditStatus.value,
    })
    list.value = res.list || []
    page.itemCount = res.total
  }
  catch (error) {
    message.error(error instanceof Error ? error.message : '加载失败')
  }
  finally {
    loading.value = false
  }
}

function reset() {
  keyword.value = ''
  auditStatus.value = ''
  page.page = 1
  void load()
}

async function toggleStatus(row: HouseCard) {
  await updateHouseStatus(row.id, row.status === 1 ? 0 : 1)
  message.success(row.status === 1 ? '已下架' : '已上架')
  await load()
}

async function audit(row: HouseCard, status: number) {
  await auditHouse(row.id, status)
  message.success(status === 1 ? '已通过' : '已拒绝')
  await load()
}

async function remove(row: HouseCard) {
  await deleteHouse(row.id)
  message.success('已删除')
  await load()
}

const columns: DataTableColumns<HouseCard> = [
  { title: 'ID', key: 'id', width: 70 },
  {
    title: '图片',
    key: 'coverImage',
    width: 88,
    render: row => h(NImage, { src: row.coverImage, width: 64, height: 48, objectFit: 'cover', style: 'border-radius: 8px' }),
  },
  { title: '房源名称', key: 'title', minWidth: 160 },
  { title: '城市', key: 'city', width: 100 },
  { title: '价格', key: 'price', width: 90, render: row => `¥${row.price}` },
  {
    title: '状态',
    key: 'status',
    width: 90,
    render: row => h(NTag, { type: row.status === 1 ? 'success' : 'default', size: 'small' }, { default: () => row.status === 1 ? '上架' : '下架' }),
  },
  {
    title: '审核状态',
    key: 'auditStatus',
    width: 100,
    render: row => h(NTag, { type: row.auditStatus === 1 ? 'success' : 'warning', size: 'small' }, { default: () => row.auditStatus === 1 ? '已通过' : '待审核' }),
  },
  {
    title: '操作',
    key: 'actions',
    width: 260,
    render: row => h(NSpace, { size: 8 }, {
      default: () => [
        h(NButton, { size: 'small', onClick: () => toggleStatus(row) }, { default: () => row.status === 1 ? '下架' : '上架' }),
        auth.isAdmin
          ? h(NButton, { size: 'small', type: 'primary', ghost: true, onClick: () => audit(row, 1) }, { default: () => '审核' })
          : null,
        h(NPopconfirm, { onPositiveClick: () => remove(row) }, {
          trigger: () => h(NButton, { size: 'small', type: 'error', ghost: true }, { default: () => '删除' }),
          default: () => '确认删除该房源？',
        }),
      ],
    }),
  },
]

onMounted(load)
</script>

<template>
  <div>
    <h2 class="page-title">房源管理</h2>
    <div class="page-card">
      <div class="page-toolbar">
        <n-input v-model:value="keyword" placeholder="房源名称" style="width: 220px" @keyup.enter="page.page = 1; load()" />
        <n-select v-model:value="auditStatus" :options="auditOptions" style="width: 140px" />
        <n-button type="primary" @click="page.page = 1; load()">查询</n-button>
        <n-button @click="reset">重置</n-button>
      </div>
      <n-data-table
        :columns="columns"
        :data="list"
        :loading="loading"
        :pagination="{
          page: page.page,
          pageSize: page.pageSize,
          itemCount: page.itemCount,
          onChange: (p: number) => { page.page = p; load() },
        }"
      />
    </div>
  </div>
</template>
