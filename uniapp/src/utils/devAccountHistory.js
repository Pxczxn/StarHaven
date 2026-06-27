const HISTORY_KEY = 'tenantLoginHistory';

export const getDevAccountHistory = () => {
  try {
    return JSON.parse(localStorage.getItem(HISTORY_KEY) || '[]');
  } catch (error) {
    return [];
  }
};

export const saveDevAccountHistory = (account) => {
  if (!account?.phone || !account?.password) {
    return;
  }

  const history = getDevAccountHistory().filter((item) => item.phone !== account.phone);
  const nextHistory = [
    {
      realName: account.realName || account.phone,
      phone: account.phone,
      password: account.password,
      idNumber: account.idNumber || '',
      savedAt: Date.now(),
    },
    ...history,
  ].slice(0, 8);

  localStorage.setItem(HISTORY_KEY, JSON.stringify(nextHistory));
  window.dispatchEvent(new Event('tenant-history-change'));
};

export const clearDevAccountHistory = () => {
  localStorage.removeItem(HISTORY_KEY);
  window.dispatchEvent(new Event('tenant-history-change'));
};

export const generateDevTenantAccount = () => {
  const surnames = ['张', '王', '李', '赵', '刘', '陈', '杨', '黄', '周', '吴'];
  const names = ['星河', '云栖', '晨光', '晓月', '安然', '若尘', '南星', '知夏', '景行', '清欢'];
  const phonePrefix = ['130', '131', '132', '133', '135', '136', '137', '138', '150', '151', '152', '157', '158', '180', '181', '186', '187', '188'];
  const realName = surnames[Math.floor(Math.random() * surnames.length)] + names[Math.floor(Math.random() * names.length)];
  const phone = phonePrefix[Math.floor(Math.random() * phonePrefix.length)] +
    Math.floor(Math.random() * 100000000).toString().padStart(8, '0');
  const password = `dev${Math.floor(Math.random() * 1000000).toString().padStart(6, '0')}`;
  const idNumber = generateIdNumber();

  return {
    realName,
    phone,
    password,
    confirmPassword: password,
    idNumber,
  };
};

const generateIdNumber = () => {
  const areas = ['110101', '310101', '320102', '330102', '440103', '420102', '510104', '610102'];
  const area = areas[Math.floor(Math.random() * areas.length)];
  const year = 1985 + Math.floor(Math.random() * 24);
  const month = String(1 + Math.floor(Math.random() * 12)).padStart(2, '0');
  const day = String(1 + Math.floor(Math.random() * 28)).padStart(2, '0');
  const sequence = String(1 + Math.floor(Math.random() * 998)).padStart(3, '0');
  const body = `${area}${year}${month}${day}${sequence}`;
  const weights = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
  const checkCodes = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'];
  const sum = body.split('').reduce((total, digit, index) => total + Number(digit) * weights[index], 0);
  return `${body}${checkCodes[sum % 11]}`;
};
