import React, { useEffect, useState } from 'react';
import { Button, Card, Form, Input, message, Modal, Popconfirm, Select, Space, Switch, Table, Tag } from 'antd';
import { DeleteOutlined, EditOutlined, KeyOutlined, PlusOutlined } from '@ant-design/icons';
import { createUser, deleteUser, getUsers, updateUser, updateUserPassword, updateUserStatus } from '../../api/user';
import { formatDateTime } from '../../utils/format';
import './index.css';

const { Option } = Select;

const ROLE_LABEL = {
  admin: '管理员',
  landlord: '房东',
  tenant: '租客',
};

const Users = () => {
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [keyword, setKeyword] = useState('');
  const [role, setRole] = useState('');
  const [status, setStatus] = useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const [passwordVisible, setPasswordVisible] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [passwordUser, setPasswordUser] = useState(null);
  const [form] = Form.useForm();
  const [passwordForm] = Form.useForm();

  const loadData = async () => {
    setLoading(true);
    try {
      const response = await getUsers({ page, pageSize, keyword, role, status });
      setDataSource(response.data.records);
      setTotal(response.data.total);
    } catch (error) {
      message.error(error.message || '加载账号失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [page, pageSize, keyword, role, status]);

  const openModal = (record) => {
    if (record) {
      setEditingId(record.id);
      form.setFieldsValue(record);
    } else {
      setEditingId(null);
      form.resetFields();
      form.setFieldsValue({ role: 'landlord', status: 'enabled' });
    }
    setModalVisible(true);
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editingId) {
        await updateUser(editingId, values);
        message.success('账号已更新');
      } else {
        await createUser(values);
        message.success('账号已创建');
      }
      setModalVisible(false);
      loadData();
    } catch (error) {
      if (error.errorFields) return;
      message.error(error.message || '保存失败');
    }
  };

  const openPasswordModal = (record) => {
    setPasswordUser(record);
    passwordForm.resetFields();
    setPasswordVisible(true);
  };

  const handlePasswordSubmit = async () => {
    try {
      const values = await passwordForm.validateFields();
      await updateUserPassword(passwordUser.id, values.password);
      message.success('密码已更新');
      setPasswordVisible(false);
    } catch (error) {
      if (error.errorFields) return;
      message.error(error.message || '修改密码失败');
    }
  };

  const handleStatusChange = async (record, checked) => {
    try {
      await updateUserStatus(record.id, checked ? 'enabled' : 'disabled');
      message.success('状态已更新');
      loadData();
    } catch (error) {
      message.error(error.message || '状态更新失败');
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteUser(id);
      message.success('账号已删除');
      loadData();
    } catch (error) {
      message.error(error.message || '删除失败');
    }
  };

  const columns = [
    { title: '用户名', dataIndex: 'username', width: 140 },
    { title: '姓名', dataIndex: 'realName', width: 120 },
    { title: '手机号', dataIndex: 'phone', width: 140 },
    {
      title: '角色',
      dataIndex: 'role',
      width: 100,
      render: value => <Tag color={value === 'admin' ? 'gold' : 'cyan'}>{ROLE_LABEL[value] || value}</Tag>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 120,
      render: (value, record) => (
        <Switch
          checked={value === 'enabled'}
          checkedChildren="启用"
          unCheckedChildren="禁用"
          onChange={(checked) => handleStatusChange(record, checked)}
        />
      ),
    },
    { title: '创建时间', dataIndex: 'createdAt', width: 170, render: formatDateTime },
    {
      title: '操作',
      key: 'action',
      width: 220,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => openModal(record)}>编辑</Button>
          <Button type="link" size="small" icon={<KeyOutlined />} onClick={() => openPasswordModal(record)}>密码</Button>
          <Popconfirm title="确定删除这个账号吗？" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div className="users-page">
      <h2 className="page-title">账号管理</h2>
      <Card>
        <div className="user-filters">
          <Input.Search
            placeholder="搜索用户名/姓名/手机号"
            allowClear
            style={{ width: 260 }}
            onSearch={(value) => { setKeyword(value); setPage(1); }}
          />
          <Select placeholder="角色" allowClear style={{ width: 140 }} onChange={(value) => { setRole(value || ''); setPage(1); }}>
            {Object.entries(ROLE_LABEL).map(([key, label]) => <Option key={key} value={key}>{label}</Option>)}
          </Select>
          <Select placeholder="状态" allowClear style={{ width: 140 }} onChange={(value) => { setStatus(value || ''); setPage(1); }}>
            <Option value="enabled">启用</Option>
            <Option value="disabled">禁用</Option>
          </Select>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => openModal(null)}>新增账号</Button>
        </div>

        <Table
          columns={columns}
          dataSource={dataSource}
          rowKey="id"
          loading={loading}
          scroll={{ x: 1000 }}
          pagination={{
            current: page,
            pageSize,
            total,
            showSizeChanger: true,
            showTotal: value => `共 ${value} 条`,
            onChange: (nextPage, nextSize) => {
              setPage(nextPage);
              setPageSize(nextSize);
            },
          }}
        />
      </Card>

      <Modal
        title={editingId ? '编辑账号' : '新增账号'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={620}
        destroyOnHidden
      >
        <Form form={form} layout="vertical">
          <Form.Item label="用户名" name="username" rules={[{ required: true, message: '请输入用户名' }]}>
            <Input placeholder="用于登录后台" />
          </Form.Item>
          {!editingId && (
            <Form.Item label="初始密码" name="password" rules={[{ required: true, message: '请输入初始密码' }, { min: 6, message: '密码至少 6 位' }]}>
              <Input.Password placeholder="至少 6 位" />
            </Form.Item>
          )}
          <Form.Item label="姓名" name="realName" rules={[{ required: true, message: '请输入姓名' }]}>
            <Input placeholder="请输入姓名" />
          </Form.Item>
          <Form.Item label="手机号" name="phone">
            <Input placeholder="请输入手机号" maxLength={11} />
          </Form.Item>
          <Form.Item label="角色" name="role" rules={[{ required: true, message: '请选择角色' }]}>
            <Select>
              <Option value="admin">管理员</Option>
              <Option value="landlord">房东</Option>
            </Select>
          </Form.Item>
          <Form.Item label="状态" name="status" rules={[{ required: true, message: '请选择状态' }]}>
            <Select>
              <Option value="enabled">启用</Option>
              <Option value="disabled">禁用</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={`修改密码：${passwordUser?.username || ''}`}
        open={passwordVisible}
        onOk={handlePasswordSubmit}
        onCancel={() => setPasswordVisible(false)}
        destroyOnHidden
      >
        <Form form={passwordForm} layout="vertical">
          <Form.Item label="新密码" name="password" rules={[{ required: true, message: '请输入新密码' }, { min: 6, message: '密码至少 6 位' }]}>
            <Input.Password placeholder="至少 6 位" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default Users;
