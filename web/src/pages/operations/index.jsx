import React, { useEffect, useState } from 'react';
import { Button, Card, DatePicker, Form, Input, message, Modal, Popconfirm, Select, Space, Table, Tag } from 'antd';
import { CheckCircleOutlined, DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { getAllRooms } from '../../api/room';
import {
  createOperationTask,
  deleteOperationTask,
  getOperationTasks,
  updateOperationTask,
  updateOperationTaskStatus,
} from '../../api/operationTask';
import { formatDateTime } from '../../utils/format';
import { TASK_STATUS, TASK_TYPE } from '../../utils/constants';
import './index.css';

const { Option } = Select;
const { TextArea } = Input;

const PRIORITY = {
  low: { text: '低', color: 'default' },
  normal: { text: '普通', color: 'processing' },
  high: { text: '高', color: 'warning' },
  urgent: { text: '紧急', color: 'error' },
};

const Operations = () => {
  const [loading, setLoading] = useState(false);
  const [rooms, setRooms] = useState([]);
  const [dataSource, setDataSource] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [roomId, setRoomId] = useState(null);
  const [taskType, setTaskType] = useState('');
  const [status, setStatus] = useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [form] = Form.useForm();

  const loadRooms = async () => {
    try {
      const response = await getAllRooms();
      setRooms(response.data || []);
    } catch (error) {
      message.error(error.message || '加载房间失败');
    }
  };

  const loadData = async () => {
    setLoading(true);
    try {
      const response = await getOperationTasks({ page, pageSize, roomId, taskType, status });
      setDataSource(response.data.records);
      setTotal(response.data.total);
    } catch (error) {
      message.error(error.message || '加载任务失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRooms();
  }, []);

  useEffect(() => {
    loadData();
  }, [page, pageSize, roomId, taskType, status]);

  const openModal = (record) => {
    if (record) {
      setEditingId(record.id);
      form.setFieldsValue({
        ...record,
        dueDate: record.dueDate ? dayjs(record.dueDate) : null,
      });
    } else {
      setEditingId(null);
      form.resetFields();
      form.setFieldsValue({ taskType: 'cleaning', priority: 'normal', status: 'pending' });
    }
    setModalVisible(true);
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const payload = {
        ...values,
        dueDate: values.dueDate ? values.dueDate.format('YYYY-MM-DDTHH:mm:ss') : null,
      };
      if (editingId) {
        await updateOperationTask(editingId, payload);
        message.success('任务已更新');
      } else {
        await createOperationTask(payload);
        message.success('任务已创建');
      }
      setModalVisible(false);
      loadData();
    } catch (error) {
      if (error.errorFields) return;
      message.error(error.message || '保存失败');
    }
  };

  const handleComplete = async (record) => {
    try {
      await updateOperationTaskStatus(record.id, 'completed');
      message.success('任务已完成');
      loadData();
    } catch (error) {
      message.error(error.message || '操作失败');
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteOperationTask(id);
      message.success('任务已删除');
      loadData();
    } catch (error) {
      message.error(error.message || '删除失败');
    }
  };

  const columns = [
    { title: '任务标题', dataIndex: 'title', width: 180 },
    { title: '房间', dataIndex: 'roomNo', width: 100 },
    {
      title: '类型',
      dataIndex: 'taskType',
      width: 100,
      render: value => <Tag color={value === 'maintenance' ? 'error' : value === 'cleaning' ? 'cyan' : 'purple'}>{TASK_TYPE[value] || value}</Tag>,
    },
    {
      title: '优先级',
      dataIndex: 'priority',
      width: 100,
      render: value => <Tag color={PRIORITY[value]?.color}>{PRIORITY[value]?.text || value}</Tag>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 110,
      render: value => <Tag color={value === 'completed' ? 'success' : value === 'processing' ? 'processing' : 'warning'}>{TASK_STATUS[value] || value}</Tag>,
    },
    { title: '负责人', dataIndex: 'assignedTo', width: 120 },
    { title: '截止时间', dataIndex: 'dueDate', width: 170, render: formatDateTime },
    { title: '创建时间', dataIndex: 'createdAt', width: 170, render: formatDateTime },
    {
      title: '操作',
      key: 'action',
      width: 220,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          {record.status !== 'completed' && (
            <Button type="link" size="small" icon={<CheckCircleOutlined />} onClick={() => handleComplete(record)}>完成</Button>
          )}
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => openModal(record)}>编辑</Button>
          <Popconfirm title="确定删除这个任务吗？" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div className="operations-page">
      <h2 className="page-title">运营任务</h2>
      <Card>
        <div className="operation-filters">
          <Select placeholder="房间" allowClear style={{ width: 180 }} onChange={(value) => { setRoomId(value); setPage(1); }}>
            {rooms.map(room => <Option key={room.id} value={room.id}>{room.roomNo} {room.name}</Option>)}
          </Select>
          <Select placeholder="任务类型" allowClear style={{ width: 140 }} onChange={(value) => { setTaskType(value || ''); setPage(1); }}>
            {Object.keys(TASK_TYPE).map(key => <Option key={key} value={key}>{TASK_TYPE[key]}</Option>)}
          </Select>
          <Select placeholder="任务状态" allowClear style={{ width: 140 }} onChange={(value) => { setStatus(value || ''); setPage(1); }}>
            {Object.keys(TASK_STATUS).map(key => <Option key={key} value={key}>{TASK_STATUS[key]}</Option>)}
          </Select>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => openModal(null)}>新增任务</Button>
        </div>

        <Table
          columns={columns}
          dataSource={dataSource}
          rowKey="id"
          loading={loading}
          scroll={{ x: 1200 }}
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
        title={editingId ? '编辑任务' : '新增任务'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={720}
        destroyOnHidden
      >
        <Form form={form} layout="vertical">
          <Form.Item label="任务标题" name="title" rules={[{ required: true, message: '请输入任务标题' }]}>
            <Input placeholder="例如：101 退房后清洁" />
          </Form.Item>
          <Form.Item label="房间" name="roomId" rules={[{ required: true, message: '请选择房间' }]}>
            <Select placeholder="请选择房间" showSearch optionFilterProp="children">
              {rooms.map(room => <Option key={room.id} value={room.id}>{room.roomNo} {room.name}</Option>)}
            </Select>
          </Form.Item>
          <Form.Item label="任务类型" name="taskType" rules={[{ required: true, message: '请选择任务类型' }]}>
            <Select>
              {Object.keys(TASK_TYPE).map(key => <Option key={key} value={key}>{TASK_TYPE[key]}</Option>)}
            </Select>
          </Form.Item>
          <Form.Item label="优先级" name="priority">
            <Select>
              {Object.entries(PRIORITY).map(([key, item]) => <Option key={key} value={key}>{item.text}</Option>)}
            </Select>
          </Form.Item>
          <Form.Item label="状态" name="status">
            <Select>
              {Object.keys(TASK_STATUS).map(key => <Option key={key} value={key}>{TASK_STATUS[key]}</Option>)}
            </Select>
          </Form.Item>
          <Form.Item label="负责人" name="assignedTo">
            <Input placeholder="请输入负责人" />
          </Form.Item>
          <Form.Item label="截止时间" name="dueDate">
            <DatePicker showTime style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item label="任务描述" name="description">
            <TextArea rows={3} placeholder="补充任务说明" />
          </Form.Item>
          <Form.Item label="备注" name="remark">
            <TextArea rows={2} placeholder="可选" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default Operations;
