<script setup lang="ts">
import { h, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NDataTable, NInput, NPopconfirm, NSelect, NSpace, NTag, useMessage } from 'naive-ui'
import type { DataTableColumns, SelectOption } from 'naive-ui'
import { fetchOrders, refundOrder, updateOrderStatus } from '@/api/admin'
import type { OrderItem } from '@/types'

const router = useRouter()
const message = useMessage()
const loading = ref(false)
const status = ref('')
const keyword = ref('')
const page = reactive({ page: 1, pageSize: 10, itemCount: 0 })
const list = ref<OrderItem[]>([])

const statusMap: Record<string, string> = {
  WAIT_PAY: '待付款',
  PAID: '待入住',
  CHECK_IN: '入住中',
  FINISHED: '已完成',
  CANCEL: '已取消',
}

const statusOptions: SelectOption[] = [
  { label: '全部状态', value: '' },
  { label: '待付款', value: 'WAIT_PAY' },
  { label: '待入住', value: 'PAID' },
  { label: '入住中', value: 'CHECK_IN' },
  { label: '已完成', value: 'FINISHED' },
  { label: '已取消', value: 'CANCEL' },
]

async function load() {
  loading.value = true
  try {
    const res = await fetchOrders({
      page: page.page,
      size: page.pageSize,
      status: status.value || undefined,
      keyword: keyword.value || undefined,
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
  status.value = ''
  keyword.value = ''
  page.page = 1
  void load()
}

async function setStatus(row: OrderItem, next: string) {
  await updateOrderStatus(row.id, next)
  message.success('状态已更新')
  await load()
}

async function refund(row: OrderItem) {
  await refundOrder(row.id)
  message.success('已退款')
  await load()
}

const columns: DataTableColumns<OrderItem> = [
  { title: '订单号', key: 'orderNo', minWidth: 160 },
  { title: '用户', key: 'contactName', width: 100 },
  { title: '房源', key: 'houseTitle', minWidth: 140 },
  { title: '入住时间', key: 'checkInDate', width: 120 },
  { title: '金额', key: 'totalAmount', width: 100, render: row => `¥${row.totalAmount}` },
  {
    title: '支付状态',
    key: 'paymentStatus',
    width: 100,
    render: row => h(NTag, { size: 'small', type: row.paymentStatus === 1 ? 'success' : 'warning' }, { default: () => row.paymentStatus === 1 ? '已支付' : '未支付' }),
  },
  {
    title: '订单状态',
    key: 'orderStatus',
    width: 100,
    render: row => h(NTag, { size: 'small' }, { default: () => statusMap[row.orderStatus] || row.orderStatus }),
  },
  {
    title: '操作',
    key: 'actions',
    width: 260,
    render: row => h(NSpace, { size: 8 }, {
      default: () => [
        h(NButton, { size: 'small', onClick: () => router.push(`/orders/${row.id}`) }, { default: () => '查看' }),
        row.orderStatus === 'PAID'
          ? h(NButton, { size: 'small', onClick: () => setStatus(row, 'CHECK_IN') }, { default: () => '办理入住' })
          : null,
        row.orderStatus !== 'CANCEL'
          ? h(NPopconfirm, { onPositiveClick: () => refund(row) }, {
              trigger: () => h(NButton, { size: 'small', type: 'error', ghost: true }, { default: () => '退款' }),
              default: () => '确认退款并取消订单？',
            })
          : null,
      ],
    }),
  },
]

onMounted(load)
</script>

<template>
  <div>
    <h2 class="page-title">订单管理</h2>
    <div class="page-card">
      <div class="page-toolbar">
        <n-select v-model:value="status" :options="statusOptions" style="width: 140px" />
        <n-input v-model:value="keyword" placeholder="订单号 / 用户 / 房源" style="width: 240px" @keyup.enter="page.page = 1; load()" />
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
