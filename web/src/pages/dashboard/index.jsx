import React, { useEffect, useMemo, useState } from 'react';
import { Card, Col, Empty, Row, Spin, Statistic, message } from 'antd';
import {
  CheckCircleOutlined,
  HomeOutlined,
  RiseOutlined,
  UserAddOutlined,
  UserDeleteOutlined,
  WalletOutlined,
} from '@ant-design/icons';
import {
  getDashboardSummary,
  getOrderTrend,
  getRevenueTrend,
  getRoomStatus,
} from '../../api/dashboard';
import { ROOM_STATUS } from '../../utils/constants';
import './index.css';

const defaultSummary = {
  todayCheckIns: 0,
  todayCheckOuts: 0,
  availableRooms: 0,
  occupiedRooms: 0,
  todayNewOrders: 0,
  todayRevenue: 0,
  monthRevenue: 0,
  occupancyRate: 0,
  totalRooms: 0,
};

const money = (value) => `¥${Number(value || 0).toFixed(2)}`;
const shortDate = (date) => (date ? date.slice(5) : '');

const MiniBars = ({ data, valueKey, color, suffix = '' }) => {
  const max = useMemo(
    () => Math.max(...data.map((item) => Number(item[valueKey] || 0)), 0),
    [data, valueKey]
  );

  if (!data.length) {
    return <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="暂无数据" />;
  }

  return (
    <div className="mini-bars">
      {data.map((item) => {
        const value = Number(item[valueKey] || 0);
        const height = max > 0 ? Math.max((value / max) * 100, 8) : 8;
        return (
          <div className="mini-bar-item" key={item.date}>
            <div className="mini-bar-track">
              <div
                className="mini-bar-fill"
                style={{
                  height: `${height}%`,
                  background: color,
                }}
                title={`${shortDate(item.date)} ${value}${suffix}`}
              />
            </div>
            <span>{shortDate(item.date)}</span>
          </div>
        );
      })}
    </div>
  );
};

const Dashboard = () => {
  const [loading, setLoading] = useState(false);
  const [summary, setSummary] = useState(defaultSummary);
  const [orderTrend, setOrderTrend] = useState([]);
  const [revenueTrend, setRevenueTrend] = useState([]);
  const [roomStatus, setRoomStatus] = useState([]);

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    setLoading(true);
    try {
      const [summaryRes, orderTrendRes, revenueTrendRes, roomStatusRes] = await Promise.all([
        getDashboardSummary(),
        getOrderTrend(),
        getRevenueTrend(),
        getRoomStatus(),
      ]);

      setSummary({ ...defaultSummary, ...(summaryRes.data || {}) });
      setOrderTrend(orderTrendRes.data || []);
      setRevenueTrend(revenueTrendRes.data || []);
      setRoomStatus(roomStatusRes.data || []);
    } catch (error) {
      message.error(error.message || '数据看板加载失败');
    } finally {
      setLoading(false);
    }
  };

  const roomStatusTotal = roomStatus.reduce((sum, item) => sum + Number(item.count || 0), 0);

  return (
    <Spin spinning={loading}>
      <div className="dashboard">
        <div className="dashboard-header">
          <div>
            <p className="dashboard-kicker">OPERATIONS OVERVIEW</p>
            <h2 className="page-title">数据看板</h2>
          </div>
          <div className="dashboard-date">今日经营概览</div>
        </div>

        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} xl={6}>
            <Card className="dashboard-stat-card">
              <Statistic
                title="今日入住"
                value={summary.todayCheckIns}
                prefix={<UserAddOutlined />}
                suffix="间"
                valueStyle={{ color: '#22C55E' }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} xl={6}>
            <Card className="dashboard-stat-card">
              <Statistic
                title="今日退房"
                value={summary.todayCheckOuts}
                prefix={<UserDeleteOutlined />}
                suffix="间"
                valueStyle={{ color: '#F59E0B' }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} xl={6}>
            <Card className="dashboard-stat-card">
              <Statistic
                title="当前空房"
                value={summary.availableRooms}
                prefix={<HomeOutlined />}
                suffix="间"
                valueStyle={{ color: '#06B6D4' }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} xl={6}>
            <Card className="dashboard-stat-card">
              <Statistic
                title="当前入住"
                value={summary.occupiedRooms}
                prefix={<CheckCircleOutlined />}
                suffix="间"
                valueStyle={{ color: '#F43F5E' }}
              />
            </Card>
          </Col>
        </Row>

        <Row gutter={[16, 16]} className="dashboard-row">
          <Col xs={24} sm={12} xl={6}>
            <Card className="dashboard-stat-card compact">
              <Statistic title="今日新增订单" value={summary.todayNewOrders} prefix={<RiseOutlined />} suffix="单" />
            </Card>
          </Col>
          <Col xs={24} sm={12} xl={6}>
            <Card className="dashboard-stat-card compact">
              <Statistic title="今日预计收入" value={money(summary.todayRevenue)} prefix={<WalletOutlined />} />
            </Card>
          </Col>
          <Col xs={24} sm={12} xl={6}>
            <Card className="dashboard-stat-card compact">
              <Statistic title="本月累计收入" value={money(summary.monthRevenue)} prefix={<WalletOutlined />} />
            </Card>
          </Col>
          <Col xs={24} sm={12} xl={6}>
            <Card className="dashboard-stat-card compact">
              <Statistic title="房间入住率" value={summary.occupancyRate} suffix="%" prefix={<RiseOutlined />} />
            </Card>
          </Col>
        </Row>

        <Row gutter={[16, 16]} className="dashboard-row">
          <Col xs={24} lg={12}>
            <Card className="dashboard-panel" title="近 7 日订单趋势">
              <MiniBars data={orderTrend} valueKey="count" color="linear-gradient(180deg, #06B6D4, #2563EB)" suffix="单" />
            </Card>
          </Col>
          <Col xs={24} lg={12}>
            <Card className="dashboard-panel" title="近 7 日收入趋势">
              <MiniBars data={revenueTrend} valueKey="amount" color="linear-gradient(180deg, #F43F5E, #8B5CF6)" suffix="元" />
            </Card>
          </Col>
        </Row>

        <Row gutter={[16, 16]} className="dashboard-row">
          <Col span={24}>
            <Card className="dashboard-panel" title="房态分布">
              <div className="room-status-grid">
                {roomStatus.map((item) => {
                  const count = Number(item.count || 0);
                  const percent = roomStatusTotal > 0 ? Math.round((count / roomStatusTotal) * 100) : 0;
                  return (
                    <div className="room-status-item" key={item.status}>
                      <div className="room-status-meta">
                        <span>{ROOM_STATUS[item.status] || item.status}</span>
                        <strong>{count} 间</strong>
                      </div>
                      <div className="room-status-progress">
                        <i style={{ width: `${percent}%` }} />
                      </div>
                    </div>
                  );
                })}
              </div>
            </Card>
          </Col>
        </Row>
      </div>
    </Spin>
  );
};

export default Dashboard;
