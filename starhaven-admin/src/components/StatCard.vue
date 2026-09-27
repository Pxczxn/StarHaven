<script setup lang="ts">
defineProps<{
  label: string
  value: string | number
  trend?: number
  prefix?: string
}>()
</script>

<template>
  <div class="stat-card">
    <div class="stat-card__label">{{ label }}</div>
    <div class="stat-card__value">
      <span v-if="prefix" class="stat-card__prefix">{{ prefix }}</span>{{ value }}
    </div>
    <div v-if="trend !== undefined" class="stat-card__trend" :class="trend >= 0 ? 'up' : 'down'">
      {{ trend >= 0 ? '+' : '' }}{{ trend }}%
    </div>
  </div>
</template>

<style scoped>
.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px 24px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.06);
}
.stat-card__label {
  color: #666;
  font-size: 14px;
}
.stat-card__value {
  margin-top: 8px;
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1.2;
}
.stat-card__prefix {
  font-size: 18px;
  margin-right: 2px;
}
.stat-card__trend {
  margin-top: 8px;
  font-size: 13px;
  font-weight: 600;
}
.stat-card__trend.up {
  color: #18a058;
}
.stat-card__trend.down {
  color: #d03050;
}
</style>
