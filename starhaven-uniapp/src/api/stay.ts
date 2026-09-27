import type { BannerItem, CommentItem, CouponItem, HouseCard, HouseDetail, MessageItem, OrderItem, PageData } from './types/stay'
import { http } from '@/http/http'
import { resolveMediaUrl } from '@/utils'

function withHouseMedia(item: HouseCard): HouseCard {
  return { ...item, coverImage: resolveMediaUrl(item.coverImage) }
}

function withPageMedia(page: PageData<HouseCard>): PageData<HouseCard> {
  return { ...page, list: (page.list || []).map(withHouseMedia) }
}

export async function fetchBanners() {
  const list = await http.get<BannerItem[]>('/api/v1/banner/list')
  return (list || []).map(item => ({ ...item, imageUrl: resolveMediaUrl(item.imageUrl) }))
}

export async function fetchRecommendHouses() {
  const list = await http.get<HouseCard[]>('/api/v1/house/recommend')
  return (list || []).map(withHouseMedia)
}

export async function fetchHousePage(query: Record<string, any>) {
  const page = await http.get<PageData<HouseCard>>('/api/v1/house/page', query)
  return withPageMedia(page)
}

export async function fetchHouseDetail(id: number) {
  const data = await http.get<HouseDetail>(`/api/v1/house/${id}`)
  return {
    ...data,
    coverImage: resolveMediaUrl(data.coverImage),
    hostAvatar: resolveMediaUrl(data.hostAvatar),
    images: (data.images || []).map(img => resolveMediaUrl(img)),
  }
}

export async function searchHouses(query: Record<string, any>) {
  const page = await http.get<PageData<HouseCard>>('/api/v1/search', query)
  return withPageMedia(page)
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

export async function fetchFavorites(page = 1, size = 10) {
  const data = await http.get<PageData<HouseCard>>('/api/v1/favorite/list', { page, size })
  return withPageMedia(data)
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

export function recordBrowse(houseId: number) {
  return http.post<void>(`/api/v1/browse/record/${houseId}`)
}

export async function fetchBrowseHistory() {
  const list = await http.get<HouseCard[]>('/api/v1/browse/history')
  return (list || []).map(withHouseMedia)
}

export function fetchBrowseCount() {
  return http.get<number>('/api/v1/browse/count')
}

export function clearBrowseHistory() {
  return http.delete<void>('/api/v1/browse/history')
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
