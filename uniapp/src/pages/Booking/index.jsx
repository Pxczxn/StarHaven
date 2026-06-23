import { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { getRoomById } from '../../api/room';
import { createOrder } from '../../api/order';
import { formatDate, getDaysDiff, formatMoney } from '../../utils/format';
import './index.css';

function Booking() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const roomId = searchParams.get('roomId');
  const checkInDate = searchParams.get('checkInDate');
  const checkOutDate = searchParams.get('checkOutDate');

  const [room, setRoom] = useState(null);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const [formData, setFormData] = useState({
    customerName: '',
    customerPhone: '',
    customerIdNumber: '',
    remark: '',
  });

  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (roomId) {
      fetchRoom();
    }
  }, [roomId]);

  const fetchRoom = async () => {
    try {
      setLoading(true);
      const data = await getRoomById(roomId, { checkInDate, checkOutDate });
      setRoom(data);
    } catch (error) {
      console.error('加载失败:', error);
      alert('房间信息加载失败');
    } finally {
      setLoading(false);
    }
  };

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

    if (!formData.customerName.trim()) {
      newErrors.customerName = '请输入客户姓名';
    }

    if (!formData.customerPhone.trim()) {
      newErrors.customerPhone = '请输入手机号';
    } else if (!/^1[3-9]\d{9}$/.test(formData.customerPhone)) {
      newErrors.customerPhone = '手机号格式不正确';
    }

    if (!formData.customerIdNumber.trim()) {
      newErrors.customerIdNumber = '请输入身份证号';
    } else if (
      !/^(\d{15}|\d{18}|\d{17}[Xx])$/.test(formData.customerIdNumber)
    ) {
      newErrors.customerIdNumber = '身份证号格式不正确';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async () => {
    if (!validate()) {
      return;
    }

    try {
      setSubmitting(true);

      const orderData = {
        roomId: parseInt(roomId),
        checkInDate,
        checkOutDate,
        customerName: formData.customerName,
        customerPhone: formData.customerPhone,
        customerIdNumber: formData.customerIdNumber,
        remark: formData.remark,
      };

      const order = await createOrder(orderData);

      // 跳转到成功页面
      navigate(`/booking/success?orderNo=${order.orderNo}&phone=${formData.customerPhone}`);
    } catch (error) {
      console.error('预订失败:', error);
      alert(error.message || '预订失败，请重试');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading || !room) {
    return (
      <div className="booking-page">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>加载中...</p>
        </div>
      </div>
    );
  }

  const nights = getDaysDiff(checkInDate, checkOutDate);
  const totalAmount = room.price * nights;

  return (
    <div className="booking-page">
      <header className="booking-header">
        <button onClick={() => navigate(-1)} className="back-button">
          ← 返回
        </button>
        <h1>确认预订</h1>
      </header>

      <div className="booking-container">
        {/* 房间信息 */}
        <div className="room-summary">
          <h3>房间信息</h3>
          <div className="summary-content">
            <div className="summary-item">
              <span className="label">房间：</span>
              <span className="value">{room.name} - {room.roomNo}</span>
            </div>
            <div className="summary-item">
              <span className="label">入住日期：</span>
              <span className="value">{formatDate(checkInDate)}</span>
            </div>
            <div className="summary-item">
              <span className="label">离店日期：</span>
              <span className="value">{formatDate(checkOutDate)}</span>
            </div>
            <div className="summary-item">
              <span className="label">住宿天数：</span>
              <span className="value">{nights} 晚</span>
            </div>
            <div className="summary-item total">
              <span className="label">订单金额：</span>
              <span className="value">{formatMoney(totalAmount)}</span>
            </div>
          </div>
        </div>

        {/* 客户信息表单 */}
        <div className="booking-form">
          <h3>客户信息</h3>
          <div className="form-content">
            <div className="form-group">
              <label>
                客户姓名 <span className="required">*</span>
              </label>
              <input
                type="text"
                name="customerName"
                value={formData.customerName}
                onChange={handleChange}
                placeholder="请输入客户姓名"
                maxLength={50}
              />
              {errors.customerName && (
                <span className="error-message">{errors.customerName}</span>
              )}
            </div>

            <div className="form-group">
              <label>
                手机号 <span className="required">*</span>
              </label>
              <input
                type="tel"
                name="customerPhone"
                value={formData.customerPhone}
                onChange={handleChange}
                placeholder="请输入手机号"
                maxLength={11}
              />
              {errors.customerPhone && (
                <span className="error-message">{errors.customerPhone}</span>
              )}
            </div>

            <div className="form-group">
              <label>
                身份证号 <span className="required">*</span>
              </label>
              <input
                type="text"
                name="customerIdNumber"
                value={formData.customerIdNumber}
                onChange={handleChange}
                placeholder="请输入身份证号"
                maxLength={18}
              />
              {errors.customerIdNumber && (
                <span className="error-message">{errors.customerIdNumber}</span>
              )}
            </div>

            <div className="form-group">
              <label>备注</label>
              <textarea
                name="remark"
                value={formData.remark}
                onChange={handleChange}
                placeholder="请输入备注信息（选填）"
                rows={4}
              />
            </div>
          </div>
        </div>

        {/* 提交按钮 */}
        <div className="booking-footer">
          <button
            className="submit-button"
            onClick={handleSubmit}
            disabled={submitting}
          >
            {submitting ? '提交中...' : '确认预订'}
          </button>
          <p className="tip">
            提交后请保存订单号，可凭订单号和手机号查询订单
          </p>
        </div>
      </div>
    </div>
  );
}

export default Booking;
