import request from './request';

/**
 * 创建订单
 */
export const createOrder = (data) => {
  return request.post('/public/orders', data);
};

/**
 * 查询订单
 */
export const queryOrder = (params) => {
  return request.get('/public/orders/query', { params });
};

/**
 * 获取订单详情
 */
export const getOrderByNo = (orderNo, params) => {
  return request.get(`/public/orders/${orderNo}`, { params });
};
