import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { registerTenant, sendRegisterCode } from '../../api/auth';
import { setTenantAuth } from '../../utils/tenantAuth';
import './index.css';

const EyeIcon = ({ hidden }) => (
  <svg viewBox="0 0 24 24" aria-hidden="true">
    <path d="M2.5 12s3.5-6 9.5-6 9.5 6 9.5 6-3.5 6-9.5 6-9.5-6-9.5-6Z" />
    <circle cx="12" cy="12" r="3" />
    {hidden ? <path d="M4 4l16 16" /> : null}
  </svg>
);

function Register() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    realName: '',
    phone: '',
    verificationCode: '',
    password: '',
    confirmPassword: '',
  });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [sendingCode, setSendingCode] = useState(false);
  const [codeCooldown, setCodeCooldown] = useState(0);
  const [showPassword, setShowPassword] = useState(false);

  useEffect(() => {
    if (codeCooldown <= 0) return undefined;
    const timer = window.setTimeout(() => setCodeCooldown((value) => value - 1), 1000);
    return () => window.clearTimeout(timer);
  }, [codeCooldown]);

  const handleChange = (field, value) => {
    if (field === 'phone') {
      value = value.replace(/\D/g, '').slice(0, 11);
    }
    if (field === 'verificationCode') {
      value = value.replace(/\D/g, '').slice(0, 6);
    }
    setFormData({ ...formData, [field]: value });
    setError('');
  };

  const validate = () => {
    if (!formData.realName.trim()) return '请输入姓名';
    if (!/^1[3-9]\d{9}$/.test(formData.phone)) return '请输入正确的手机号';
    if (!/^\d{6}$/.test(formData.verificationCode)) return '请输入 6 位验证码';
    if (formData.password.length < 6) return '密码至少 6 位';
    if (formData.password !== formData.confirmPassword) return '两次输入的密码不一致';
    return '';
  };

  const handleSendCode = async () => {
    if (!/^1[3-9]\d{9}$/.test(formData.phone)) {
      setError('请先输入正确的手机号');
      return;
    }
    try {
      setSendingCode(true);
      const code = await sendRegisterCode({ phone: formData.phone });
      setFormData((current) => ({ ...current, verificationCode: code || current.verificationCode }));
      setCodeCooldown(60);
      setError(code ? `开发验证码已自动填入：${code}` : '验证码已发送');
    } catch (err) {
      setError(err.message || '验证码发送失败，请重试');
    } finally {
      setSendingCode(false);
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const message = validate();
    if (message) {
      setError(message);
      return;
    }

    try {
      setSubmitting(true);
      const data = await registerTenant({
        realName: formData.realName.trim(),
        phone: formData.phone,
        verificationCode: formData.verificationCode,
        password: formData.password,
      });
      setTenantAuth(data);
      navigate('/profile');
    } catch (err) {
      setError(err.message || '注册失败，请重试');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card cosmic-ui-card">
        <Link to="/" className="auth-back">← 返回首页</Link>
        <div className="auth-kicker">CREATE ACCOUNT</div>
        <h1 className="auth-title">租客注册</h1>
        <p className="auth-subtitle">使用手机号创建账号，后续预订和查询订单都会更顺手。</p>

        <form className="auth-form" onSubmit={handleSubmit}>
          <div className="auth-field">
            <label>姓名</label>
            <input value={formData.realName} onChange={(e) => handleChange('realName', e.target.value)} placeholder="请输入姓名" />
          </div>
          <div className="auth-field">
            <label>手机号</label>
            <input type="tel" inputMode="numeric" value={formData.phone} onChange={(e) => handleChange('phone', e.target.value)} placeholder="请输入 11 位手机号" />
          </div>
          <div className="auth-field">
            <label>验证码</label>
            <div className="code-field">
              <input type="tel" inputMode="numeric" value={formData.verificationCode} onChange={(e) => handleChange('verificationCode', e.target.value)} placeholder="请输入 6 位验证码" />
              <button type="button" onClick={handleSendCode} disabled={sendingCode || codeCooldown > 0}>
                {codeCooldown > 0 ? `${codeCooldown}s` : sendingCode ? '发送中' : '获取验证码'}
              </button>
            </div>
          </div>
          <div className="auth-field">
            <label>密码</label>
            <div className="password-field">
              <input type={showPassword ? 'text' : 'password'} value={formData.password} onChange={(e) => handleChange('password', e.target.value)} placeholder="至少 6 位" />
              <button type="button" className="password-toggle-btn" aria-label={showPassword ? '隐藏密码' : '显示密码'} onClick={() => setShowPassword((visible) => !visible)}>
                <EyeIcon hidden={!showPassword} />
              </button>
            </div>
          </div>
          <div className="auth-field">
            <label>确认密码</label>
            <div className="password-field">
              <input type={showPassword ? 'text' : 'password'} value={formData.confirmPassword} onChange={(e) => handleChange('confirmPassword', e.target.value)} placeholder="再次输入密码" />
              <button type="button" className="password-toggle-btn" aria-label={showPassword ? '隐藏密码' : '显示密码'} onClick={() => setShowPassword((visible) => !visible)}>
                <EyeIcon hidden={!showPassword} />
              </button>
            </div>
          </div>
          <div className="auth-error">{error}</div>
          <button className="cosmic-btn-primary auth-submit" disabled={submitting}>
            {submitting ? '注册中...' : '注册并登录'}
          </button>
        </form>

        <div className="auth-switch">
          已有账号？<Link to="/login">去登录</Link>
        </div>
      </div>
    </div>
  );
}

export default Register;
