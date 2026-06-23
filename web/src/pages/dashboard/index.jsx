import React from 'react';
import { Card, Row, Col, Statistic } from 'antd';
import {
  UserAddOutlined,
  UserDeleteOutlined,
  HomeOutlined,
  CheckCircleOutlined,
} from '@ant-design/icons';

const Dashboard = () => {
  return (
    <div className="dashboard">
      <h2 className="page-title">数据看板</h2>

      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <Card className="stat-card">
            <Statistic
              title="今日入住"
              value={0}
              prefix={<UserAddOutlined />}
              suffix="间"
              valueStyle={{ color: '#22C55E' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card className="stat-card">
            <Statistic
              title="今日退房"
              value={0}
              prefix={<UserDeleteOutlined />}
              suffix="间"
              valueStyle={{ color: '#F59E0B' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card className="stat-card">
            <Statistic
              title="当前空房"
              value={7}
              prefix={<HomeOutlined />}
              suffix="间"
              valueStyle={{ color: '#2563EB' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card className="stat-card">
            <Statistic
              title="当前入住"
              value={0}
              prefix={<CheckCircleOutlined />}
              suffix="间"
              valueStyle={{ color: '#38BDF8' }}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col span={24}>
          <Card className="info-card">
            <p style={{ color: '#cbd5e1', textAlign: 'center', margin: 0 }}>
              欢迎使用星栖民宿管理系统
            </p>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Dashboard;
