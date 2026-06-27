import { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { getTenantUser } from '../utils/tenantAuth';
import './TenantAccountBar.css';

function TenantAccountBar() {
  const [user, setUser] = useState(getTenantUser());
  const location = useLocation();

  useEffect(() => {
    const syncUser = () => setUser(getTenantUser());
    syncUser();
    window.addEventListener('tenant-auth-change', syncUser);
    window.addEventListener('storage', syncUser);
    return () => {
      window.removeEventListener('tenant-auth-change', syncUser);
      window.removeEventListener('storage', syncUser);
    };
  }, [location.pathname]);

  if (location.pathname === '/login' || location.pathname === '/register') {
    return null;
  }

  return (
    <div className="tenant-account-bar">
      {user ? (
        <Link to="/profile" className="tenant-account-pill signed">
          <span className="tenant-avatar">{(user.realName || user.phone || '星').slice(0, 1)}</span>
          <span>{user.realName || user.phone}</span>
        </Link>
      ) : (
        <Link to="/login" className="tenant-account-pill">
          <span>登录 / 注册</span>
        </Link>
      )}
    </div>
  );
}

export default TenantAccountBar;
