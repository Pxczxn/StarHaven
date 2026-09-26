import dayjs from 'dayjs'

export interface CityItem { name: string, initial: string }
export type OverlayKind = 'city' | 'date' | 'filter'

export const CITY_PRESET: CityItem[] = [
  { name: '杭州', initial: 'H' },
  { name: '上海', initial: 'S' },
  { name: '北京', initial: 'B' },
  { name: '广州', initial: 'G' },
  { name: '深圳', initial: 'S' },
  { name: '成都', initial: 'C' },
  { name: '重庆', initial: 'C' },
  { name: '厦门', initial: 'X' },
  { name: '三亚', initial: 'S' },
  { name: '青岛', initial: 'Q' },
  { name: '大理', initial: 'D' },
  { name: '丽江', initial: 'L' },
  { name: '西安', initial: 'X' },
  { name: '苏州', initial: 'S' },
  { name: '长沙', initial: 'C' },
  { name: '武汉', initial: 'W' },
  { name: '南京', initial: 'N' },
  { name: '珠海', initial: 'Z' },
  { name: '香港', initial: 'X' },
  { name: '东京', initial: 'D' },
  { name: '京都', initial: 'J' },
]

export const HOT_CITIES = ['杭州', '上海', '成都', '三亚', '厦门', '大理']
export const CITY_LETTERS = ['全部', 'B', 'C', 'D', 'G', 'H', 'J', 'L', 'N', 'Q', 'S', 'W', 'X', 'Z']

export function filterCities(cities: CityItem[], keyword: string, initial: string) {
  const key = keyword.trim()
  return cities.filter(item =>
    (!key || item.name.includes(key))
    && (initial === '全部' || item.initial === initial),
  )
}

export function stayNights(start: string, end: string) {
  if (!start || !end)
    return 0
  return Math.max(1, dayjs(end).diff(dayjs(start), 'day'))
}

export function formatDateTrigger(start: string, end: string) {
  const startText = dayjs(start).format('M月D日')
  if (!end)
    return `${startText} - 请选择离店`
  return `${startText} - ${dayjs(end).format('M月D日')} · ${stayNights(start, end)}晚`
}

export function overlayTitle(kind: OverlayKind) {
  if (kind === 'city')
    return '选择目的地'
  if (kind === 'date')
    return '选择入住离店日期'
  return '快速筛选'
}

export function canConfirmStay(start: string, end: string) {
  return Boolean(start && end)
}
