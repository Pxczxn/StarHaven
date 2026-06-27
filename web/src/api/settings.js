import request from './request';

export const getSiteSetting = () => request.get('/settings/site');

export const updateSiteSetting = (data) => request.put('/settings/site', data);
