import request from './request';

/**
 * 获取房间列表
 */
export const getRooms = (params) => {
  return request.get('/rooms', { params });
};

export const getAllRooms = () => {
  return request.get('/rooms/all');
};

/**
 * 获取房间详情
 */
export const getRoomById = (id) => {
  return request.get(`/rooms/${id}`);
};

/**
 * 创建房间
 */
export const createRoom = (data) => {
  return request.post('/rooms', data);
};

/**
 * 更新房间
 */
export const updateRoom = (id, data) => {
  return request.put(`/rooms/${id}`, data);
};

/**
 * 修改房间状态
 */
export const updateRoomStatus = (id, data) => {
  const status = typeof data === 'string' ? data : data?.status;
  return request.put(`/rooms/${id}/status`, null, { params: { status } });
};

/**
 * 删除房间
 */
export const deleteRoom = (id) => {
  return request.delete(`/rooms/${id}`);
};
