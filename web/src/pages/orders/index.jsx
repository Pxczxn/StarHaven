import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Tag, Input, Select, DatePicker, Modal, message, Popconfirm } from 'antd';
import { PlusOutlined, SearchOutlined, EyeOutlined, EditOutlined, DeleteOutlined, CheckOutlined, LoginOutlined, LogoutOutlined, CloseOutlined } from '@ant-design/icons';
import { getOrders, deleteOrder, confirmOrder, checkInOrder, checkOutOrder, cancelOrder } from '../../api/order';
import { ORDER_STATUS, ORDER_SOURCE, PAYMENT_METHOD } from '../../utils/constants';
import OrderFormModal from './OrderFormModal';
import OrderDetailModal from './OrderDetailModal';
import CancelOrderModal from './CancelOrderModal';
import PaymentModal from './PaymentModal';
import dayjs from 'dayjs';

const { RangePicker } = DatePicker;

const OrderList = () => {
  const [loading, setLoading] = useState(false);
  const [orders, setOrders] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [keyword, setKeyword] = useState('');
  const [status, setStatus] = useState('');
  const [dateRange, setDateRange] = useState(null);

  const [formModalVisible, setFormModalVisible] = useState(false);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [cancelModalVisible, setCancelModalVisible] = useState(false);
  const [paymentModalVisible, setPaymentModalVisible] = useState(false);
  const [currentOrder, setCurrentOrder] = useState(null);

  useEffect(() => {
    fetchOrders();
  }, [page, pageSize, keyword, status, dateRange]);

  const fetchOrders = async () => {
    setLoading(true);
    try {
      const params = {
        page,
        pageSize,
        keyword,
        status,
      };

      if (dateRange && dateRange.length === 2) {
        params.startDate = dateRange[0].format('YYYY-MM-DD');
        params.endDate = dateRange[1].format('YYYY-MM-DD');
      }

      const res = await getOrders(params);
      setOrders(res.data.records);
      setTotal(res.data.total);
    } catch (error) {
      message.error('获取订单列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    setPage(1);
    fetchOrders();
  };

  const handleReset = () => {
    setKeyword('');
    setStatus('');
    setDateRange(null);
    setPage(1);
  };

  const handleCreate = () => {
    setCurrentOrder(null);
    setFormModalVisible(true);
  };

  const handleEdit = (record) => {
    setCurrentOrder(record);
    setFormModalVisible(true);
  };

  const handleDetail = (record) => {
    setCurrentOrder(record);
    setDetailModalVisible(true);
  };

  const handleDelete = async (id) => {
    try {
      await deleteOrder(id);
      message.success('删除成功');
      fetchOrders();
    } catch (error) {
      message.error(error.message || '删除失败');
    }
  };

  const handleConfirm = async (record) => {
    try {
      await confirmOrder(record.id);
      message.success('订单已确认');
      fetchOrders();
    } catch (error) {
      message.error(error.message || '确认失败');
    }
  };

  const handleCheckIn = async (record) => {
    try {
      await checkInOrder(record.id);
      message.success('入住办理成功');
      fetchOrders();
    } catch (error) {
      message.error(error.message || '入住办理失败');
    }
  };

  const handleCheckOut = async (record) => {
    try {
      await checkOutOrder(record.id);
      message.success('退房办理成功');
      fetchOrders();
    } catch (error) {
      message.error(error.message || '退房办理失败');
    }
  };

  const handleCancel = (record) => {
    setCurrentOrder(record);
    setCancelModalVisible(true);
  };

  const handlePayment = (record) => {
    setCurrentOrder(record);
    setPaymentModalVisible(true);
  };

  const handleCancelSuccess = async () => {
    if (!currentOrder) return;
    try {
      await cancelOrder(currentOrder.id);
      message.success('订单已取消');
      setCancelModalVisible(false);
      setCurrentOrder(null);
      fetchOrders();
    } catch (error) {
      message.error(error.message || '取消失败');
    }
  };

  const handlePaymentSuccess = async () => {
    message.success('收款成功');
    setPaymentModalVisible(false);
    setCurrentOrder(null);
    fetchOrders();
  };

  const getStatusColor = (status) => {
    const colorMap = {
      pending: 'orange',
      reserved: 'blue',
      occupied: 'green',
      completed: 'default',
      cancelled: 'red',
      refunded: 'purple',
    };
    return colorMap[status] || 'default';
  };

  const columns = [
    {
      title: '订单编号',
      dataIndex: 'orderNo',
      key: 'orderNo',
      width: 180,
    },
    {
      title: '客户姓名',
      dataIndex: 'customerName',
      key: 'customerName',
      width: 100,
    },
    {
      title: '客户手机',
      dataIndex: 'customerPhone',
      key: 'customerPhone',
      width: 120,
    },
    {
      title: '房间号',
      dataIndex: 'roomNo',
      key: 'roomNo',
      width: 100,
    },
    {
      title: '入住日期',
      dataIndex: 'checkInDate',
      key: 'checkInDate',
      width: 110,
    },
    {
      title: '离店日期',
      dataIndex: 'checkOutDate',
      key: 'checkOutDate',
      width: 110,
    },
    {
      title: '晚数',
      dataIndex: 'nights',
      key: 'nights',
      width: 60,
      align: 'center',
    },
    {
      title: '订单金额',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      width: 100,
      align: 'right',
      render: (text) => `¥${text}`,
    },
    {
      title: '已付金额',
      dataIndex: 'paidAmount',
      key: 'paidAmount',
      width: 100,
      align: 'right',
      render: (text) => `¥${text}`,
    },
    {
      title: '订单来源',
      dataIndex: 'source',
      key: 'source',
      width: 80,
      render: (text) => ORDER_SOURCE[text] || text,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 90,
      render: (text) => (
        <Tag color={getStatusColor(text)}>{ORDER_STATUS[text] || text}</Tag>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 280,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handleDetail(record)}
          >
            详情
          </Button>

          {(record.status === 'pending' || record.status === 'reserved') && (
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
            >
              编辑
            </Button>
          )}

          {record.status === 'pending' && (
            <Button
              type="link"
              size="small"
              icon={<CheckOutlined />}
              onClick={() => handleConfirm(record)}
            >
              确认
            </Button>
          )}

          {(record.status === 'reserved' || record.status === 'pending') && (
            <Button
              type="link"
              size="small"
              icon={<LoginOutlined />}
              onClick={() => handleCheckIn(record)}
            >
              入住
            </Button>
          )}

          {record.status === 'occupied' && (
            <Button
              type="link"
              size="small"
              icon={<LogoutOutlined />}
              onClick={() => handleCheckOut(record)}
            >
              退房
            </Button>
          )}

          {(record.status === 'pending' || record.status === 'reserved') && (
            <Button
              type="link"
              size="small"
              danger
              icon={<CloseOutlined />}
              onClick={() => handleCancel(record)}
            >
              取消
            </Button>
          )}

          {record.status !== 'cancelled' && record.status !== 'completed' && record.paymentStatus !== 'paid' && (
            <Button
              type="link"
              size="small"
              onClick={() => handlePayment(record)}
            >
              收款
            </Button>
          )}

          {record.status !== 'occupied' && (
            <Popconfirm
              title="确定删除该订单吗？"
              onConfirm={() => handleDelete(record.id)}
              okText="确定"
              cancelText="取消"
            >
              <Button
                type="link"
                size="small"
                danger
                icon={<DeleteOutlined />}
              >
                删除
              </Button>
            </Popconfirm>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Space wrap>
          <Input
            placeholder="搜索订单号/客户姓名/手机号"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            onPressEnter={handleSearch}
            style={{ width: 240 }}
            prefix={<SearchOutlined />}
          />
          <Select
            placeholder="订单状态"
            value={status || undefined}
            onChange={setStatus}
            style={{ width: 120 }}
            allowClear
          >
            {Object.keys(ORDER_STATUS).map((key) => (
              <Select.Option key={key} value={key}>
                {ORDER_STATUS[key]}
              </Select.Option>
            ))}
          </Select>
          <RangePicker
            value={dateRange}
            onChange={setDateRange}
            placeholder={['入住日期', '离店日期']}
          />
          <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
            搜索
          </Button>
          <Button onClick={handleReset}>重置</Button>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            新增订单
          </Button>
        </Space>
      </div>

      <Table
        columns={columns}
        dataSource={orders}
        rowKey="id"
        loading={loading}
        pagination={{
          current: page,
          pageSize: pageSize,
          total: total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (page, pageSize) => {
            setPage(page);
            setPageSize(pageSize);
          },
        }}
      />

      <OrderFormModal
        open={formModalVisible}
        order={currentOrder}
        onClose={() => {
          setFormModalVisible(false);
          setCurrentOrder(null);
        }}
        onSuccess={() => {
          setFormModalVisible(false);
          setCurrentOrder(null);
          fetchOrders();
        }}
      />

      <OrderDetailModal
        open={detailModalVisible}
        order={currentOrder}
        onClose={() => {
          setDetailModalVisible(false);
          setCurrentOrder(null);
        }}
      />

      <CancelOrderModal
        open={cancelModalVisible}
        order={currentOrder}
        onClose={() => {
          setCancelModalVisible(false);
          setCurrentOrder(null);
        }}
        onSuccess={() => {
          handleCancelSuccess();
        }}
      />

      <PaymentModal
        open={paymentModalVisible}
        order={currentOrder}
        onClose={() => {
          setPaymentModalVisible(false);
          setCurrentOrder(null);
        }}
        onSuccess={() => {
          handlePaymentSuccess();
        }}
      />
    </div>
  );
};

export default OrderList;
