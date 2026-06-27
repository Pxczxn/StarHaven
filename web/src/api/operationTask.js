import request from './request';

export const getOperationTasks = (params) => request.get('/operation-tasks', { params });

export const createOperationTask = (data) => request.post('/operation-tasks', data);

export const updateOperationTask = (id, data) => request.put(`/operation-tasks/${id}`, data);

export const updateOperationTaskStatus = (id, status) => request.put(`/operation-tasks/${id}/status`, null, { params: { status } });

export const deleteOperationTask = (id) => request.delete(`/operation-tasks/${id}`);
