/**
 * 订单状态
 */
export const ORDER_STATUS = {
  pending: '待确认',
  reserved: '已预订',
  occupied: '已入住',
  checked_out: '已退房',
  completed: '已完成',
  cancelled: '已取消',
  refunded: '已退款',
};

/**
 * 订单状态颜色
 */
export const ORDER_STATUS_COLOR = {
  pending: '#faad14',
  reserved: '#1890ff',
  occupied: '#52c41a',
  checked_out: '#8c8c8c',
  completed: '#8c8c8c',
  cancelled: '#ff4d4f',
  refunded: '#722ed1',
};

/**
 * 支付状态
 */
export const PAYMENT_STATUS = {
  unpaid: '未支付',
  partial: '部分支付',
  paid: '已支付',
  refunded: '已退款',
};

/**
 * 支付状态颜色
 */
export const PAYMENT_STATUS_COLOR = {
  unpaid: '#ff4d4f',
  partial: '#faad14',
  paid: '#52c41a',
  refunded: '#722ed1',
};
