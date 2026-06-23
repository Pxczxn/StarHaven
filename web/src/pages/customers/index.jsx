import React, { useState, useEffect } from 'react';
import { Card, Table, Button, Space, Input, Modal, Form, Select, message, Popconfirm, Drawer } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, HistoryOutlined } from '@ant-design/icons';
import { getCustomers, createCustomer, updateCustomer, deleteCustomer, getCustomerOrders } from '../../api/customer';
import { formatDateTime, formatPhone, formatIdNumber } from '../../utils/format';
import { ORDER_STATUS, ORDER_STATUS_COLOR } from '../../utils/constants';

const { TextArea } = Input;
const { Option } = Select;

const Customers = () => {
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [keyword, setKeyword] = useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const [drawerVisible, setDrawerVisible] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [currentCustomer, setCurrentCustomer] = useState(null);
  const [orders, setOrders] = useState([]);
  const [ordersLoading, setOrdersLoading] = useState(false);
  const [form] = Form.useForm();

  // 加载客户数据
  const loadData = async () => {
    setLoading(true);
    try {
      const response = await getCustomers({ page, pageSize, keyword });
      setDataSource(response.data.records);
      setTotal(response.data.total);
    } catch (error) {
      message.error(error.message || '加载失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [page, pageSize, keyword]);

  // 打开新增/编辑弹窗
  const handleOpenModal = (record) => {
    if (record) {
      setEditingId(record.id);
      form.setFieldsValue(record);
    } else {
      setEditingId(null);
      form.resetFields();
    }
    setModalVisible(true);
  };

  // 提交表单
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      if (editingId) {
        await updateCustomer(editingId, values);
        message.success('更新成功');
      } else {
        await createCustomer(values);
        message.success('创建成功');
      }

      setModalVisible(false);
      loadData();
    } catch (error) {
      if (error.errorFields) {
        return;
      }
      message.error(error.message || '操作失败');
    }
  };

  // 删除客户
  const handleDelete = async (id) => {
    try {
      await deleteCustomer(id);
      message.success('删除成功');
      loadData();
    } catch (error) {
      message.error(error.message || '删除失败');
    }
  };

  // 查看历史订单
  const handleViewOrders = async (record) => {
    setCurrentCustomer(record);
    setDrawerVisible(true);
    setOrdersLoading(true);
    try {
      const response = await getCustomerOrders(record.id);
      setOrders(response.data || []);
    } catch (error) {
      message.error(error.message || '加载订单失败');
      setOrders([]);
    } finally {
      setOrdersLoading(false);
    }
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80,
    },
    {
      title: '姓名',
      dataIndex: 'name',
      width: 120,
    },
    {
      title: '性别',
      dataIndex: 'gender',
      width: 80,
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      width: 150,
      render: (phone) => formatPhone(phone),
    },
    {
      title: '证件类型',
      dataIndex: 'idType',
      width: 100,
    },
    {
      title: '证件号码',
      dataIndex: 'idNumber',
      width: 180,
      render: (idNumber) => formatIdNumber(idNumber),
    },
    {
      title: '客户来源',
      dataIndex: 'source',
      width: 100,
    },
    {
      title: '会员等级',
      dataIndex: 'level',
      width: 100,
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      width: 180,
      render: (time) => formatDateTime(time),
    },
    {
      title: '操作',
      key: 'action',
      width: 220,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<HistoryOutlined />}
            onClick={() => handleViewOrders(record)}
          >
            订单
          </Button>
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleOpenModal(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除这个客户吗？"
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
        </Space>
      ),
    },
  ];

  const orderColumns = [
    {
      title: '订单编号',
      dataIndex: 'orderNo',
      width: 180,
    },
    {
      title: '房间号',
      dataIndex: 'roomNo',
      width: 100,
    },
    {
      title: '入住日期',
      dataIndex: 'checkInDate',
      width: 120,
    },
    {
      title: '离店日期',
      dataIndex: 'checkOutDate',
      width: 120,
    },
    {
      title: '订单状态',
      dataIndex: 'status',
      width: 100,
      render: (status) => (
        <span style={{ color: ORDER_STATUS_COLOR[status] }}>
          {ORDER_STATUS[status]}
        </span>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      width: 180,
      render: (time) => formatDateTime(time),
    },
  ];

  return (
    <div className="customers">
      <h2 className="page-title">客户管理</h2>

      <Card>
        <Space style={{ marginBottom: 16 }}>
          <Input.Search
            placeholder="搜索姓名/手机号/证件号"
            allowClear
            style={{ width: 300 }}
            onSearch={(value) => {
              setKeyword(value);
              setPage(1);
            }}
          />
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => handleOpenModal(null)}
          >
            新增客户
          </Button>
        </Space>

        <Table
          columns={columns}
          dataSource={dataSource}
          rowKey="id"
          loading={loading}
          scroll={{ x: 1400 }}
          pagination={{
            current: page,
            pageSize: pageSize,
            total: total,
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条`,
            onChange: (page, pageSize) => {
              setPage(page);
              setPageSize(pageSize);
            },
          }}
        />
      </Card>

      {/* 新增/编辑弹窗 */}
      <Modal
        title={editingId ? '编辑客户' : '新增客户'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        size="large"
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          <Form.Item
            label="客户姓名"
            name="name"
            rules={[{ required: true, message: '请输入客户姓名' }]}
          >
            <Input placeholder="请输入客户姓名" />
          </Form.Item>

          <Form.Item label="性别" name="gender">
            <Select placeholder="请选择性别" allowClear>
              <Option value="男">男</Option>
              <Option value="女">女</Option>
            </Select>
          </Form.Item>

          <Form.Item
            label="手机号"
            name="phone"
            rules={[
              { required: true, message: '请输入手机号' },
              { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确' }
            ]}
          >
            <Input placeholder="请输入手机号" />
          </Form.Item>

          <Form.Item label="证件类型" name="idType">
            <Select placeholder="请选择证件类型" allowClear>
              <Option value="身份证">身份证</Option>
              <Option value="护照">护照</Option>
              <Option value="港澳通行证">港澳通行证</Option>
              <Option value="台湾通行证">台湾通行证</Option>
            </Select>
          </Form.Item>

          <Form.Item label="证件号码" name="idNumber">
            <Input placeholder="请输入证件号码" />
          </Form.Item>

          <Form.Item label="生日" name="birthday">
            <Input placeholder="例如：1990-01-01" />
          </Form.Item>

          <Form.Item label="客户来源" name="source">
            <Select placeholder="请选择客户来源" allowClear>
              <Option value="h5">H5</Option>
              <Option value="wechat">微信</Option>
              <Option value="phone">电话</Option>
              <Option value="front_desk">前台</Option>
              <Option value="other">其他</Option>
            </Select>
          </Form.Item>

          <Form.Item label="会员等级" name="level">
            <Select placeholder="请选择会员等级" allowClear>
              <Option value="normal">普通</Option>
              <Option value="silver">银卡</Option>
              <Option value="gold">金卡</Option>
              <Option value="platinum">白金</Option>
            </Select>
          </Form.Item>

          <Form.Item label="备注" name="remark">
            <TextArea rows={3} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>

      {/* 历史订单抽屉 */}
      <Drawer
        title={`${currentCustomer?.name} 的历史订单`}
        placement="right"
        width={800}
        open={drawerVisible}
        onClose={() => setDrawerVisible(false)}
      >
        <Table
          columns={orderColumns}
          dataSource={orders}
          rowKey="id"
          loading={ordersLoading}
          pagination={false}
        />
      </Drawer>
    </div>
  );
};

export default Customers;
