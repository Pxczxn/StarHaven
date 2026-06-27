import { useEffect, useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { loginTenant } from '../../api/auth';
import { setTenantAuth } from '../../utils/tenantAuth';
import { clearDevAccountHistory, getDevAccountHistory, saveDevAccountHistory } from '../../utils/devAccountHistory';
import './index.css';

function Login() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [formData, setFormData] = useState({ phone: '', password: '' });
  const [history, setHistory] = useState(getDevAccountHistory());
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  useEffect(() => {
    const syncHistory = () => setHistory(getDevAccountHistory());
    window.addEventListener('tenant-history-change', syncHistory);
    window.addEventListener('storage', syncHistory);
    return () => {
      window.removeEventListener('tenant-history-change', syncHistory);
      window.removeEventListener('storage', syncHistory);
    };
  }, []);

  const handleChange = (field, value) => {
    if (field === 'phone') {
      value = value.replace(/\D/g, '').slice(0, 11);
    }
    setFormData({ ...formData, [field]: value });
    setError('');
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!/^1[3-9]\d{9}$/.test(formData.phone)) {
      setError('请输入正确的手机号');
      return;
    }
    if (!formData.password) {
      setError('请输入密码');
      return;
    }

    try {
      setSubmitting(true);
      const data = await loginTenant(formData);
      saveDevAccountHistory({
        realName: data.user?.realName,
        phone: formData.phone,
        password: formData.password,
        idNumber: data.user?.idNumber,
      });
      setTenantAuth(data);
      navigate(searchParams.get('redirect') || '/profile');
    } catch (err) {
      setError(err.message || '登录失败，请重试');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card cosmic-ui-card">
        <Link to="/" className="auth-home-pill">← 返回主页</Link>
        <div className="auth-kicker">TENANT LOGIN</div>
        <h1 className="auth-title">租客登录</h1>
        <p className="auth-subtitle">登录后可快速填写预订信息，并用手机号查看自己的订单。</p>

        {history.length > 0 && (
          <div className="dev-history-panel">
            <div className="dev-history-head">
              <span>历史开发账号</span>
              <button type="button" onClick={clearDevAccountHistory}>清空</button>
            </div>
            <div className="dev-history-list">
              {history.map((account) => (
                <button
                  key={account.phone}
                  type="button"
                  className="dev-history-item"
                  onClick={() => {
                    setFormData({ phone: account.phone, password: account.password });
                    setError('');
                  }}
                >
                  <strong>{account.realName || account.phone}</strong>
                  <span>{account.phone}</span>
                </button>
              ))}
            </div>
          </div>
        )}

        <form className="auth-form" onSubmit={handleSubmit}>
          <div className="auth-field">
            <label>手机号</label>
            <input
              type="tel"
              inputMode="numeric"
              value={formData.phone}
              onChange={(e) => handleChange('phone', e.target.value)}
              placeholder="请输入 11 位手机号"
            />
          </div>
          <div className="auth-field">
            <label>密码</label>
            <div className="password-field">
              <input
                type={showPassword ? 'text' : 'password'}
                value={formData.password}
                onChange={(e) => handleChange('password', e.target.value)}
                placeholder="请输入密码"
              />
              <button type="button" className="password-toggle-btn" aria-label={showPassword ? '隐藏密码' : '显示密码'} onClick={() => setShowPassword((visible) => !visible)}>
                {showPassword ? '◉' : '◎'}
              </button>
            </div>
          </div>
          <div className="auth-error">{error}</div>
          <button className="cosmic-btn-primary auth-submit" disabled={submitting}>
            {submitting ? '登录中...' : '登录'}
          </button>
        </form>

        <div className="auth-switch">
          还没有账号？ <Link to="/register">立即注册</Link>
        </div>
      </div>
    </div>
  );
}

export default Login;
