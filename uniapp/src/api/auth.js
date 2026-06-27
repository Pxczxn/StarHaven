import request from './request';

export const registerTenant = (data) => {
  return request.post('/tenant/auth/register', data);
};

export const sendRegisterCode = (data) => {
  return request.post('/tenant/auth/sms-code', data);
};

export const loginTenant = (data) => {
  return request.post('/tenant/auth/login', data);
};

export const getTenantProfile = () => {
  return request.get('/tenant/auth/profile');
};

export const verifyTenant = (data) => {
  return request.put('/tenant/auth/verify', data);
};

export const logoutTenant = () => {
  return request.post('/tenant/auth/logout');
};
