export interface HostRow {
  id: number
  nickname: string
  phone: string
  avatar: string
  verified: boolean
  houseCount: number
  orderCount: number
  income: number
  status: number
}

export interface CommentRow {
  id: number
  user: string
  house: string
  score: number
  content: string
  time: string
  status: string
}

export interface MessageRow {
  id: number
  target: string
  type: string
  title: string
  content: string
  time: string
  read: boolean
}

export interface CouponRow {
  id: number
  name: string
  amount: number
  minSpend: number
  validRange: string
  claimed: number
  status: string
}

export const mockHosts: HostRow[] = [
  { id: 1, nickname: '星栖商家', phone: '13800000002', avatar: 'https://picsum.photos/seed/host/80', verified: true, houseCount: 8, orderCount: 126, income: 486520, status: 1 },
  { id: 2, nickname: '海边房东', phone: '13800000012', avatar: 'https://picsum.photos/seed/host2/80', verified: true, houseCount: 3, orderCount: 58, income: 128600, status: 1 },
  { id: 3, nickname: '山居民宿', phone: '13800000022', avatar: 'https://picsum.photos/seed/host3/80', verified: false, houseCount: 1, orderCount: 12, income: 18600, status: 1 },
]

export const mockComments: CommentRow[] = [
  { id: 1, user: '陈女士', house: '海景木屋 A 栋', score: 5, content: '环境安静，看海视野很好，下次还会再来。', time: '2026-09-26 18:20', status: '正常' },
  { id: 2, user: '李先生', house: '花园独栋', score: 4, content: '院子很大，适合家庭聚会，就是离主路稍远。', time: '2026-09-25 11:05', status: '正常' },
  { id: 3, user: '王同学', house: '湖景阁楼', score: 5, content: '拍照很出片，房东回复也很及时。', time: '2026-09-24 09:40', status: '正常' },
]

export const mockMessages: MessageRow[] = [
  { id: 1, target: '全部用户', type: '系统消息', title: '平台维护通知', content: '9 月 28 日凌晨将进行系统升级。', time: '2026-09-27 10:00', read: true },
  { id: 2, target: '商家', type: '订单通知', title: '新订单提醒', content: '您有新的待入住订单，请及时确认。', time: '2026-09-27 09:12', read: false },
  { id: 3, target: '全部用户', type: '活动消息', title: '国庆出行优惠', content: '领取满 500 减 100 优惠券。', time: '2026-09-26 15:30', read: true },
]

export const mockCoupons: CouponRow[] = [
  { id: 1, name: '新客立减券', amount: 100, minSpend: 500, validRange: '2026-09-01 ~ 2026-10-31', claimed: 3268, status: '进行中' },
  { id: 2, name: '周末专享券', amount: 50, minSpend: 300, validRange: '2026-09-01 ~ 2026-12-31', claimed: 1580, status: '进行中' },
  { id: 3, name: '暑期返场券', amount: 80, minSpend: 400, validRange: '2026-07-01 ~ 2026-08-31', claimed: 4200, status: '已结束' },
]

export function buildTrendLabels(days = 7) {
  const labels: string[] = []
  const now = new Date()
  for (let i = days - 1; i >= 0; i--) {
    const d = new Date(now)
    d.setDate(now.getDate() - i)
    labels.push(`${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`)
  }
  return labels
}

export function buildTrendValues(seed: number, days = 7) {
  return Array.from({ length: days }, (_, i) => Math.round(seed * (0.6 + Math.sin(i) * 0.2 + i * 0.08)))
}
