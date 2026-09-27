export interface ApiResult<T> {
  code: number
  message: string
  data: T
}

export interface PageData<T> {
  total: number
  list: T[]
  page: number
  size: number
}

export interface AdminUser {
  userId: number
  username: string
  nickname: string
  role: string
  roles?: string[]
  phone?: string
  avatar?: string
}

export interface HouseCard {
  id: number
  title: string
  coverImage: string
  city: string
  address: string
  price: number
  avgScore: number
  commentCount: number
  houseType?: string
  status?: number
  auditStatus?: number
}

export interface OrderItem {
  id: number
  orderNo: string
  houseId: number
  houseTitle: string
  houseCover: string
  checkInDate: string
  checkOutDate: string
  guestCount: number
  roomCount?: number
  contactName?: string
  contactPhone?: string
  housePrice?: number
  serviceFee?: number
  totalAmount: number
  orderStatus: string
  paymentStatus: number
  createTime: string
}

export interface DashboardData {
  userCount: number
  houseCount: number
  orderCount: number
  tradeAmount: number
  todayOrderCount: number
  hotHouses: HouseCard[]
}

export interface StaffUser {
  id: number
  username: string
  nickname: string
  phone?: string
  avatar?: string
  role: string
  status: number
  createTime?: string
}
