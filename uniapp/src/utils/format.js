import dayjs from 'dayjs';

/**
 * 格式化日期
 */
export const formatDate = (date, format = 'YYYY-MM-DD') => {
  if (!date) return '';
  return dayjs(date).format(format);
};

/**
 * 格式化日期时间
 */
export const formatDateTime = (date) => {
  return formatDate(date, 'YYYY-MM-DD HH:mm:ss');
};

/**
 * 格式化金额
 */
export const formatMoney = (amount) => {
  if (amount === null || amount === undefined) return '¥0.00';
  return `¥${Number(amount).toFixed(2)}`;
};

/**
 * 格式化手机号（中间4位隐藏）
 */
export const formatPhone = (phone) => {
  if (!phone) return '';
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
};

/**
 * 计算天数差
 */
export const getDaysDiff = (startDate, endDate) => {
  const start = dayjs(startDate);
  const end = dayjs(endDate);
  return end.diff(start, 'day');
};
