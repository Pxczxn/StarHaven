export const getTenantUser = () => {
  try {
    return JSON.parse(localStorage.getItem('tenantUser') || 'null');
  } catch (error) {
    return null;
  }
};

export const getTenantToken = () => localStorage.getItem('tenantToken');

export const setTenantAuth = ({ token, user }) => {
  localStorage.setItem('tenantToken', token);
  localStorage.setItem('tenantUser', JSON.stringify(user));
  window.dispatchEvent(new Event('tenant-auth-change'));
};

export const clearTenantAuth = () => {
  localStorage.removeItem('tenantToken');
  localStorage.removeItem('tenantUser');
  window.dispatchEvent(new Event('tenant-auth-change'));
};
