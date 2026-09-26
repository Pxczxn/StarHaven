import { describe, expect, it } from 'vitest'
import { canConfirmStay, filterCities, formatDateTrigger, overlayTitle, stayNights } from './homeStay'

const cities = [
  { name: '杭州', initial: 'H' },
  { name: '上海', initial: 'S' },
  { name: '成都', initial: 'C' },
]

describe('filterCities', () => {
  it('按关键词过滤城市名', () => {
    expect(filterCities(cities, '杭', '全部').map(item => item.name)).toEqual(['杭州'])
  })

  it('按首字母过滤', () => {
    expect(filterCities(cities, '', 'S').map(item => item.name)).toEqual(['上海'])
  })

  it('无匹配时返回空列表', () => {
    expect(filterCities(cities, '北极', '全部')).toEqual([])
  })
})

describe('stayNights', () => {
  it('晚数等于离店减入住的日差', () => {
    expect(stayNights('2026-09-01', '2026-09-10')).toBe(9)
  })

  it('缺少离店时为 0', () => {
    expect(stayNights('2026-09-01', '')).toBe(0)
  })
})

describe('formatDateTrigger', () => {
  it('完整区间显示 M月D日 - M月D日 · N晚', () => {
    expect(formatDateTrigger('2026-09-01', '2026-09-10')).toBe('9月1日 - 9月10日 · 9晚')
  })

  it('未选离店时提示请选择离店', () => {
    expect(formatDateTrigger('2026-09-01', '')).toBe('9月1日 - 请选择离店')
  })
})

describe('overlayTitle', () => {
  it('城市面板标题为选择目的地', () => {
    expect(overlayTitle('city')).toBe('选择目的地')
  })

  it('日期面板标题为选择入住离店日期', () => {
    expect(overlayTitle('date')).toBe('选择入住离店日期')
  })
})

describe('canConfirmStay', () => {
  it('起止都有才能确认', () => {
    expect(canConfirmStay('2026-09-01', '2026-09-10')).toBe(true)
    expect(canConfirmStay('2026-09-01', '')).toBe(false)
  })
})
