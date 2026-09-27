<script setup lang="ts">
import { NConfigProvider, NDialogProvider, NMessageProvider, NNotificationProvider, dateZhCN, zhCN } from 'naive-ui'
import { themeOverrides } from '@/theme'
</script>

<template>
  <n-config-provider :locale="zhCN" :date-locale="dateZhCN" :theme-overrides="themeOverrides">
    <n-message-provider>
      <n-dialog-provider>
        <n-notification-provider>
          <router-view />
        </n-notification-provider>
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>
