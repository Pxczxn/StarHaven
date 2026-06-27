import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getSiteInfo } from '../../api/site';
import { getRoomTypes } from '../../api/room';
import './index.css';

function Home() {
  const [siteInfo, setSiteInfo] = useState(null);
  const [roomTypes, setRoomTypes] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [site, types] = await Promise.all([
        getSiteInfo(),
        getRoomTypes(),
      ]);
      setSiteInfo(site);
      setRoomTypes(types.slice(0, 3));
    } catch (error) {
      console.error('加载失败:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>加载中...</p>
      </div>
    );
  }

  return (
    <div className="home-page">
      {/* 品牌展示区 */}
      <header className="hero-section">
        <div className="brand-tag">{siteInfo?.brandName || '星栖民宿'}</div>
        <h1 className="hero-title">{siteInfo?.slogan || '星辰为引，栖心而居'}</h1>
        <p className="hero-subtitle">
          {siteInfo?.description || '在星空下，找到心灵的栖息地'}
        </p>
        <Link to="/rooms" className="cosmic-btn-primary">
          立即预订
        </Link>
      </header>

      {/* 功能导航 */}
      <section className="features-section">
        <Link to="/rooms" className="feature-card cosmic-ui-card">
          <div className="feature-icon">🏠</div>
          <h3 className="feature-title">浏览房型</h3>
          <p className="feature-desc">查看房型、价格、可住人数等信息</p>
        </Link>

        <Link to="/rooms" className="feature-card cosmic-ui-card">
          <div className="feature-icon">📅</div>
          <h3 className="feature-title">在线预订</h3>
          <p className="feature-desc">选择入住日期，在线完成预订下单</p>
        </Link>

        <Link to="/orders/query" className="feature-card cosmic-ui-card">
          <div className="feature-icon">🔍</div>
          <h3 className="feature-title">订单查询</h3>
          <p className="feature-desc">通过订单号和手机号查询预订信息</p>
        </Link>
      </section>

      {/* 推荐房型 */}
      <section className="rooms-section">
        <h2 className="section-title">推荐房型</h2>
        <div className="rooms-grid">
          {roomTypes.map((room) => (
            <div key={room.id} className="room-card cosmic-ui-card">
              <div className="room-image">
                {room.imageUrl ? (
                  <img src={room.imageUrl} alt={room.name} />
                ) : (
                  <div className="room-placeholder">🌟</div>
                )}
              </div>
              <div className="room-info">
                <h3 className="room-name">{room.name}</h3>
                <p className="room-desc">{room.description}</p>
                <div className="room-meta">
                  <span className="room-capacity">👥 {room.capacity}人</span>
                  <span className="room-bed">{room.bedType}</span>
                  <span className="room-breakfast">{room.breakfast}</span>
                </div>
                <div className="room-footer">
                  <div className="room-price">
                    <span className="price-label">起</span>
                    <span className="price-value">¥{room.defaultPrice}</span>
                    <span className="price-unit">/晚</span>
                  </div>
                  <Link to="/rooms" className="cosmic-btn-secondary">
                    查看详情
                  </Link>
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* 底部联系信息 */}
      <footer className="home-footer">
        <Link to="/contact" className="footer-link">
          联系我们
        </Link>
        {siteInfo && (
          <div className="footer-info">
            <p>📞 {siteInfo.phone}</p>
            <p>📍 {siteInfo.address}</p>
          </div>
        )}
      </footer>
    </div>
  );
}

export default Home;
