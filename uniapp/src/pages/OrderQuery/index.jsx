import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { queryOrder } from '../../api/order';
import { formatDate, formatMoney } from '../../utils/format';
import './index.css';

function OrderQuery() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [formData, setFormData] = useState({
    orderNo: searchParams.get('orderNo') || '',
    phone: searchParams.get('phone') || '',
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [orders, setOrders] = useState([]);

  const handleChange = (e) => {
    const { name } = e.target;
    let { value } = e.target;

    if (name === 'orderNo') {
      value = value.trim().toUpperCase();
    }

    if (name === 'phone') {
      value = value.replace(/\D/g, '').slice(0, 11);
    }

    setFormData((prev) => ({ ...prev, [name]: value }));
    // 清除错误
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: '' }));
    }
  };

  const validate = () => {
    const newErrors = {};

    if (!formData.orderNo.trim() && !formData.phone.trim()) {
      newErrors.orderNo = '订单尾号和手机号至少填写一项';
      newErrors.phone = '订单尾号和手机号至少填写一项';
    }

    if (
      formData.orderNo.trim() &&
      !/^\d{6}$/.test(formData.orderNo.trim()) &&
      !formData.orderNo.trim().startsWith('ORD')
    ) {
      newErrors.orderNo = '请输入 6 位订单尾号，或 ORD 开头的完整订单号';
    }

    if (formData.phone.trim() && !/^1[3-9]\d{9}$/.test(formData.phone)) {
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
      const result = await queryOrder({
        orderNo: formData.orderNo,
        phone: formData.phone,
      });
      const orderList = Array.isArray(result) ? result : [result];

      if (orderList.length === 1) {
        const order = orderList[0];
        navigate(`/orders/${order.orderNo}?phone=${order.customerPhone}`);
        return;
      }

      setOrders(orderList);
    } catch (error) {
      console.error('查询失败:', error);
      alert(error.message || '订单不存在或手机号不匹配');
      setOrders([]);
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
          <div className="card-icon">
            <span>⌕</span>
          </div>
          <p className="query-kicker">ORDER LOOKUP</p>
          <h2>查询您的订单</h2>
          <p className="card-desc">订单尾号后 6 位或手机号任选一项即可查询</p>

          <div className="query-form">
            <div className="form-group">
              <label>
                订单尾号后 6 位
              </label>
              <input
                type="text"
                name="orderNo"
                value={formData.orderNo}
                onChange={handleChange}
                placeholder="例如 413014，也支持完整订单号"
              />
              {errors.orderNo && (
                <span className="error-message">{errors.orderNo}</span>
              )}
            </div>

            <div className="form-group">
              <label>
                手机号
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
            <li><span>01</span>预订成功后会显示完整订单号，日常查询可只输入尾号后 6 位</li>
            <li><span>02</span>也可以只输入预订时使用的手机号查询全部订单</li>
            <li><span>03</span>如有疑问，请联系客服</li>
          </ul>
        </div>

        {orders.length > 1 && (
          <div className="query-results">
            <div className="results-header">
              <h3>查询结果</h3>
              <span>{orders.length} 个订单</span>
            </div>
            <div className="result-list">
              {orders.map((order) => (
                <button
                  type="button"
                  className="result-card"
                  key={order.orderNo}
                  onClick={() => navigate(`/orders/${order.orderNo}?phone=${order.customerPhone}`)}
                >
                  <div>
                    <strong>{order.orderNo}</strong>
                    <p>{formatDate(order.checkInDate)} 至 {formatDate(order.checkOutDate)}</p>
                  </div>
                  <div className="result-meta">
                    <span>{order.roomNo || '待分配'}</span>
                    <b>{formatMoney(order.totalAmount)}</b>
                  </div>
                </button>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default OrderQuery;
