import React, { useState, useEffect } from 'react';
import { Card, Table, Button, Space, Tag, Input, Modal, Form, InputNumber, Select, message, Popconfirm } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, SwapOutlined } from '@ant-design/icons';
import { getRooms, createRoom, updateRoom, deleteRoom, updateRoomStatus } from '../../api/room';
import { getRoomTypes } from '../../api/roomType';
import { formatDateTime, formatMoney } from '../../utils/format';
import { ROOM_STATUS, ROOM_STATUS_COLOR } from '../../utils/constants';
import ImageUpload from '../../components/ImageUpload';

const { TextArea } = Input;
const { Option } = Select;

const Rooms = () => {
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState([]);
  const [roomTypes, setRoomTypes] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [keyword, setKeyword] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [roomTypeFilter, setRoomTypeFilter] = useState(null);
  const [modalVisible, setModalVisible] = useState(false);
  const [statusModalVisible, setStatusModalVisible] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [currentRoom, setCurrentRoom] = useState(null);
  const [form] = Form.useForm();
  const [statusForm] = Form.useForm();

  // 加载房型列表
  const loadRoomTypes = async () => {
    try {
      const response = await getRoomTypes({ page: 1, pageSize: 100 });
      setRoomTypes(response.data.records);
    } catch (error) {
      console.error('加载房型失败', error);
    }
  };

  // 加载房间数据
  const loadData = async () => {
    setLoading(true);
    try {
      const response = await getRooms({
        page,
        pageSize,
        keyword,
        status: statusFilter,
        roomTypeId: roomTypeFilter,
      });
      setDataSource(response.data.records);
      setTotal(response.data.total);
    } catch (error) {
      message.error(error.message || '加载失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRoomTypes();
  }, []);

  useEffect(() => {
    loadData();
  }, [page, pageSize, keyword, statusFilter, roomTypeFilter]);

  // 打开新增/编辑弹窗
  const handleOpenModal = (record) => {
    if (record) {
      setEditingId(record.id);
      form.setFieldsValue(record);
    } else {
      setEditingId(null);
      form.resetFields();
      form.setFieldsValue({ status: 'available', capacity: 2 });
    }
    setModalVisible(true);
  };

  // 打开状态修改弹窗
  const handleOpenStatusModal = (record) => {
    setCurrentRoom(record);
    statusForm.setFieldsValue({ status: record.status });
    setStatusModalVisible(true);
  };

  // 提交表单
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      if (editingId) {
        await updateRoom(editingId, values);
        message.success('更新成功');
      } else {
        await createRoom(values);
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

  // 修改房间状态
  const handleUpdateStatus = async () => {
    try {
      const values = await statusForm.validateFields();
      await updateRoomStatus(currentRoom.id, values);
      message.success('状态更新成功');
      setStatusModalVisible(false);
      loadData();
    } catch (error) {
      message.error(error.message || '状态更新失败');
    }
  };

  // 删除房间
  const handleDelete = async (id) => {
    try {
      await deleteRoom(id);
      message.success('删除成功');
      loadData();
    } catch (error) {
      message.error(error.message || '删除失败');
    }
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80,
    },
    {
      title: '房间号',
      dataIndex: 'roomNo',
      width: 120,
    },
    {
      title: '房间名称',
      dataIndex: 'name',
      width: 150,
    },
    {
      title: '楼层',
      dataIndex: 'floor',
      width: 80,
    },
    {
      title: '面积',
      dataIndex: 'area',
      width: 100,
      render: (area) => area ? `${area}㎡` : '-',
    },
    {
      title: '可住人数',
      dataIndex: 'capacity',
      width: 100,
      render: (capacity) => `${capacity}人`,
    },
    {
      title: '价格',
      dataIndex: 'price',
      width: 120,
      render: (price) => formatMoney(price),
    },
    {
      title: '房间状态',
      dataIndex: 'status',
      width: 120,
      render: (status) => (
        <Tag color={ROOM_STATUS_COLOR[status]}>
          {ROOM_STATUS[status]}
        </Tag>
      ),
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
      width: 200,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<SwapOutlined />}
            onClick={() => handleOpenStatusModal(record)}
          >
            状态
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
            title="确定要删除这个房间吗？"
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

  return (
    <div className="rooms">
      <h2 className="page-title">房间管理</h2>

      <Card>
        <Space style={{ marginBottom: 16 }} wrap>
          <Input.Search
            placeholder="搜索房间号/名称"
            allowClear
            style={{ width: 250 }}
            onSearch={(value) => {
              setKeyword(value);
              setPage(1);
            }}
          />
          <Select
            placeholder="房间状态"
            allowClear
            style={{ width: 150 }}
            onChange={(value) => {
              setStatusFilter(value || '');
              setPage(1);
            }}
          >
            {Object.keys(ROOM_STATUS).map(key => (
              <Option key={key} value={key}>{ROOM_STATUS[key]}</Option>
            ))}
          </Select>
          <Select
            placeholder="房型"
            allowClear
            style={{ width: 150 }}
            onChange={(value) => {
              setRoomTypeFilter(value);
              setPage(1);
            }}
          >
            {roomTypes.map(type => (
              <Option key={type.id} value={type.id}>{type.name}</Option>
            ))}
          </Select>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => handleOpenModal(null)}
          >
            新增房间
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
        title={editingId ? '编辑房间' : '新增房间'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={900}
        centered
        className="room-modal"
        destroyOnClose
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{ status: 'available', capacity: 2 }}
        >
          <div className="room-form-grid">
            <div>
              <Form.Item
                label="房间号"
                name="roomNo"
                rules={[{ required: true, message: '请输入房间号' }]}
              >
                <Input placeholder="例如：101" />
              </Form.Item>

              <Form.Item
                label="房间名称"
                name="name"
                rules={[{ required: true, message: '请输入房间名称' }]}
              >
                <Input placeholder="例如：星河大床房-101" />
              </Form.Item>

              <Form.Item
                label="房型"
                name="roomTypeId"
                rules={[{ required: true, message: '请选择房型' }]}
              >
                <Select placeholder="请选择房型">
                  {roomTypes.map(type => (
                    <Option key={type.id} value={type.id}>{type.name}</Option>
                  ))}
                </Select>
              </Form.Item>

              <Form.Item label="楼层" name="floor">
                <Input placeholder="例如：1F" />
              </Form.Item>

              <Form.Item label="面积" name="area">
                <InputNumber
                  style={{ width: '100%' }}
                  placeholder="32"
                  min={0}
                  precision={1}
                  addonAfter="㎡"
                />
              </Form.Item>
            </div>

            <div>
              <Form.Item
                label="可住人数"
                name="capacity"
                rules={[{ required: true, message: '请输入可住人数' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  placeholder="2"
                  min={1}
                  addonAfter="人"
                />
              </Form.Item>

              <Form.Item
                label="价格"
                name="price"
                rules={[{ required: true, message: '请输入价格' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  placeholder="399"
                  min={0}
                  precision={2}
                  addonAfter="元"
                />
              </Form.Item>

              <Form.Item label="配套设施" name="facilities">
                <Input placeholder="例如：独立卫浴,投影,浴缸" />
              </Form.Item>

              <Form.Item label="房间图片" name="imageUrl">
                <ImageUpload />
              </Form.Item>

              <Form.Item label="备注" name="remark">
                <TextArea rows={3} placeholder="请输入备注" />
              </Form.Item>
            </div>
          </div>
        </Form>
      </Modal>

      {/* 修改状态弹窗 */}
      <Modal
        title="修改房间状态"
        open={statusModalVisible}
        onOk={handleUpdateStatus}
        onCancel={() => setStatusModalVisible(false)}
        width={400}
        destroyOnClose
      >
        <Form form={statusForm} layout="vertical">
          <Form.Item
            label="房间状态"
            name="status"
            rules={[{ required: true, message: '请选择房间状态' }]}
          >
            <Select placeholder="请选择状态">
              {Object.keys(ROOM_STATUS).map(key => (
                <Option key={key} value={key}>
                  <Tag color={ROOM_STATUS_COLOR[key]}>{ROOM_STATUS[key]}</Tag>
                </Option>
              ))}
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default Rooms;
