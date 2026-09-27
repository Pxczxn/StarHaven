<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NButton, NCard, NDescriptions, NDescriptionsItem, NImage, NSpin, NTag, useMessage } from 'naive-ui'
import { fetchOrderDetail } from '@/api/admin'
import type { OrderItem } from '@/types'

const route = useRoute()
const router = useRouter()
const message = useMessage()
const loading = ref(false)
const order = ref<OrderItem>()

const statusMap: Record<string, string> = {
  WAIT_PAY: '待付款',
  PAID: '待入住',
  CHECK_IN: '入住中',
  FINISHED: '已完成',
  CANCEL: '已取消',
}

const statusType = computed(() => {
  const map: Record<string, 'default' | 'success' | 'warning' | 'error' | 'info'> = {
    WAIT_PAY: 'warning',
    PAID: 'info',
    CHECK_IN: 'success',
    FINISHED: 'success',
    CANCEL: 'error',
  }
  return map[order.value?.orderStatus || ''] || 'default'
})

onMounted(async () => {
  loading.value = true
  try {
    order.value = await fetchOrderDetail(Number(route.params.id))
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
    <div class="page-toolbar" style="margin-bottom: 16px">
      <n-button @click="router.back()">返回</n-button>
    </div>
    <n-card v-if="order" :bordered="false" class="page-card">
      <div class="order-head">
        <div>
          <div class="order-head__no">{{ order.orderNo }}</div>
          <div class="order-head__time">创建时间：{{ order.createTime }}</div>
        </div>
        <n-tag :type="statusType" size="large">
          {{ statusMap[order.orderStatus] || order.orderStatus }}
        </n-tag>
      </div>
      <div class="order-grid">
        <n-card title="房源信息" size="small" :bordered="false">
          <div class="order-house">
            <n-image :src="order.houseCover" width="120" height="88" object-fit="cover" style="border-radius: 8px" />
            <div>
              <div class="order-house__title">{{ order.houseTitle }}</div>
              <div class="order-house__meta">房源 ID：{{ order.houseId }}</div>
            </div>
          </div>
        </n-card>
        <n-card title="用户信息" size="small" :bordered="false">
          <n-descriptions :column="1" label-placement="left">
            <n-descriptions-item label="姓名">{{ order.contactName || '—' }}</n-descriptions-item>
            <n-descriptions-item label="手机号">{{ order.contactPhone || '—' }}</n-descriptions-item>
          </n-descriptions>
        </n-card>
        <n-card title="入住信息" size="small" :bordered="false">
          <n-descriptions :column="1" label-placement="left">
            <n-descriptions-item label="入住日期">{{ order.checkInDate }}</n-descriptions-item>
            <n-descriptions-item label="退房日期">{{ order.checkOutDate }}</n-descriptions-item>
            <n-descriptions-item label="入住人数">{{ order.guestCount }} 人</n-descriptions-item>
          </n-descriptions>
        </n-card>
        <n-card title="金额信息" size="small" :bordered="false">
          <n-descriptions :column="1" label-placement="left">
            <n-descriptions-item label="房费">¥{{ order.housePrice ?? order.totalAmount }}</n-descriptions-item>
            <n-descriptions-item label="服务费">¥{{ order.serviceFee ?? 0 }}</n-descriptions-item>
            <n-descriptions-item label="总金额">
              <strong>¥{{ order.totalAmount }}</strong>
            </n-descriptions-item>
          </n-descriptions>
        </n-card>
      </div>
    </n-card>
  </n-spin>
</template>

<style scoped>
.order-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.order-head__no {
  font-size: 22px;
  font-weight: 700;
}
.order-head__time {
  margin-top: 6px;
  color: #666;
  font-size: 13px;
}
.order-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}
.order-house {
  display: flex;
  gap: 16px;
  align-items: center;
}
.order-house__title {
  font-size: 16px;
  font-weight: 600;
}
.order-house__meta {
  margin-top: 6px;
  color: #666;
  font-size: 13px;
}
@media (max-width: 960px) {
  .order-grid {
    grid-template-columns: 1fr;
  }
}
</style>
