import React, { useEffect, useMemo, useState } from 'react';
import { Button, Card, DatePicker, Empty, message, Row, Col, Table, Tag } from 'antd';
import { DownloadOutlined, ReloadOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import {
  exportFinanceData,
  getFinanceRevenue,
  getFinanceSummary,
  getPaymentMethods,
  getRefundOrders,
  getUnpaidOrders,
} from '../../api/finance';
import { formatDateTime, formatMoney } from '../../utils/format';
import { ORDER_STATUS, ORDER_STATUS_COLOR, PAYMENT_METHOD } from '../../utils/constants';
import './index.css';

const { RangePicker } = DatePicker;

const Finance = () => {
  const [loading, setLoading] = useState(false);
  const [range, setRange] = useState([dayjs().startOf('month'), dayjs()]);
  const [summary, setSummary] = useState({});
  const [revenue, setRevenue] = useState([]);
  const [methods, setMethods] = useState([]);
  const [unpaidOrders, setUnpaidOrders] = useState([]);
  const [refundOrders, setRefundOrders] = useState([]);

  const params = useMemo(() => ({
    startDate: range?.[0]?.format('YYYY-MM-DD'),
    endDate: range?.[1]?.format('YYYY-MM-DD'),
  }), [range]);

  const loadData = async () => {
    setLoading(true);
    try {
      const [summaryRes, revenueRes, methodsRes, unpaidRes, refundRes] = await Promise.all([
        getFinanceSummary(params),
        getFinanceRevenue(params),
        getPaymentMethods(params),
        getUnpaidOrders(),
        getRefundOrders(),
      ]);
      setSummary(summaryRes.data || {});
      setRevenue(revenueRes.data || []);
      setMethods(methodsRes.data || []);
      setUnpaidOrders(unpaidRes.data || []);
      setRefundOrders(refundRes.data || []);
    } catch (error) {
      message.error(error.message || '加载财务数据失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [params.startDate, params.endDate]);

  const handleExport = async () => {
    try {
      const response = await exportFinanceData(params);
      message.success(`已生成 ${response.data?.length || 0} 条财务导出数据`);
    } catch (error) {
      message.error(error.message || '导出失败');
    }
  };

  const maxRevenue = Math.max(...revenue.map(item => Number(item.amount || 0)), 1);

  const orderColumns = [
    { title: '订单编号', dataIndex: 'orderNo', width: 190 },
    { title: '客户', dataIndex: 'customerName', width: 100 },
    { title: '房间', dataIndex: 'roomNo', width: 90 },
    { title: '总额', dataIndex: 'totalAmount', width: 110, render: formatMoney },
    { title: '已付', dataIndex: 'paidAmount', width: 110, render: formatMoney },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: status => <Tag color={ORDER_STATUS_COLOR[status]}>{ORDER_STATUS[status] || status}</Tag>,
    },
    { title: '创建时间', dataIndex: 'createdAt', width: 170, render: formatDateTime },
  ];

  return (
    <div className="finance-page">
      <h2 className="page-title">财务统计</h2>

      <div className="finance-toolbar">
        <RangePicker value={range} onChange={setRange} allowClear={false} />
        <div>
          <Button icon={<ReloadOutlined />} onClick={loadData} style={{ marginRight: 8 }}>刷新</Button>
          <Button type="primary" icon={<DownloadOutlined />} onClick={handleExport}>导出数据</Button>
        </div>
      </div>

      <Row gutter={[16, 16]}>
        <Col xs={24} md={8} xl={4}>
          <Card loading={loading} className="metric-card">
            <div className="metric-label">实收金额</div>
            <div className="metric-value accent">{formatMoney(summary.totalRevenue)}</div>
          </Card>
        </Col>
        <Col xs={24} md={8} xl={4}>
          <Card loading={loading} className="metric-card">
            <div className="metric-label">应收金额</div>
            <div className="metric-value">{formatMoney(summary.receivable)}</div>
          </Card>
        </Col>
        <Col xs={24} md={8} xl={4}>
          <Card loading={loading} className="metric-card">
            <div className="metric-label">未结清金额</div>
            <div className="metric-value cyan">{formatMoney(summary.unpaidAmount)}</div>
          </Card>
        </Col>
        <Col xs={24} md={8} xl={4}>
          <Card loading={loading} className="metric-card">
            <div className="metric-label">订单数</div>
            <div className="metric-value">{summary.orderCount || 0}</div>
          </Card>
        </Col>
        <Col xs={24} md={8} xl={4}>
          <Card loading={loading} className="metric-card">
            <div className="metric-label">未结清订单</div>
            <div className="metric-value">{summary.unpaidOrders || 0}</div>
          </Card>
        </Col>
        <Col xs={24} md={8} xl={4}>
          <Card loading={loading} className="metric-card">
            <div className="metric-label">退款订单</div>
            <div className="metric-value">{summary.refundOrders || 0}</div>
          </Card>
        </Col>
      </Row>

      <div className="finance-grid">
        <Card title="收入趋势" loading={loading}>
          {revenue.length ? (
            <div className="trend-list">
              {revenue.map(item => (
                <div className="trend-item" key={item.date}>
                  <span>{item.date}</span>
                  <div className="trend-bar" style={{ width: `${Math.max(Number(item.amount || 0) / maxRevenue * 100, 4)}%` }} />
                  <strong>{formatMoney(item.amount)}</strong>
                </div>
              ))}
            </div>
          ) : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />}
        </Card>

        <Card title="支付方式" loading={loading}>
          {methods.length ? (
            <div className="method-list">
              {methods.map(item => (
                <div className="method-row" key={item.method}>
                  <span>{PAYMENT_METHOD[item.method] || '未知'}</span>
                  <div className="trend-bar" />
                  <strong>{formatMoney(item.amount)} / {item.count} 单</strong>
                </div>
              ))}
            </div>
          ) : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />}
        </Card>
      </div>

      <Card title="未结清订单" style={{ marginTop: 16 }}>
        <Table columns={orderColumns} dataSource={unpaidOrders} rowKey="id" loading={loading} scroll={{ x: 900 }} />
      </Card>

      <Card title="退款订单" style={{ marginTop: 16 }}>
        <Table columns={orderColumns} dataSource={refundOrders} rowKey="id" loading={loading} scroll={{ x: 900 }} />
      </Card>
    </div>
  );
};

export default Finance;
