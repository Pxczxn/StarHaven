<script lang="ts" setup>
import type { HouseDetail } from '@/api/types/stay'
import { createOrder, fetchHouseDetail } from '@/api/stay'
import { useUserStore } from '@/store/user'

definePage({
  style: { navigationBarTitleText: '确认订单' },
})

const userStore = useUserStore()
const houseId = ref(0)
const checkIn = ref('')
const checkOut = ref('')
const guestCount = ref(2)
const roomCount = ref(1)
const detail = ref<HouseDetail>()
const contactName = ref('')
const contactPhone = ref('')
const submitting = ref(false)

onLoad(async (options) => {
  houseId.value = Number(options?.id || 0)
  checkIn.value = options?.checkIn || ''
  checkOut.value = options?.checkOut || ''
  guestCount.value = Number(options?.guestCount || 2)
  roomCount.value = Number(options?.roomCount || 1)
  contactName.value = userStore.userInfo.nickname || ''
  contactPhone.value = userStore.userInfo.phone || ''
  detail.value = await fetchHouseDetail(houseId.value)
})

async function submit() {
  submitting.value = true
  try {
    const order = await createOrder({
      houseId: houseId.value,
      checkInDate: checkIn.value,
      checkOutDate: checkOut.value,
      guestCount: guestCount.value,
      roomCount: roomCount.value,
      contactName: contactName.value,
      contactPhone: contactPhone.value,
    })
    uni.redirectTo({ url: `/pages/payment/payment?id=${order.id}` })
  }
  finally {
    submitting.value = false
  }
}
</script>

<template>
  <view v-if="detail" class="min-h-screen bg-page p-4">
    <view class="rounded-3 bg-white p-4">
      <view class="font-semibold">
        {{ detail.title }}
      </view>
      <view class="mt-2 text-sm text-muted">
        {{ checkIn }} 至 {{ checkOut }} · {{ guestCount }} 人 · {{ roomCount }} 间
      </view>
    </view>
    <view class="mt-3 rounded-3 bg-white p-4">
      <input v-model="contactName" class="mb-3 border-b border-#EEE py-2" placeholder="联系人">
      <input v-model="contactPhone" class="py-2" placeholder="联系电话">
    </view>
    <view class="mt-8 center h-11 rounded-full bg-primary text-white" @click="submit">
      {{ submitting ? '提交中...' : '提交订单' }}
    </view>
  </view>
</template>
