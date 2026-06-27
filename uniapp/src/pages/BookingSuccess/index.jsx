import { useSearchParams, Link } from 'react-router-dom';
import './index.css';

function BookingSuccess() {
  const [searchParams] = useSearchParams();
  const orderNo = searchParams.get('orderNo');
  const phone = searchParams.get('phone');
  const orderTail = orderNo ? orderNo.slice(-6) : '';
  const detailLink = phone ? `/orders/${orderTail}?phone=${phone}` : `/orders/query?orderNo=${orderTail}`;

  return (
    <div className="success-page">
      <div className="success-stars"></div>
      <div className="success-container">
        <div className="success-visual">
          <div className="success-orbit"></div>
          <div className="success-icon">✓</div>
        </div>

        <p className="success-kicker">BOOKING SUBMITTED</p>
        <h1 className="success-title">预订成功</h1>
        <p className="success-message">您的订单已提交，请保存订单号，工作人员确认后即可入住。</p>

        <div className="order-info">
          <div className="order-no">
            <span className="label">订单号</span>
            <span className="value">{orderNo}</span>
          </div>
          <div className="success-tip-row">
            <span>凭订单尾号后 6 位或手机号可随时查询订单状态</span>
          </div>
        </div>

        <div className="success-actions">
          <Link to={detailLink} className="action-button primary">
            {phone ? '查看订单详情' : '查询订单详情'}
          </Link>
          <Link to="/" className="action-button secondary">
            返回首页
          </Link>
        </div>

        <div className="success-footnote">
          请在到店时出示订单号，民宿将为您保留预订信息。
        </div>
      </div>
    </div>
  );
}

export default BookingSuccess;
