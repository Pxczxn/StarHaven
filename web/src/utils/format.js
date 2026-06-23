import dayjs from 'dayjs';

/**
 * 格式化日期时间
 */
export const formatDateTime = (dateTime) => {
  if (!dateTime) return '-';
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss');
};

/**
 * 格式化日期
 */
export const formatDate = (date) => {
  if (!date) return '-';
  return dayjs(date).format('YYYY-MM-DD');
};

/**
 * 格式化金额
 */
export const formatMoney = (amount) => {
  if (amount === null || amount === undefined) return '¥0.00';
  return `¥${Number(amount).toFixed(2)}`;
};

/**
 * 格式化手机号（部分隐藏）
 */
export const formatPhone = (phone) => {
  if (!phone) return '-';
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
};

/**
 * 格式化证件号（部分隐藏）
 */
export const formatIdNumber = (idNumber) => {
  if (!idNumber) return '-';
  const len = idNumber.length;
  if (len <= 6) return idNumber;
  return idNumber.substring(0, 3) + '****' + idNumber.substring(len - 4);
};
