import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getSiteInfo } from '../../api/site';

function Contact() {
  const [siteInfo, setSiteInfo] = useState(null);

  useEffect(() => {
    fetchSiteInfo();
  }, []);

  const fetchSiteInfo = async () => {
    try {
      const data = await getSiteInfo();
      setSiteInfo(data);
    } catch (error) {
      console.error('加载失败:', error);
    }
  };

  return (
    <div style={{ padding: '20px', maxWidth: '600px', margin: '0 auto' }}>
      <h1>联系我们</h1>
      {siteInfo && (
        <div style={{ marginTop: '20px' }}>
          <p><strong>电话：</strong>{siteInfo.phone}</p>
          <p><strong>地址：</strong>{siteInfo.address}</p>
        </div>
      )}
      <Link to="/" style={{ display: 'inline-block', marginTop: '20px' }}>返回首页</Link>
    </div>
  );
}

export default Contact;
