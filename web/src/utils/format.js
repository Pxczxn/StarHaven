export const formatMoney = (amount) => {
  if (amount === null || amount === undefined) return '¥0.00';
  return `¥${Number(amount).toFixed(2)}`;
};

export const formatDateTime = (dateTime) => {
  if (!dateTime) return '-';
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });
};

export const formatDate = (date) => {
  if (!date) return '-';
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });
};

export const formatPhone = (phone) => {
  if (!phone) return '-';
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
};

export const formatIdNumber = (idNumber) => {
  if (!idNumber) return '-';
  if (idNumber.length === 18) {
    return idNumber.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2');
  }
  return idNumber;
};
