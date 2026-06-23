import { useState, useEffect } from 'react';
import { useParams, useSearchParams, useNavigate } from 'react-router-dom';
import { getOrderByNo } from '../../api/order';
import { formatDate, formatMoney, getDaysDiff } from '../../utils/format';
import { ORDER_STATUS, ORDER_STATUS_COLOR, PAYMENT_STATUS, PAYMENT_STATUS_COLOR } from '../../utils/constants';
import './index.css';

function OrderDetail() {
  const { orderNo } = useParams();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const phone = searchParams.get('phone');

  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (orderNo && phone) {
      fetchOrder();
    } else {
      navigate('/orders/query');
    }
  }, [orderNo, phone]);

  const fetchOrder = async () => {
    try {
      setLoading(true);
      const data = await getOrderByNo(orderNo, { phone });
      setOrder(data);
    } catch (error) {
      console.error('加载失败:', error);
      alert(error.message || '订单加载失败');
      navigate('/orders/query');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="detail-page">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>加载中...</p>
        </div>
      </div>
    );
  }

  if (!order) {
    return null;
  }

  const nights = getDaysDiff(order.checkInDate, order.checkOutDate);

  return (
    <div className="detail-page">
      <header className="detail-header">
        <button onClick={() => navigate('/orders/query')} className="back-button">
          ← 返回查询
        </button>
        <h1>订单详情</h1>
      </header>

      <div className="detail-container">
        {/* 订单状态 */}
        <div className="status-card">
          <div className="status-badge" style={{ backgroundColor: ORDER_STATUS_COLOR[order.status] }}>
            {ORDER_STATUS[order.status] || order.status}
          </div>
          <p className="status-message">
            {order.status === 'pending' && '订单待确认，请耐心等待'}
            {order.status === 'reserved' && '订单已确认，期待您的入住'}
            {order.status === 'occupied' && '正在入住中'}
            {order.status === 'checked_out' && '已退房'}
            {order.status === 'completed' && '订单已完成'}
            {order.status === 'cancelled' && '订单已取消'}
          </p>
        </div>

        {/* 订单信息 */}
        <div className="info-card">
          <h3>订单信息</h3>
          <div className="info-list">
            <div className="info-item">
              <span className="label">订单号：</span>
              <span className="value">{order.orderNo}</span>
            </div>
            <div className="info-item">
              <span className="label">下单时间：</span>
              <span className="value">{formatDate(order.createdAt, 'YYYY-MM-DD HH:mm')}</span>
            </div>
            <div className="info-item">
              <span className="label">订单来源：</span>
              <span className="value">UniApp</span>
            </div>
          </div>
        </div>

        {/* 房间信息 */}
        <div className="info-card">
          <h3>房间信息</h3>
          <div className="info-list">
            <div className="info-item">
              <span className="label">房间号：</span>
              <span className="value">{order.roomNo}</span>
            </div>
            <div className="info-item">
              <span className="label">入住日期：</span>
              <span className="value">{formatDate(order.checkInDate)}</span>
            </div>
            <div className="info-item">
              <span className="label">离店日期：</span>
              <span className="value">{formatDate(order.checkOutDate)}</span>
            </div>
            <div className="info-item">
              <span className="label">住宿天数：</span>
              <span className="value">{nights} 晚</span>
            </div>
          </div>
        </div>

        {/* 客户信息 */}
        <div className="info-card">
          <h3>客户信息</h3>
          <div className="info-list">
            <div className="info-item">
              <span className="label">姓名：</span>
              <span className="value">{order.customerName}</span>
            </div>
            <div className="info-item">
              <span className="label">手机号：</span>
              <span className="value">{order.customerPhone}</span>
            </div>
            {order.remark && (
              <div className="info-item">
                <span className="label">备注：</span>
                <span className="value">{order.remark}</span>
              </div>
            )}
          </div>
        </div>

        {/* 费用信息 */}
        <div className="info-card">
          <h3>费用信息</h3>
          <div className="info-list">
            <div className="info-item">
              <span className="label">订单金额：</span>
              <span className="value">{formatMoney(order.totalAmount)}</span>
            </div>
            <div className="info-item">
              <span className="label">已付金额：</span>
              <span className="value">{formatMoney(order.paidAmount)}</span>
            </div>
            <div className="info-item">
              <span className="label">支付状态：</span>
              <span className="value" style={{ color: PAYMENT_STATUS_COLOR[order.paymentStatus] }}>
                {PAYMENT_STATUS[order.paymentStatus] || order.paymentStatus}
              </span>
            </div>
            {order.paymentStatus !== 'paid' && (
              <div className="info-item total">
                <span className="label">待支付：</span>
                <span className="value highlight">
                  {formatMoney(order.totalAmount - order.paidAmount)}
                </span>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default OrderDetail;
