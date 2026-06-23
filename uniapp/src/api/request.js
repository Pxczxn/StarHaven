import axios from 'axios';

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data;

    // 成功响应
    if (res.code === 200) {
      return res.data;
    }

    // 业务错误
    return Promise.reject(new Error(res.message || '请求失败'));
  },
  (error) => {
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

export default request;
