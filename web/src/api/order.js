import request from './request';

/**
 * 订单 API
 */

// 分页查询订单
export const getOrders = (params) => {
  return request.get('/orders', { params });
};

// 根据ID查询订单
export const getOrderById = (id) => {
  return request.get(`/orders/${id}`);
};

// 创建订单
export const createOrder = (data) => {
  return request.post('/orders', data);
};

// 更新订单
export const updateOrder = (id, data) => {
  return request.put(`/orders/${id}`, data);
};

// 确认订单
export const confirmOrder = (id) => {
  return request.put(`/orders/${id}/confirm`);
};

// 办理入住
export const checkInOrder = (id) => {
  return request.put(`/orders/${id}/check-in`);
};

// 办理退房
export const checkOutOrder = (id) => {
  return request.put(`/orders/${id}/check-out`);
};

// 取消订单
export const cancelOrder = (id, data) => {
  return request.put(`/orders/${id}/cancel`, data);
};

// 记录收款
export const recordPayment = (id, data) => {
  return request.post(`/orders/${id}/payment`, data);
};

// 删除订单
export const deleteOrder = (id) => {
  return request.delete(`/orders/${id}`);
};
