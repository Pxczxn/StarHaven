import request from './request';

/**
 * 登录
 */
export const login = (data) => {
  return request.post('/auth/login', data);
};

/**
 * 获取当前用户信息
 */
export const getCurrentUser = () => {
  return request.get('/auth/profile');
};

/**
 * 退出登录
 */
export const logout = () => {
  return request.post('/auth/logout');
};
