import request from './request';

export const getOrders = (params) => {
  return request.get('/orders', { params });
};

export const getOrderById = (id) => {
  return request.get(`/orders/${id}`);
};

export const createOrder = (data) => {
  return request.post('/orders', data);
};

export const updateOrder = (id, data) => {
  return request.put(`/orders/${id}`, data);
};

export const deleteOrder = (id) => {
  return request.delete(`/orders/${id}`);
};

export const confirmOrder = (id) => {
  return request.put(`/orders/${id}/confirm`);
};

export const checkInOrder = (id) => {
  return request.put(`/orders/${id}/check-in`);
};

export const checkOutOrder = (id) => {
  return request.put(`/orders/${id}/check-out`);
};

export const cancelOrder = (id, data) => {
  return request.put(`/orders/${id}/cancel`, data);
};

export const paymentOrder = (id, data) => {
  return request.put(`/orders/${id}/payment`, data);
};
