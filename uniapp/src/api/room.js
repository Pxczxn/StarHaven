import request from './request';

/**
 * 获取房型列表
 */
export const getRoomTypes = () => {
  return request.get('/public/room-types');
};

/**
 * 获取房型详情
 */
export const getRoomTypeById = (id) => {
  return request.get(`/public/room-types/${id}`);
};

/**
 * 获取房间详情
 */
export const getRoomById = (id, params) => {
  return request.get('/public/available-rooms', { params }).then(rooms => {
    return rooms.find(room => room.id === parseInt(id));
  });
};

/**
 * 查询可订房间
 */
export const getAvailableRooms = (params) => {
  return request.get('/public/available-rooms', { params });
};
