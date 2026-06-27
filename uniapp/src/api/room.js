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
/**
 * 获取房间详情（通过查询可订房间）
 */
export const getRoomById = (id, params) => {
  // 如果没有传递日期参数，使用默认日期
  const queryParams = params || {
    checkInDate: new Date().toISOString().split('T')[0],
    checkOutDate: new Date(Date.now() + 86400000).toISOString().split('T')[0],
  };

  return request.get('/public/available-rooms', { params: queryParams }).then(rooms => {
    return rooms.find(room => room.id === parseInt(id));
  });
};

/**
 * 查询可订房间
 */
export const getAvailableRooms = (params) => {
  return request.get('/public/available-rooms', { params });
};
