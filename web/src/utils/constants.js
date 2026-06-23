/**
 * 房间状态
 */
export const ROOM_STATUS = {
  available: '空闲',
  reserved: '已预订',
  occupied: '已入住',
  cleaning: '清洁中',
  maintenance: '维修中',
  disabled: '停用',
};

/**
 * 房间状态颜色
 */
export const ROOM_STATUS_COLOR = {
  available: 'success',
  reserved: 'processing',
  occupied: 'warning',
  cleaning: 'default',
  maintenance: 'error',
  disabled: 'default',
};

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
  pending: 'warning',
  reserved: 'processing',
  occupied: 'success',
  checked_out: 'default',
  completed: 'default',
  cancelled: 'error',
  refunded: 'default',
};

/**
 * 用户角色
 */
export const USER_ROLE = {
  admin: '管理员',
  landlord: '房东',
  tenant: '租客',
};

/**
 * 支付方式
 */
export const PAYMENT_METHOD = {
  cash: '现金',
  wechat: '微信',
  alipay: '支付宝',
  bank_card: '银行卡',
  other: '其他',
};

/**
 * 订单来源
 */
export const ORDER_SOURCE = {
  front_desk: '前台',
  phone: '电话',
  wechat: '微信',
  ctrip: '携程',
  meituan: '美团',
  uniapp: 'UniApp',
  other: '其他',
};

/**
 * 任务类型
 */
export const TASK_TYPE = {
  cleaning: '清洁',
  maintenance: '维修',
  note: '备注',
};

/**
 * 任务状态
 */
export const TASK_STATUS = {
  pending: '待处理',
  processing: '处理中',
  completed: '已完成',
};
