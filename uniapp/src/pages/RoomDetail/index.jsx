import { Link } from 'react-router-dom';

function RoomDetail() {
  return (
    <div style={{ padding: '20px', textAlign: 'center' }}>
      <h1>房间详情页</h1>
      <p>开发中...</p>
      <Link to="/rooms">返回房间列表</Link>
    </div>
  );
}

export default RoomDetail;
