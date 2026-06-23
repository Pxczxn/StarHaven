import axios from 'axios';

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { data } = response;

    // 统一处理响应
    if (data.code === 200) {
      return data;
    } else {
      // 业务错误
      return Promise.reject(new Error(data.message || '请求失败'));
    }
  },
  (error) => {
    // HTTP 错误
    if (error.response) {
      const { status, data } = error.response;

      if (status === 401) {
        // 未登录或 token 过期
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
        return Promise.reject(new Error('登录已过期，请重新登录'));
      } else if (status === 403) {
        return Promise.reject(new Error(data.message || '无权限访问'));
      } else if (status === 404) {
        return Promise.reject(new Error(data.message || '资源不存在'));
      } else if (status === 500) {
        return Promise.reject(new Error(data.message || '服务器内部错误'));
      } else {
        return Promise.reject(new Error(data.message || '请求失败'));
      }
    } else if (error.request) {
      return Promise.reject(new Error('网络连接失败'));
    } else {
      return Promise.reject(error);
    }
  }
);

export default request;
