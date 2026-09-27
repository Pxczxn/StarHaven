<script setup lang="ts">
import { reactive } from 'vue'
import { NButton, NForm, NFormItem, NInput, NTabPane, NTabs, useMessage } from 'naive-ui'

const message = useMessage()
const form = reactive({
  platformName: '星栖民宿预订平台',
  siteUrl: 'https://admin.starhaven.com',
  hotline: '400-660-8866',
  copyright: '© 2026 StarHaven 星栖',
  intro: '让每一次停留，都成为星光下的相遇。',
})

function save() {
  message.success('设置已保存（演示）')
}
</script>

<template>
  <div>
    <h2 class="page-title">系统设置</h2>
    <div class="page-card">
      <n-tabs type="line">
        <n-tab-pane name="basic" tab="基础设置">
          <n-form :model="form" label-placement="left" label-width="100" style="max-width: 640px; margin-top: 12px">
            <n-form-item label="平台名称">
              <n-input v-model:value="form.platformName" />
            </n-form-item>
            <n-form-item label="系统网址">
              <n-input v-model:value="form.siteUrl" />
            </n-form-item>
            <n-form-item label="客服电话">
              <n-input v-model:value="form.hotline" />
            </n-form-item>
            <n-form-item label="版权信息">
              <n-input v-model:value="form.copyright" />
            </n-form-item>
            <n-form-item label="系统介绍">
              <n-input v-model:value="form.intro" type="textarea" :rows="4" />
            </n-form-item>
            <n-form-item>
              <n-button type="primary" @click="save">保存修改</n-button>
            </n-form-item>
          </n-form>
        </n-tab-pane>
        <n-tab-pane name="params" tab="参数配置">
          <p style="color:#666;margin-top:12px">订单规则、支付配置、消息配置将在后续版本接入。</p>
        </n-tab-pane>
      </n-tabs>
    </div>
  </div>
</template>
