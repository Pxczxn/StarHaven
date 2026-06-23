import { useSearchParams, Link } from 'react-router-dom';
import './index.css';

function BookingSuccess() {
  const [searchParams] = useSearchParams();
  const orderNo = searchParams.get('orderNo');
  const phone = searchParams.get('phone');

  return (
    <div className="success-page">
      <div className="success-container">
        <div className="success-icon">✓</div>
        <h1 className="success-title">预订成功！</h1>
        <p className="success-message">您的预订已提交，请保存订单号</p>

        <div className="order-info">
          <div className="order-no">
            <span className="label">订单号：</span>
            <span className="value">{orderNo}</span>
          </div>
          <p className="tip">请凭订单号和手机号查询订单</p>
        </div>

        <div className="success-actions">
          <Link to={`/orders/${orderNo}?phone=${phone}`} className="action-button primary">
            查看订单详情
          </Link>
          <Link to="/" className="action-button secondary">
            返回首页
          </Link>
        </div>
      </div>
    </div>
  );
}

export default BookingSuccess;
