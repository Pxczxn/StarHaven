import request from './request';

export const getUsers = (params) => request.get('/users', { params });

export const createUser = (data) => request.post('/users', data);

export const updateUser = (id, data) => request.put(`/users/${id}`, data);

export const updateUserPassword = (id, password) => request.put(`/users/${id}/password`, { password });

export const updateUserStatus = (id, status) => request.put(`/users/${id}/status`, null, { params: { status } });

export const deleteUser = (id) => request.delete(`/users/${id}`);
