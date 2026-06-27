import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getTenantProfile, logoutTenant, verifyTenant } from '../../api/auth';
import { clearTenantAuth, getTenantToken, getTenantUser, setTenantAuth } from '../../utils/tenantAuth';
import './index.css';

function Profile() {
  const navigate = useNavigate();
  const [user, setUser] = useState(getTenantUser());
  const [loading, setLoading] = useState(true);
  const [idNumber, setIdNumber] = useState('');
  const [verifyError, setVerifyError] = useState('');
  const [verifying, setVerifying] = useState(false);

  useEffect(() => {
    if (!getTenantToken()) {
      navigate('/login?redirect=/profile');
      return;
    }
    loadProfile();
  }, []);

  const loadProfile = async () => {
    try {
      setLoading(true);
      const profile = await getTenantProfile();
      const token = getTenantToken();
      setTenantAuth({ token, user: profile });
      setUser(profile);
      setIdNumber(profile.idNumber || '');
    } catch (error) {
      clearTenantAuth();
      navigate('/login?redirect=/profile');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    try {
      await logoutTenant();
    } catch (error) {
      // 退出以本地状态为准
    }
    clearTenantAuth();
    navigate('/');
  };

  const handleIdNumberChange = (value) => {
    setIdNumber(value.replace(/[^0-9xX]/g, '').toUpperCase().slice(0, 18));
    setVerifyError('');
  };

  const handleVerify = async () => {
    if (!/^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dX]$/.test(idNumber)) {
      setVerifyError('请输入正确的 18 位身份证号');
      return;
    }

    try {
      setVerifying(true);
      const profile = await verifyTenant({ idNumber });
      const token = getTenantToken();
      setTenantAuth({ token, user: profile });
      setUser(profile);
      setVerifyError('');
    } catch (error) {
      setVerifyError(error.message || '实名认证失败');
    } finally {
      setVerifying(false);
    }
  };

  if (loading) {
    return (
      <div className="profile-page">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>加载中...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="profile-page">
      <div className="profile-card cosmic-ui-card">
        <div className="profile-head">
          <div className="profile-avatar">{(user?.realName || user?.phone || '星').slice(0, 1)}</div>
          <div>
            <div className="profile-name">{user?.realName || '星栖租客'}</div>
            <div className="profile-phone">{user?.phone}</div>
          </div>
        </div>

        <div className="profile-grid">
          <div className="profile-item">
            <div className="profile-label">账号</div>
            <div className="profile-value">{user?.username}</div>
          </div>
          <div className="profile-item">
            <div className="profile-label">身份</div>
            <div className="profile-value">租客</div>
          </div>
          <div className="profile-item">
            <div className="profile-label">手机号</div>
            <div className="profile-value">{user?.phone}</div>
          </div>
          <div className="profile-item">
            <div className="profile-label">实名认证</div>
            <div className="profile-value">{user?.idNumber ? '已认证' : '未认证'}</div>
          </div>
        </div>

        <div className="verify-panel">
          <div>
            <div className="verify-title">实名认证</div>
            <p>根据入住登记要求，完成身份证信息后才可以提交预订。</p>
          </div>
          <div className="verify-form">
            <input
              value={idNumber}
              onChange={(event) => handleIdNumberChange(event.target.value)}
              placeholder="请输入 18 位身份证号"
              maxLength={18}
            />
            <button type="button" className="cosmic-btn-secondary" onClick={handleVerify} disabled={verifying}>
              {verifying ? '保存中...' : user?.idNumber ? '更新认证' : '完成认证'}
            </button>
          </div>
          {verifyError && <div className="verify-error">{verifyError}</div>}
        </div>

        <div className="profile-actions">
          <Link to="/rooms" className="cosmic-btn-primary">继续预订</Link>
          <Link to={`/orders/query?phone=${user?.phone || ''}`} className="cosmic-btn-secondary">查询我的订单</Link>
          <button type="button" className="cosmic-btn-secondary" onClick={handleLogout}>退出登录</button>
        </div>
      </div>
    </div>
  );
}

export default Profile;
