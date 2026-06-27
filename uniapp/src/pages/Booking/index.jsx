import { useEffect, useRef, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { createOrder } from '../../api/order';
import { getRoomById } from '../../api/room';
import { formatDate, getDaysDiff } from '../../utils/format';
import { getTenantUser } from '../../utils/tenantAuth';
import './index.css';

function Booking() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const roomId = searchParams.get('roomId');
  const checkInDate = searchParams.get('checkInDate');
  const checkOutDate = searchParams.get('checkOutDate');

  const [room, setRoom] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [verifyModalOpen, setVerifyModalOpen] = useState(false);
  const verifyPromptShownRef = useRef(false);

  const [formData, setFormData] = useState({
    customerName: '',
    customerPhone: '',
    customerIdNumber: '',
    remark: '',
  });
  const [errors, setErrors] = useState({});

  useEffect(() => {
    const tenantUser = getTenantUser();
    if (!tenantUser?.idNumber) {
      if (!verifyPromptShownRef.current) {
        verifyPromptShownRef.current = true;
        setVerifyModalOpen(true);
      }
      setLoading(false);
      return;
    }

    setFormData((current) => ({
      ...current,
      customerName: current.customerName || tenantUser.realName || '',
      customerPhone: current.customerPhone || tenantUser.phone || '',
      customerIdNumber: current.customerIdNumber || tenantUser.idNumber || '',
    }));

    if (!roomId || !checkInDate || !checkOutDate) {
      window.alert('缺少预订信息');
      navigate('/rooms');
      return;
    }

    const fetchRoom = async () => {
      try {
        setLoading(true);
        const data = await getRoomById(roomId, {
          checkInDate,
          checkOutDate,
        });
        if (!data) {
          window.alert('房间不存在或不可用');
          navigate('/rooms');
          return;
        }
        setRoom(data);
      } catch (error) {
        console.error('加载失败:', error);
        window.alert('房间信息加载失败');
        navigate('/rooms');
      } finally {
        setLoading(false);
      }
    };

    fetchRoom();
  }, [roomId, checkInDate, checkOutDate, navigate]);

  const validateForm = () => {
    const newErrors = {};

    if (!formData.customerName.trim()) {
      newErrors.customerName = '请输入姓名';
    }

    if (!formData.customerPhone.trim()) {
      newErrors.customerPhone = '请输入手机号';
    } else if (!/^1[3-9]\d{9}$/.test(formData.customerPhone)) {
      newErrors.customerPhone = '手机号格式不正确';
    }

    if (!formData.customerIdNumber.trim()) {
      newErrors.customerIdNumber = '请先完成实名认证';
    } else if (!/^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dX]$/.test(formData.customerIdNumber)) {
      newErrors.customerIdNumber = '身份证号格式不正确';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!validateForm()) return;

    try {
      setSubmitting(true);
      const result = await createOrder({
        roomId: parseInt(roomId, 10),
        customerName: formData.customerName,
        customerPhone: formData.customerPhone,
        customerIdNumber: formData.customerIdNumber,
        checkInDate,
        checkOutDate,
        remark: formData.remark,
      });
      navigate(`/booking/success?orderNo=${result.orderNo}`);
    } catch (error) {
      console.error('预订失败:', error);
      window.alert(error.message || '预订失败，请重试');
    } finally {
      setSubmitting(false);
    }
  };

  const handleChange = (field, value) => {
    if (field === 'customerPhone') {
      value = value.replace(/\D/g, '').slice(0, 11);
    }
    if (field === 'customerIdNumber') {
      value = value.replace(/[^0-9X]/gi, '').toUpperCase().slice(0, 18);
    }

    setFormData({ ...formData, [field]: value });
    if (errors[field]) {
      setErrors({ ...errors, [field]: '' });
    }
  };

  const renderVerifyModal = () => (
    <div className="booking-modal-mask" role="dialog" aria-modal="true">
      <div className="booking-modal cosmic-ui-card">
        <div className="booking-modal-icon">!</div>
        <div className="booking-modal-kicker">REAL NAME REQUIRED</div>
        <h2>请先完成实名认证</h2>
        <p>为保障入住登记合规，预订前需要先在个人中心填写身份证号。</p>
        <div className="booking-modal-actions">
          <button type="button" className="cosmic-btn-primary" onClick={() => navigate('/profile?verify=1')}>
            去实名认证
          </button>
          <button type="button" className="booking-modal-secondary" onClick={() => navigate('/rooms')}>
            返回房间
          </button>
        </div>
      </div>
    </div>
  );

  if (loading) {
    return (
      <div className="booking-page">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p className="loading-text">加载中...</p>
        </div>
      </div>
    );
  }

  if (!room) {
    return <div className="booking-page">{verifyModalOpen && renderVerifyModal()}</div>;
  }

  const nights = getDaysDiff(checkInDate, checkOutDate);
  const totalAmount = room.price * nights;

  return (
    <div className="booking-page">
      {verifyModalOpen && renderVerifyModal()}

      <header className="booking-header">
        <button onClick={() => navigate(-1)} className="back-button">
          ← 返回
        </button>
        <h1>确认预订</h1>
      </header>

      <div className="booking-container">
        <div className="form-section">
          <form onSubmit={handleSubmit} className="form-card cosmic-ui-card">
            <h2 className="card-title">客户信息</h2>

            <div className="form-grid">
              <div className="form-field">
                <label>
                  姓名 <span className="required">*</span>
                </label>
                <input
                  type="text"
                  value={formData.customerName}
                  onChange={(e) => handleChange('customerName', e.target.value)}
                  placeholder="请输入客户姓名"
                />
                {errors.customerName && <span className="error-message">{errors.customerName}</span>}
              </div>

              <div className="form-field">
                <label>
                  手机号 <span className="required">*</span>
                </label>
                <input
                  type="tel"
                  inputMode="numeric"
                  maxLength="11"
                  value={formData.customerPhone}
                  onChange={(e) => handleChange('customerPhone', e.target.value)}
                  placeholder="请输入 11 位手机号"
                />
                {errors.customerPhone && <span className="error-message">{errors.customerPhone}</span>}
              </div>

              <div className="form-field">
                <label>
                  身份证号 <span className="required">*</span>
                </label>
                <input
                  type="text"
                  maxLength="18"
                  value={formData.customerIdNumber}
                  readOnly
                  placeholder="请先在个人中心完成实名认证"
                />
                {errors.customerIdNumber && <span className="error-message">{errors.customerIdNumber}</span>}
              </div>

              <div className="form-field">
                <label>备注</label>
                <textarea
                  value={formData.remark}
                  onChange={(e) => handleChange('remark', e.target.value)}
                  placeholder="请输入备注信息（选填）"
                />
              </div>
            </div>
          </form>
        </div>

        <div className="info-section">
          <div className="info-card cosmic-ui-card">
            <h2 className="card-title">订单信息</h2>

            <div className="room-info">
              <div className="info-row highlight">
                <span className="label">房间</span>
                <span className="value">{room.name}</span>
              </div>
              <div className="info-row">
                <span className="label">房间号</span>
                <span className="value">{room.roomNo}</span>
              </div>
              <div className="info-row">
                <span className="label">入住日期</span>
                <span className="value">{formatDate(checkInDate)}</span>
              </div>
              <div className="info-row">
                <span className="label">离店日期</span>
                <span className="value">{formatDate(checkOutDate)}</span>
              </div>
              <div className="info-row">
                <span className="label">住宿天数</span>
                <span className="value">{nights} 晚</span>
              </div>
            </div>

            <div className="price-summary">
              <div className="price-row">
                <span className="label">房费</span>
                <span className="amount">¥{room.price} × {nights}晚</span>
              </div>
              <div className="price-total">
                <span className="label">订单金额</span>
                <span className="amount">¥{totalAmount}</span>
              </div>
            </div>

            <button
              type="submit"
              onClick={handleSubmit}
              disabled={submitting}
              className="cosmic-btn-primary submit-button"
            >
              {submitting ? '提交中...' : '确认预订'}
            </button>

            <p className="notice-text">提交后请尽快支付，订单将保留 24 小时</p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Booking;
