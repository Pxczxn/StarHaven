import { beforeEach, describe, expect, it, vi } from 'vitest'

/**
 * store.ts 的 userRoles 是模块级 computed，会缓存对某个 pinia 实例的依赖；
 * test-setup 每个用例都会换一个新的 pinia，所以这里必须一起重置模块，
 * 否则第二个用例读到的是上一个 pinia 的缓存值。
 */
beforeEach(() => {
  vi.resetModules()
})

async function invoke(url: string, userInfo?: { roles?: string[] }) {
  if (userInfo) {
    const { useUserStore } = await import('@/store/user')
    useUserStore().setUserInfo({ userId: 1, username: 'u', nickname: 'U', ...userInfo })
  }
  const { navigateToInterceptor } = await import('./interceptor')
  return navigateToInterceptor.invoke({ url })
}

// 当前业务 tabbar 未配置 roles；受限 tab 场景见下方 mock 用例
describe('navigateToInterceptor 角色守卫', () => {
  it('非 tab 页不受角色守卫限制', async () => {
    const result = await invoke('/pages/about/about')

    expect(result).not.toBe(false)
    expect(uni.reLaunch).not.toHaveBeenCalled()
  })

  it('角色满足时放行', async () => {
    const result = await invoke('/pages/about/about', { roles: ['admin'] })

    expect(result).not.toBe(false)
    expect(uni.reLaunch).not.toHaveBeenCalled()
  })

  it('未配置 roles 的 tabbar 页放行', async () => {
    const result = await invoke('/pages/me/me')

    expect(result).not.toBe(false)
    expect(uni.reLaunch).not.toHaveBeenCalled()
  })

  it('首页未被角色限制时，首屏 "/" 放行', async () => {
    const result = await invoke('/')

    expect(result).not.toBe(false)
    expect(uni.reLaunch).not.toHaveBeenCalled()
  })

  it('当前 tabbar 项均无 roles 时，不会因角色回退', async () => {
    const { getTabbarRedirectPath } = await import('@/tabbar/store')

    expect(getTabbarRedirectPath('/pages/about/about')).toBe('')
    expect(getTabbarRedirectPath('/pages/index/index')).toBe('')
    expect(getTabbarRedirectPath('/pages/me/me')).toBe('')
  })

  it('url 为 undefined 时直接返回，不做任何跳转', async () => {
    await invoke(undefined as unknown as string)

    expect(uni.reLaunch).not.toHaveBeenCalled()
  })
})

describe('navigateToInterceptor 登录黑名单', () => {
  it('未登录进入预订页时拦截', async () => {
    const result = await invoke('/pages/booking/booking?houseId=1')

    expect(result).toBe(false)
  })

  it('未登录进入消息页时拦截', async () => {
    const result = await invoke('/pages/messages/messages')

    expect(result).toBe(false)
  })

  it('未登录进入优惠券页时拦截', async () => {
    const result = await invoke('/pages/coupons/coupons')

    expect(result).toBe(false)
  })

  it('未登录进入 Tab 页时放行', async () => {
    const result = await invoke('/pages/favorites/favorites')

    expect(result).not.toBe(false)
    expect(uni.reLaunch).not.toHaveBeenCalled()
  })

  it('已登录进入预订页时放行', async () => {
    uni.getStorageSync.mockImplementation((key: string) => {
      if (key === 'accessTokenExpireTime')
        return Date.now() + 8 * 3600 * 1000
      return null
    })
    const { useTokenStore } = await import('@/store/token')
    useTokenStore().setTokenInfo({ token: 'test-token', expiresIn: 7200 })
    const { navigateToInterceptor } = await import('./interceptor')
    const result = navigateToInterceptor.invoke({ url: '/pages/booking/booking?houseId=1' })

    expect(result).not.toBe(false)
  })
})

describe('navigateToInterceptor 首页就是受限页（issue #454 的核心场景）', () => {
  // 把首页配成 admin 专属，复现「没有 role 却因为它是 home 而直接进入」
  beforeEach(() => {
    vi.doMock('@/tabbar/config', () => ({
      TABBAR_STRATEGY_MAP: { NO_TABBAR: 0, NATIVE_TABBAR: 1, CUSTOM_TABBAR: 2 },
      selectedTabbarStrategy: 2,
      tabbarList: [
        { text: '首页', pagePath: 'pages/index/index', iconType: 'unocss', icon: 'i-carbon-home', roles: ['admin'] },
        { text: '我的', pagePath: 'pages/me/me', iconType: 'unocss', icon: 'i-carbon-user' },
      ],
    }))
  })

  it('冷启动落在受限首页时被拦截，回退到可见的 tab', async () => {
    // App.vue onShow 首次进入时就是这样调用的：没有 options.path 时传 '/'
    const result = await invoke('/')

    expect(result).toBe(false)
    expect(uni.reLaunch).toHaveBeenCalledWith({ url: '/pages/me/me' })
  })

  it('冷启动带上具体首页路径时同样被拦截', async () => {
    const result = await invoke('/pages/index/index')

    expect(result).toBe(false)
    expect(uni.reLaunch).toHaveBeenCalledWith({ url: '/pages/me/me' })
  })

  it('角色满足时冷启动放行', async () => {
    const result = await invoke('/', { roles: ['admin'] })

    expect(result).not.toBe(false)
    expect(uni.reLaunch).not.toHaveBeenCalled()
  })
})
