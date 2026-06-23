// 订单状态
export const ORDER_STATUS = {
  pending: '待确认',
  reserved: '已预订',
  occupied: '已入住',
  completed: '已完成',
  cancelled: '已取消',
  refunded: '已退款',
};

// 订单来源
export const ORDER_SOURCE = {
  front_desk: '前台',
  phone: '电话',
  wechat: '微信',
  ctrip: '携程',
  meituan: '美团',
  h5: 'H5',
  other: '其他',
};

// 支付方式
export const PAYMENT_METHOD = {
  cash: '现金',
  wechat: '微信',
  alipay: '支付宝',
  bank_card: '银行卡',
  other: '其他',
};
