import request from './request';

export const getDashboardSummary = () => {
  return request.get('/dashboard/summary');
};

export const getOrderTrend = () => {
  return request.get('/dashboard/order-trend');
};

export const getRevenueTrend = () => {
  return request.get('/dashboard/revenue-trend');
};

export const getRoomStatus = () => {
  return request.get('/dashboard/room-status');
};
