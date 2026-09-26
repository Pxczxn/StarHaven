/**
 * by 菲鸽 on 2025-08-19
 * 路由拦截，通常也是登录拦截
 */
import { getTabbarRedirectPath, tabbarStore } from '@/tabbar/store'
import { useTokenStore } from '@/store/token'
import { getLastPage, parseUrlToObj } from '@/utils/index'
import { toLoginPage } from '@/utils/toLoginPage'

export const FG_LOG_ENABLE = false

export const LOGIN_PAGE = '/pages/login/index'

/** Tab 页不进黑名单，避免 switchTab 预加载把用户直接踢去登录页。 */
export const LOGIN_REQUIRED_PATH_LIST = [
  '/pages/booking/booking',
  '/pages/order-confirm/order-confirm',
  '/pages/payment/payment',
  '/pages/order-detail/order-detail',
  '/pages/coupons/coupons',
  '/pages/messages/messages',
]

export function isLoginRequiredPath(path: string) {
  const normalized = path.startsWith('/') ? path : `/${path}`
  return LOGIN_REQUIRED_PATH_LIST.some(item => normalized === item || normalized.startsWith(`${item}?`))
}

export const navigateToInterceptor = {
  // 注意，这里的url是 '/' 开头的，如 '/pages/index/index'，跟 'pages.json' 里面的 path 不同
  // 增加对相对路径的处理，BY 网友 @ideal
  invoke({ url, query }: { url: string, query?: Record<string, string> }) {
    if (url === undefined) {
      return
    }
    let { path, query: _query } = parseUrlToObj(url)

    FG_LOG_ENABLE && console.log('\n\n路由拦截器:-------------------------------------')
    FG_LOG_ENABLE && console.log('路由拦截器 1: url->', url, ', query ->', query)
    const myQuery = { ..._query, ...query }
    // /pages/route-interceptor/index?name=feige&age=30
    FG_LOG_ENABLE && console.log('路由拦截器 2: path->', path, ', _query ->', _query)
    FG_LOG_ENABLE && console.log('路由拦截器 3: myQuery ->', myQuery)

    // 处理相对路径
    if (!path.startsWith('/')) {
      const currentPath = getLastPage()?.route || ''
      const normalizedCurrentPath = currentPath.startsWith('/') ? currentPath : `/${currentPath}`
      const baseDir = normalizedCurrentPath.substring(0, normalizedCurrentPath.lastIndexOf('/'))
      path = `${baseDir}/${path}`
    }

    // // 处理路由不存在的情况
    // if (path !== '/' && !getAllPages().some(page => page.path === path)) {
    //   console.warn('路由不存在:', path)
    //   return false // 明确表示阻止原路由继续执行
    // }

    // // 插件页面
    // if (url.startsWith('plugin://')) {
    //   FG_LOG_ENABLE && console.log('路由拦截器 4: plugin:// 路径 ==>', url)
    //   path = url
    // }

    // tabbar 项配置的 roles 只能隐藏入口，页面本身还是会生成到 pages.json（编译期产物，没法按角色裁剪）。
    // 这里做运行时兜底：直接输入路由、分享进入、或首页恰好是受限页时，都不允许越权进入。
    const redirectPath = getTabbarRedirectPath(path)
    if (redirectPath) {
      FG_LOG_ENABLE && console.log('路由拦截器: 角色不足，拦截 ->', path, '，回退到 ->', redirectPath)
      uni.reLaunch({ url: redirectPath })
      return false
    }

    if (isLoginRequiredPath(path) && path !== LOGIN_PAGE) {
      const tokenStore = useTokenStore()
      if (!tokenStore.updateNowTime().hasLogin) {
        const fullUrl = Object.keys(myQuery).length
          ? `${path}?${Object.entries(myQuery).map(([key, value]) => `${key}=${encodeURIComponent(value)}`).join('&')}`
          : path
        toLoginPage({ queryString: `?redirect=${encodeURIComponent(fullUrl)}` })
        return false
      }
    }

    // 处理直接进入路由非首页时，tabbarIndex 不正确的问题
    tabbarStore.setAutoCurIdx(path)
  },
}

export const routeInterceptor = {
  install() {
    uni.addInterceptor('navigateTo', navigateToInterceptor)
    uni.addInterceptor('reLaunch', navigateToInterceptor)
    uni.addInterceptor('redirectTo', navigateToInterceptor)
    uni.addInterceptor('switchTab', navigateToInterceptor)
  },
}
