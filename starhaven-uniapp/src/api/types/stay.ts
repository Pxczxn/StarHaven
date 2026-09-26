export interface HouseCard {
  id: number
  title: string
  coverImage: string
  city: string
  address: string
  price: number
  avgScore: number
  commentCount: number
  facilities?: string[]
  favorited?: boolean
}

export interface HouseDetail extends HouseCard {
  hostId: number
  description: string
  latitude?: number
  longitude?: number
  guestNumber: number
  roomNumber: number
  bathroomNumber: number
  bedNumber: number
  houseType: string
  images: string[]
  hostNickname: string
  hostAvatar: string
  hostCertified: boolean
}

export interface PageData<T> {
  total: number
  list: T[]
  page: number
  size: number
}

export interface BannerItem {
  id: number
  title: string
  subtitle: string
  imageUrl: string
  linkUrl?: string
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
  roomCount: number
  contactName?: string
  contactPhone?: string
  housePrice: number
  serviceFee: number
  totalAmount: number
  orderStatus: string
  paymentStatus: number
  createTime: string
}

export interface CommentItem {
  id: number
  userId: number
  nickname: string
  avatar: string
  score: number
  content: string
  images?: string[]
  createTime: string
}

export interface MessageItem {
  id: number
  type: string
  title: string
  content: string
  readStatus: number
  createTime: string
}

export interface CouponItem {
  id: number
  couponUserId: number
  name: string
  discount: number
  conditionAmount: number
  used: number
}
