import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { queryOrder } from '../../api/order';
import './index.css';

function OrderQuery() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    orderNo: '',
    phone: '',
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    // 清除错误
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: '' }));
    }
  };

  const validate = () => {
    const newErrors = {};

    if (!formData.orderNo.trim()) {
      newErrors.orderNo = '请输入订单号';
    }

    if (!formData.phone.trim()) {
      newErrors.phone = '请输入手机号';
    } else if (!/^1[3-9]\d{9}$/.test(formData.phone)) {
      newErrors.phone = '手机号格式不正确';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleQuery = async () => {
    if (!validate()) {
      return;
    }

    try {
      setLoading(true);
      await queryOrder({
        orderNo: formData.orderNo,
        phone: formData.phone,
      });
      // 查询成功，跳转到订单详情
      navigate(`/orders/${formData.orderNo}?phone=${formData.phone}`);
    } catch (error) {
      console.error('查询失败:', error);
      alert(error.message || '订单不存在或手机号不匹配');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="query-page">
      <header className="query-header">
        <Link to="/" className="back-link">← 返回首页</Link>
        <h1>订单查询</h1>
      </header>

      <div className="query-container">
        <div className="query-card">
          <div className="card-icon">🔍</div>
          <h2>查询您的订单</h2>
          <p className="card-desc">请输入订单号和预订时使用的手机号</p>

          <div className="query-form">
            <div className="form-group">
              <label>
                订单号 <span className="required">*</span>
              </label>
              <input
                type="text"
                name="orderNo"
                value={formData.orderNo}
                onChange={handleChange}
                placeholder="请输入订单号"
              />
              {errors.orderNo && (
                <span className="error-message">{errors.orderNo}</span>
              )}
            </div>

            <div className="form-group">
              <label>
                手机号 <span className="required">*</span>
              </label>
              <input
                type="tel"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                placeholder="请输入手机号"
                maxLength={11}
              />
              {errors.phone && (
                <span className="error-message">{errors.phone}</span>
              )}
            </div>

            <button
              className="query-button"
              onClick={handleQuery}
              disabled={loading}
            >
              {loading ? '查询中...' : '查询订单'}
            </button>
          </div>
        </div>

        <div className="query-tips">
          <h3>温馨提示</h3>
          <ul>
            <li>订单号在预订成功后显示，请妥善保存</li>
            <li>手机号必须与预订时使用的手机号一致</li>
            <li>如有疑问，请联系客服</li>
          </ul>
        </div>
      </div>
    </div>
  );
}

export default OrderQuery;
