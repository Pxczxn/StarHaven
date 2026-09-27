import type { GlobalThemeOverrides } from 'naive-ui'

export const themeOverrides: GlobalThemeOverrides = {
  common: {
    primaryColor: '#2878FF',
    primaryColorHover: '#4A8FFF',
    primaryColorPressed: '#1E66E6',
    primaryColorSuppl: '#2878FF',
    borderRadius: '8px',
    fontFamily: '"Segoe UI", "PingFang SC", "Microsoft YaHei", sans-serif',
  },
  Layout: {
    siderColor: '#0F172A',
    headerColor: '#FFFFFF',
    color: '#F6F8FB',
  },
  Menu: {
    itemTextColor: 'rgba(255,255,255,0.72)',
    itemTextColorHover: '#FFFFFF',
    itemTextColorActive: '#FFFFFF',
    itemTextColorActiveHover: '#FFFFFF',
    itemIconColor: 'rgba(255,255,255,0.72)',
    itemIconColorHover: '#FFFFFF',
    itemIconColorActive: '#FFFFFF',
    itemColorActive: '#2878FF',
    itemColorHover: 'rgba(255,255,255,0.08)',
    itemColorActiveHover: '#2878FF',
  },
  Card: {
    borderRadius: '12px',
  },
  DataTable: {
    thColor: '#F8FAFC',
    borderRadius: '12px',
  },
}
