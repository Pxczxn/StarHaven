import React, { useState, useEffect } from 'react';
import { Modal, Form, Input, Select, DatePicker, InputNumber, message, AutoComplete } from 'antd';
import { UserOutlined, PhoneOutlined } from '@ant-design/icons';
import { createOrder, updateOrder } from '../../api/order';
import { getRooms } from '../../api/room';
import { getCustomers } from '../../api/customer';
import { ORDER_SOURCE, PAYMENT_METHOD } from '../../utils/constants';
import dayjs from 'dayjs';

const { TextArea } = Input;
const { RangePicker } = DatePicker;

const OrderFormModal = ({ open, order, onClose, onSuccess }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [rooms, setRooms] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [customerOptions, setCustomerOptions] = useState([]);

  useEffect(() => {
    if (open) {
      fetchRooms();
      fetchCustomers();
      if (order) {
        form.setFieldsValue({
          ...order,
          dateRange: [dayjs(order.checkInDate), dayjs(order.checkOutDate)],
        });
      } else {
        form.resetFields();
      }
    }
  }, [open, order]);

  const fetchRooms = async () => {
    try {
      const res = await getRooms({ page: 1, pageSize: 100, status: 'available' });
      setRooms(res.data.records || []);
    } catch (error) {
      message.error('获取房间列表失败');
    }
  };

  const fetchCustomers = async () => {
    try {
      const res = await getCustomers({ page: 1, pageSize: 100 });
      const customerList = res.data.records || [];
      setCustomers(customerList);
      updateCustomerOptions(customerList, '');
    } catch (error) {
      message.error('获取客户列表失败');
    }
  };

  const updateCustomerOptions = (customerList, searchValue) => {
    // 如果没有输入搜索内容，显示最近3个客户
    if (!searchValue || searchValue.trim() === '') {
      const recentCustomers = customerList.slice(0, 3);
      const options = recentCustomers.map(customer => ({
        value: customer.name,
        label: (
          <div style={{ padding: '8px 0' }}>
            <div style={{ display: 'flex', alignItems: 'center', marginBottom: '4px' }}>
              <UserOutlined style={{ marginRight: '8px', color: '#1890ff' }} />
              <span style={{ fontSize: '14px', fontWeight: 500 }}>{customer.name}</span>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', paddingLeft: '24px' }}>
              <PhoneOutlined style={{ marginRight: '8px', color: '#8c8c8c', fontSize: '12px' }} />
              <span style={{ fontSize: '12px', color: '#8c8c8c' }}>{customer.phone}</span>
            </div>
          </div>
        ),
        customer: customer
      }));
      setCustomerOptions(options);
      return;
    }

    // 根据输入内容过滤客户
    const filtered = customerList.filter(customer =>
      customer.name.toLowerCase().includes(searchValue.toLowerCase()) ||
      customer.phone.includes(searchValue)
    );

    // 如果找到匹配的客户，显示前3个
    if (filtered.length > 0) {
      const options = filtered.slice(0, 3).map(customer => ({
        value: customer.name,
        label: (
          <div style={{ padding: '8px 0' }}>
            <div style={{ display: 'flex', alignItems: 'center', marginBottom: '4px' }}>
              <UserOutlined style={{ marginRight: '8px', color: '#1890ff' }} />
              <span style={{ fontSize: '14px', fontWeight: 500 }}>{customer.name}</span>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', paddingLeft: '24px' }}>
              <PhoneOutlined style={{ marginRight: '8px', color: '#8c8c8c', fontSize: '12px' }} />
              <span style={{ fontSize: '12px', color: '#8c8c8c' }}>{customer.phone}</span>
            </div>
          </div>
        ),
        customer: customer
      }));
      setCustomerOptions(options);
    } else {
      // 没有匹配的客户，清空下拉选项（表示新客户）
      setCustomerOptions([]);
    }
  };

  const handleCustomerSearch = (searchValue) => {
    updateCustomerOptions(customers, searchValue);
  };

  const handleCustomerSelect = (value, option) => {
    if (option && option.customer) {
      form.setFieldsValue({
        customerName: option.customer.name,
        customerPhone: option.customer.phone,
        customerIdNumber: option.customer.idNumber,
      });
      message.success('已选择客户：' + option.customer.name);
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      const data = {
        customerName: values.customerName,
        customerPhone: values.customerPhone,
        customerIdNumber: values.customerIdNumber,
        roomId: values.roomId,
        checkInDate: values.dateRange[0].format('YYYY-MM-DD'),
        checkOutDate: values.dateRange[1].format('YYYY-MM-DD'),
        paidAmount: values.paidAmount || 0,
        paymentMethod: values.paymentMethod,
        source: values.source || 'front_desk',
        remark: values.remark,
      };

      setLoading(true);

      if (order) {
        await updateOrder(order.id, data);
        message.success('订单更新成功');
      } else {
        await createOrder(data);
        message.success('订单创建成功');
      }

      onSuccess();
    } catch (error) {
      if (error.errorFields) {
        return;
      }
      message.error(error.message || '操作失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title={order ? '编辑订单' : '新增订单'}
      open={open}
      onOk={handleSubmit}
      onCancel={onClose}
      confirmLoading={loading}
      width={900}
      destroyOnHidden
    >
      <Form
        form={form}
        layout="vertical"
        initialValues={{
          source: 'front_desk',
          paidAmount: 0,
        }}
      >
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
          <Form.Item
            label="客户姓名"
            name="customerName"
            rules={[{ required: true, message: '请输入客户姓名' }]}
          >
            <AutoComplete
              options={customerOptions}
              onSelect={handleCustomerSelect}
              onSearch={handleCustomerSearch}
              placeholder="请输入客户姓名"
              disabled={!!order}
              allowClear
              styles={{
                popup: {
                  root: {
                    background: 'rgba(20, 20, 40, 0.95)',
                    border: '1px solid rgba(255, 255, 255, 0.1)',
                    borderRadius: '8px',
                    padding: '8px',
                  },
                },
              }}
            />
          </Form.Item>

          <Form.Item
            label="客户手机号"
            name="customerPhone"
            rules={[
              { required: true, message: '请输入客户手机号' },
              {
                pattern: /^1[3-9]\d{9}$/,
                message: '手机号格式不正确'
              }
            ]}
          >
            <Input
              placeholder="请输入客户手机号"
              maxLength={11}
            />
          </Form.Item>

          <Form.Item
            label="身份证号"
            name="customerIdNumber"
            rules={[
              { required: true, message: '请输入身份证号' },
              {
                pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
                message: '身份证号格式不正确'
              }
            ]}
          >
            <Input
              placeholder="请输入身份证号"
              maxLength={18}
            />
          </Form.Item>

          <Form.Item
            label="选择房间"
            name="roomId"
            rules={[{ required: true, message: '请选择房间' }]}
          >
            <Select placeholder="请选择房间" showSearch optionFilterProp="children">
              {rooms.map((room) => (
                <Select.Option key={room.id} value={room.id}>
                  {room.roomNo} - {room.name} (¥{room.price}/晚)
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
        </div>

        <Form.Item
          label="入住日期"
          name="dateRange"
          rules={[{ required: true, message: '请选择入住日期' }]}
        >
          <RangePicker
            style={{ width: '100%' }}
            placeholder={['入住日期', '离店日期']}
            format="YYYY-MM-DD"
          />
        </Form.Item>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
          <Form.Item label="已付金额" name="paidAmount">
            <InputNumber
              style={{ width: '100%' }}
              min={0}
              precision={2}
              placeholder="请输入已付金额"
              prefix="¥"
            />
          </Form.Item>

          <Form.Item label="支付方式" name="paymentMethod">
            <Select placeholder="请选择支付方式" allowClear>
              {Object.keys(PAYMENT_METHOD).map((key) => (
                <Select.Option key={key} value={key}>
                  {PAYMENT_METHOD[key]}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
        </div>

        <Form.Item label="订单来源" name="source">
          <Select placeholder="请选择订单来源">
            {Object.keys(ORDER_SOURCE).map((key) => (
              <Select.Option key={key} value={key}>
                {ORDER_SOURCE[key]}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item label="备注" name="remark">
          <TextArea rows={3} placeholder="请输入备注信息" />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default OrderFormModal;
