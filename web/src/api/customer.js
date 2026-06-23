import request from './request';

/**
 * 获取客户列表
 */
export const getCustomers = (params) => {
  return request.get('/customers', { params });
};

/**
 * 获取客户详情
 */
export const getCustomerById = (id) => {
  return request.get(`/customers/${id}`);
};

/**
 * 创建客户
 */
export const createCustomer = (data) => {
  return request.post('/customers', data);
};

/**
 * 更新客户
 */
export const updateCustomer = (id, data) => {
  return request.put(`/customers/${id}`, data);
};

/**
 * 删除客户
 */
export const deleteCustomer = (id) => {
  return request.delete(`/customers/${id}`);
};

/**
 * 获取客户的历史订单
 */
export const getCustomerOrders = (id) => {
  return request.get(`/customers/${id}/orders`);
};
