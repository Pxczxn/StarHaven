import http from '@/api/http'
import type { AdminUser, DashboardData, HouseCard, OrderItem, PageData, StaffUser } from '@/types'

export function login(username: string, password: string) {
  return http.post<{ token: string, expiresIn: number }>('/api/v1/user/login', {
    username,
    password,
    loginType: 'PASSWORD',
  })
}

export function fetchProfile() {
  return http.get<AdminUser>('/api/v1/user/info')
}

export function logout() {
  return http.post('/api/v1/user/logout')
}

export function fetchDashboard() {
  return http.get<DashboardData>('/admin/dashboard')
}

export function fetchHouses(params: { page: number, size: number, keyword?: string, auditStatus?: number | null }) {
  return http.get<PageData<HouseCard>>('/admin/house/page', { params })
}

export function updateHouseStatus(id: number, status: number) {
  return http.put('/admin/house/status', null, { params: { id, status } })
}

export function auditHouse(id: number, auditStatus: number) {
  return http.put('/admin/house/audit', null, { params: { id, auditStatus } })
}

export function deleteHouse(id: number) {
  return http.delete('/admin/house', { params: { id } })
}

export function fetchOrders(params: { page: number, size: number, status?: string, keyword?: string }) {
  return http.get<PageData<OrderItem>>('/admin/order/page', { params })
}

export function fetchOrderDetail(id: number) {
  return http.get<OrderItem>(`/admin/order/${id}`)
}

export function updateOrderStatus(id: number, status: string) {
  return http.put('/admin/order/status', null, { params: { id, status } })
}

export function refundOrder(id: number) {
  return http.put('/admin/order/refund', null, { params: { id } })
}

export function fetchUsers(params: { page: number, size: number, keyword?: string, role?: string }) {
  return http.get<PageData<StaffUser>>('/admin/user/page', { params })
}

export function updateUserStatus(id: number, status: number) {
  return http.put('/admin/user/status', null, { params: { id, status } })
}

export function updateUserRole(id: number, role: string) {
  return http.put('/admin/user/role', null, { params: { id, role } })
}

export function deleteUser(id: number) {
  return http.delete('/admin/user', { params: { id } })
}
