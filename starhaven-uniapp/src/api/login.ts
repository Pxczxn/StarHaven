import type { IAuthLoginRes, IUserInfoRes } from './types/login'
import { http } from '@/http/http'

export interface ILoginForm {
  username: string
  password: string
  loginType?: 'PASSWORD' | 'SMS'
  smsCode?: string
}

export function login(loginForm: ILoginForm) {
  return http.post<IAuthLoginRes>('/api/v1/user/login', {
    username: loginForm.username,
    password: loginForm.password,
    loginType: loginForm.loginType || 'PASSWORD',
    smsCode: loginForm.smsCode,
  })
}

export function refreshToken(_refreshToken?: string) {
  return http.post<IAuthLoginRes>('/api/v1/user/refresh')
}

export function getUserInfo() {
  return http.get<IUserInfoRes>('/api/v1/user/info')
}

export function logout() {
  return http.post('/api/v1/user/logout')
}

export function register(data: { username: string, password: string, phone?: string, nickname?: string }) {
  return http.post('/api/v1/user/register', data)
}

export function getWxCode() {
  return new Promise<UniApp.LoginRes>((resolve, reject) => {
    uni.login({
      provider: 'weixin',
      success: res => resolve(res),
      fail: err => reject(new Error(String(err))),
    })
  })
}

export function wxLogin(data: { code: string } | UniApp.LoginRes) {
  const code = typeof data === 'object' && 'code' in data ? data.code : ''
  return http.post<IAuthLoginRes>('/api/v1/user/login', {
    username: 'staruser',
    password: '123456',
    loginType: 'PASSWORD',
    code,
  })
}
