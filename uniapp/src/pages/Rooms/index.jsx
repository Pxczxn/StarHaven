import { useState, useEffect } from 'react';
import { Link, useSearchParams, useNavigate } from 'react-router-dom';
import { getAvailableRooms } from '../../api/room';
import { formatDate, formatMoney, getDaysDiff } from '../../utils/format';
import dayjs from 'dayjs';
import './index.css';

function Rooms() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const [checkInDate, setCheckInDate] = useState(
    searchParams.get('checkInDate') || dayjs().add(1, 'day').format('YYYY-MM-DD')
  );
  const [checkOutDate, setCheckOutDate] = useState(
    searchParams.get('checkOutDate') || dayjs().add(2, 'day').format('YYYY-MM-DD')
  );
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);

  useEffect(() => {
    if (searchParams.get('checkInDate') && searchParams.get('checkOutDate')) {
      fetchRooms();
    }
  }, []);

  const fetchRooms = async () => {
    try {
      setLoading(true);
      const data = await getAvailableRooms({
        checkInDate,
        checkOutDate,
      });
      setRooms(data);
      setHasSearched(true);
    } catch (error) {
      console.error('查询失败:', error);
      alert(error.message || '查询失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    if (!checkInDate || !checkOutDate) {
      alert('请选择入住和离店日期');
      return;
    }

    if (dayjs(checkOutDate).isBefore(dayjs(checkInDate))) {
      alert('离店日期必须晚于入住日期');
      return;
    }

    fetchRooms();
  };

  const handleBooking = (room) => {
    navigate(`/booking?roomId=${room.id}&checkInDate=${checkInDate}&checkOutDate=${checkOutDate}`);
  };

  const nights = checkInDate && checkOutDate ? getDaysDiff(checkInDate, checkOutDate) : 0;

  return (
    <div className="rooms-page">
      {/* 头部 */}
      <header className="rooms-header">
        <div className="header-content">
          <Link to="/" className="back-link">← 返回首页</Link>
          <h1>预订房间</h1>
        </div>
      </header>

      {/* 日期选择器 */}
      <div className="date-selector">
        <div className="date-selector-content">
          <div className="date-input-group">
            <label>入住日期</label>
            <input
              type="date"
              value={checkInDate}
              onChange={(e) => setCheckInDate(e.target.value)}
              min={dayjs().format('YYYY-MM-DD')}
            />
          </div>
          <div className="date-arrow">→</div>
          <div className="date-input-group">
            <label>离店日期</label>
            <input
              type="date"
              value={checkOutDate}
              onChange={(e) => setCheckOutDate(e.target.value)}
              min={dayjs(checkInDate).add(1, 'day').format('YYYY-MM-DD')}
            />
          </div>
          <button className="search-button" onClick={handleSearch} disabled={loading}>
            {loading ? '查询中...' : '查询房间'}
          </button>
        </div>
        {nights > 0 && (
          <div className="nights-info">
            共 <span className="nights-count">{nights}</span> 晚
          </div>
        )}
      </div>

      {/* 房间列表 */}
      <div className="rooms-container">
        {loading ? (
          <div className="loading-state">
            <div className="loading-spinner"></div>
            <p>正在查询可订房间...</p>
          </div>
        ) : hasSearched ? (
          rooms.length > 0 ? (
            <div className="rooms-list">
              {rooms.map((room) => (
                <div key={room.id} className="room-item">
                  <div className="room-image">
                    {room.imageUrl ? (
                      <img src={room.imageUrl} alt={room.name} />
                    ) : (
                      <div className="room-placeholder">暂无图片</div>
                    )}
                  </div>
                  <div className="room-info">
                    <div className="room-header">
                      <h3 className="room-name">{room.name}</h3>
                      <span className="room-no">房间号: {room.roomNo}</span>
                    </div>
                    <div className="room-details">
                      <span className="room-detail-item">📐 {room.area}㎡</span>
                      <span className="room-detail-item">🏢 {room.floor}</span>
                      <span className="room-detail-item">👥 {room.capacity}人</span>
                    </div>
                    {room.facilities && (
                      <div className="room-facilities">
                        {room.facilities.split(',').map((facility, index) => (
                          <span key={index} className="facility-tag">{facility}</span>
                        ))}
                      </div>
                    )}
                    <div className="room-footer">
                      <div className="room-price">
                        <span className="price-label">¥{room.price}</span>
                        <span className="price-unit">/晚</span>
                        {nights > 0 && (
                          <span className="total-price">
                            总计: {formatMoney(room.price * nights)}
                          </span>
                        )}
                      </div>
                      <button
                        className="book-button"
                        onClick={() => handleBooking(room)}
                      >
                        立即预订
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="empty-state">
              <div className="empty-icon">🏠</div>
              <p>所选日期暂无可订房间</p>
              <p className="empty-tip">请尝试更换日期</p>
            </div>
          )
        ) : (
          <div className="empty-state">
            <div className="empty-icon">📅</div>
            <p>请选择入住日期查询可订房间</p>
          </div>
        )}
      </div>
    </div>
  );
}

export default Rooms;
