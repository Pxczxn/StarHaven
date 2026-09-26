import type { BannerItem, CommentItem, CouponItem, HouseCard, HouseDetail, MessageItem, OrderItem, PageData } from './types/stay'
import { http } from '@/http/http'

export function fetchBanners() {
  return http.get<BannerItem[]>('/api/v1/banner/list')
}

export function fetchRecommendHouses() {
  return http.get<HouseCard[]>('/api/v1/house/recommend')
}

export function fetchHousePage(query: Record<string, any>) {
  return http.get<PageData<HouseCard>>('/api/v1/house/page', query)
}

export function fetchHouseDetail(id: number) {
  return http.get<HouseDetail>(`/api/v1/house/${id}`)
}

export function searchHouses(query: Record<string, any>) {
  return http.get<PageData<HouseCard>>('/api/v1/search', query)
}

export function fetchHotKeywords() {
  return http.get<string[]>('/api/v1/search/hot')
}

export function fetchSearchHistory() {
  return http.get<string[]>('/api/v1/search/history')
}

export function clearSearchHistory() {
  return http.delete<void>('/api/v1/search/history')
}

export function toggleFavorite(houseId: number) {
  return http.post<{ favorited: boolean }>('/api/v1/favorite/toggle', undefined, { houseId })
}

export function fetchFavorites(page = 1, size = 10) {
  return http.get<PageData<HouseCard>>('/api/v1/favorite/list', { page, size })
}

export function createOrder(data: Record<string, any>) {
  return http.post<OrderItem>('/api/v1/order/create', data)
}

export function fetchMyOrders(status?: string, page = 1, size = 10) {
  return http.get<PageData<OrderItem>>('/api/v1/order/my', { status, page, size })
}

export function fetchOrderDetail(id: number) {
  return http.get<OrderItem>(`/api/v1/order/${id}`)
}

export function cancelOrder(id: number) {
  return http.put<void>('/api/v1/order/cancel', undefined, { id })
}

export function createPay(orderId: number, payType: string) {
  return http.post('/api/v1/pay/create', { orderId, payType })
}

export function mockPaySuccess(orderId: number) {
  return http.post<void>('/api/v1/pay/mockSuccess', undefined, { orderId })
}

export function fetchComments(houseId: number) {
  return http.get<CommentItem[]>(`/api/v1/comment/list/${houseId}`)
}

export function createComment(data: { orderId: number, score: number, content: string }) {
  return http.post<void>('/api/v1/comment/create', data)
}

export function fetchMessages() {
  return http.get<MessageItem[]>('/api/v1/message/list')
}

export function readMessage(id: number) {
  return http.put<void>('/api/v1/message/read', undefined, { id })
}

export function fetchMyCoupons() {
  return http.get<CouponItem[]>('/api/v1/coupon/my')
}
