import { h, type Component } from 'vue'
import type { MenuOption } from 'naive-ui'
import {
  BarChartOutline,
  ChatboxEllipsesOutline,
  HomeOutline,
  MailOutline,
  PeopleOutline,
  PersonOutline,
  PricetagOutline,
  ReceiptOutline,
  SettingsOutline,
  StorefrontOutline,
} from '@vicons/ionicons5'

function icon(comp: Component) {
  return () => h(comp, { style: 'width: 18px; height: 18px' })
}

export interface AppMenuItem {
  label: string
  key: string
  icon: Component
  admin?: boolean
}

export const menuItems: AppMenuItem[] = [
  { label: '首页', key: '/', icon: HomeOutline },
  { label: '用户管理', key: '/users', icon: PeopleOutline, admin: true },
  { label: '房源管理', key: '/houses', icon: StorefrontOutline },
  { label: '房东管理', key: '/hosts', icon: PersonOutline, admin: true },
  { label: '订单管理', key: '/orders', icon: ReceiptOutline },
  { label: '评论管理', key: '/comments', icon: ChatboxEllipsesOutline, admin: true },
  { label: '消息管理', key: '/messages', icon: MailOutline, admin: true },
  { label: '财务管理', key: '/finance', icon: BarChartOutline, admin: true },
  { label: '优惠券管理', key: '/coupons', icon: PricetagOutline, admin: true },
  { label: '系统设置', key: '/settings', icon: SettingsOutline, admin: true },
]

export function buildMenuOptions(isAdmin: boolean): MenuOption[] {
  return menuItems
    .filter(item => !item.admin || isAdmin)
    .map(item => ({
      label: item.label,
      key: item.key,
      icon: icon(item.icon),
    }))
}
