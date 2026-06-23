import React, { useState, useEffect } from 'react';
import { Card, Table, Button, Space, Tag, Input, Modal, Form, InputNumber, message, Popconfirm } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined } from '@ant-design/icons';
import { getRoomTypes, createRoomType, updateRoomType, deleteRoomType } from '../../api/roomType';
import { formatDateTime, formatMoney } from '../../utils/format';
import ImageUpload from '../../components/ImageUpload';

const { TextArea } = Input;

const RoomTypes = () => {
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [keyword, setKeyword] = useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [form] = Form.useForm();

  // 加载数据
  const loadData = async () => {
    setLoading(true);
    try {
      const response = await getRoomTypes({ page, pageSize, keyword });
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
      form.setFieldsValue({ status: 'enabled', capacity: 2 });
    }
    setModalVisible(true);
  };

  // 提交表单
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      if (editingId) {
        await updateRoomType(editingId, values);
        message.success('更新成功');
      } else {
        await createRoomType(values);
        message.success('创建成功');
      }

      setModalVisible(false);
      loadData();
    } catch (error) {
      if (error.errorFields) {
        // 表单验证失败
        return;
      }
      message.error(error.message || '操作失败');
    }
  };

  // 删除房型
  const handleDelete = async (id) => {
    try {
      await deleteRoomType(id);
      message.success('删除成功');
      loadData();
    } catch (error) {
      message.error(error.message || '删除失败');
    }
  };

  // 搜索
  const handleSearch = (value) => {
    setKeyword(value);
    setPage(1);
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80,
    },
    {
      title: '房型名称',
      dataIndex: 'name',
      width: 150,
    },
    {
      title: '默认价格',
      dataIndex: 'defaultPrice',
      width: 120,
      render: (price) => formatMoney(price),
    },
    {
      title: '可住人数',
      dataIndex: 'capacity',
      width: 100,
      render: (capacity) => `${capacity}人`,
    },
    {
      title: '床型',
      dataIndex: 'bedType',
      width: 150,
    },
    {
      title: '早餐',
      dataIndex: 'breakfast',
      width: 100,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (status) => (
        <Tag color={status === 'enabled' ? 'success' : 'default'}>
          {status === 'enabled' ? '启用' : '禁用'}
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
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleOpenModal(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除这个房型吗？"
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
    <div className="room-types">
      <h2 className="page-title">房型管理</h2>

      <Card>
        <Space style={{ marginBottom: 16 }}>
          <Input.Search
            placeholder="搜索房型名称"
            allowClear
            style={{ width: 300 }}
            onSearch={handleSearch}
          />
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => handleOpenModal(null)}
          >
            新增房型
          </Button>
        </Space>

        <Table
          columns={columns}
          dataSource={dataSource}
          rowKey="id"
          loading={loading}
          scroll={{ x: 1200 }}
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

      <Modal
        title={editingId ? '编辑房型' : '新增房型'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
        destroyOnClose
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{ status: 'enabled', capacity: 2 }}
        >
          <Form.Item
            label="房型名称"
            name="name"
            rules={[{ required: true, message: '请输入房型名称' }]}
          >
            <Input placeholder="例如：星河大床房" />
          </Form.Item>

          <Form.Item
            label="默认价格"
            name="defaultPrice"
            rules={[
              { required: true, message: '请输入默认价格' },
              { type: 'number', min: 0, message: '价格必须大于0' }
            ]}
          >
            <InputNumber
              style={{ width: '100%' }}
              placeholder="399"
              min={0}
              precision={2}
              addonAfter="元"
            />
          </Form.Item>

          <Form.Item
            label="可住人数"
            name="capacity"
            rules={[
              { required: true, message: '请输入可住人数' },
              { type: 'number', min: 1, message: '人数必须大于0' }
            ]}
          >
            <InputNumber
              style={{ width: '100%' }}
              placeholder="2"
              min={1}
              addonAfter="人"
            />
          </Form.Item>

          <Form.Item label="床型" name="bedType">
            <Input placeholder="例如：1.8m 大床" />
          </Form.Item>

          <Form.Item label="早餐说明" name="breakfast">
            <Input placeholder="例如：双早" />
          </Form.Item>

          <Form.Item label="房型描述" name="description">
            <TextArea rows={4} placeholder="请输入房型描述" />
          </Form.Item>

          <Form.Item label="房型图片" name="imageUrl">
            <ImageUpload />
          </Form.Item>

          <Form.Item label="状态" name="status">
            <Input.Group compact>
              <Button
                type={form.getFieldValue('status') === 'enabled' ? 'primary' : 'default'}
                onClick={() => form.setFieldsValue({ status: 'enabled' })}
              >
                启用
              </Button>
              <Button
                type={form.getFieldValue('status') === 'disabled' ? 'primary' : 'default'}
                onClick={() => form.setFieldsValue({ status: 'disabled' })}
              >
                禁用
              </Button>
            </Input.Group>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default RoomTypes;
