import { Navigate, useLocation } from 'react-router-dom';
import { getTenantToken, getTenantUser } from '../utils/tenantAuth';

const isTenantAuthenticated = () => Boolean(getTenantToken() && getTenantUser());

export function TenantRoute({ children }) {
  const location = useLocation();
  if (!isTenantAuthenticated()) {
    const redirect = encodeURIComponent(`${location.pathname}${location.search}`);
    return <Navigate to={`/login?redirect=${redirect}`} replace />;
  }
  return children;
}

export function GuestRoute({ children }) {
  const location = useLocation();
  if (isTenantAuthenticated()) {
    const redirect = new URLSearchParams(location.search).get('redirect');
    return <Navigate to={redirect || '/profile'} replace />;
  }
  return children;
}
