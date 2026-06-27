import { useEffect, useState } from 'react';
import { Link, NavLink, useLocation } from 'react-router-dom';
import { getTenantUser } from '../utils/tenantAuth';
import './UserNavbar.css';

const navItems = [
  { to: '/', label: '首页', end: true },
  { to: '/rooms', label: '预订房间' },
  { to: '/orders/query', label: '订单查询' },
  { to: '/contact', label: '联系我们' },
];

function UserNavbar() {
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
    <header className="user-navbar">
      <Link to="/" className="user-navbar-brand">
        <span className="brand-mark">星</span>
        <span className="brand-text">星栖民宿</span>
      </Link>

      <nav className="user-navbar-links" aria-label="用户端导航">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            className={({ isActive }) => `user-nav-link ${isActive ? 'active' : ''}`}
          >
            {item.label}
          </NavLink>
        ))}
      </nav>

      <div className="user-navbar-account">
        {user ? (
          <Link to="/profile" className="user-account-pill signed">
            <span className="user-avatar">{(user.realName || user.phone || '星').slice(0, 1)}</span>
            <span>{user.realName || user.phone}</span>
          </Link>
        ) : (
          <Link to="/login" className="user-account-pill">
            登录 / 注册
          </Link>
        )}
      </div>
    </header>
  );
}

export default UserNavbar;
