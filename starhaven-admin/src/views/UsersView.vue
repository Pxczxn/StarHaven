<script setup lang="ts">
import { h, onMounted, reactive, ref } from 'vue'
import { NAvatar, NButton, NDataTable, NInput, NPopconfirm, NSelect, NSpace, NTag, useMessage } from 'naive-ui'
import type { DataTableColumns, SelectOption } from 'naive-ui'
import { deleteUser, fetchUsers, updateUserRole, updateUserStatus } from '@/api/admin'
import type { StaffUser } from '@/types'

const message = useMessage()
const loading = ref(false)
const keyword = ref('')
const role = ref('')
const page = reactive({ page: 1, pageSize: 10, itemCount: 0 })
const list = ref<StaffUser[]>([])

const roleMap: Record<string, string> = {
  USER: '用户',
  HOST: '商家',
  ADMIN: '管理员',
}

const roleOptions: SelectOption[] = [
  { label: '全部角色', value: '' },
  { label: '用户', value: 'USER' },
  { label: '商家', value: 'HOST' },
  { label: '管理员', value: 'ADMIN' },
]

async function load() {
  loading.value = true
  try {
    const res = await fetchUsers({
      page: page.page,
      size: page.pageSize,
      keyword: keyword.value || undefined,
      role: role.value || undefined,
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
  role.value = ''
  page.page = 1
  void load()
}

async function toggle(row: StaffUser) {
  await updateUserStatus(row.id, row.status === 1 ? 0 : 1)
  message.success(row.status === 1 ? '已冻结' : '已解冻')
  await load()
}

async function setRole(row: StaffUser, next: string) {
  await updateUserRole(row.id, next)
  message.success('角色已更新')
  await load()
}

async function remove(row: StaffUser) {
  await deleteUser(row.id)
  message.success('已删除')
  await load()
}

const columns: DataTableColumns<StaffUser> = [
  { title: 'ID', key: 'id', width: 70 },
  {
    title: '头像',
    key: 'avatar',
    width: 80,
    render: row => h(NAvatar, { round: true, size: 'small', src: row.avatar }, { default: () => row.nickname?.slice(0, 1) }),
  },
  { title: '昵称', key: 'nickname' },
  { title: '手机号', key: 'phone' },
  {
    title: '角色',
    key: 'role',
    width: 100,
    render: row => h(NTag, { size: 'small' }, { default: () => roleMap[row.role] || row.role }),
  },
  {
    title: '状态',
    key: 'status',
    width: 90,
    render: row => h(NTag, { type: row.status === 1 ? 'success' : 'warning', size: 'small' }, { default: () => row.status === 1 ? '正常' : '冻结' }),
  },
  { title: '注册时间', key: 'createTime', width: 170, render: row => row.createTime || '—' },
  {
    title: '操作',
    key: 'actions',
    width: 260,
    render: row => h(NSpace, { size: 8 }, {
      default: () => [
        h(NButton, { size: 'small', onClick: () => toggle(row) }, { default: () => row.status === 1 ? '冻结' : '解冻' }),
        row.role !== 'HOST'
          ? h(NButton, { size: 'small', onClick: () => setRole(row, 'HOST') }, { default: () => '设为商家' })
          : null,
        h(NPopconfirm, { onPositiveClick: () => remove(row) }, {
          trigger: () => h(NButton, { size: 'small', type: 'error', ghost: true }, { default: () => '删除' }),
          default: () => '确认删除该用户？',
        }),
      ],
    }),
  },
]

onMounted(load)
</script>

<template>
  <div>
    <h2 class="page-title">用户管理</h2>
    <div class="page-card">
      <div class="page-toolbar">
        <n-input v-model:value="keyword" placeholder="用户名 / 昵称 / 手机号" style="width: 240px" @keyup.enter="page.page = 1; load()" />
        <n-select v-model:value="role" :options="roleOptions" style="width: 140px" />
        <n-button type="primary" @click="page.page = 1; load()">查询</n-button>
        <n-button @click="reset">重置</n-button>
        <n-button type="primary" ghost>新增用户</n-button>
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
