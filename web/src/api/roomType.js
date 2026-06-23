import request from './request';

/**
 * 获取房型列表
 */
export const getRoomTypes = (params) => {
  return request.get('/room-types', { params });
};

/**
 * 获取房型详情
 */
export const getRoomTypeById = (id) => {
  return request.get(`/room-types/${id}`);
};

/**
 * 创建房型
 */
export const createRoomType = (data) => {
  return request.post('/room-types', data);
};

/**
 * 更新房型
 */
export const updateRoomType = (id, data) => {
  return request.put(`/room-types/${id}`, data);
};

/**
 * 删除房型
 */
export const deleteRoomType = (id) => {
  return request.delete(`/room-types/${id}`);
};
