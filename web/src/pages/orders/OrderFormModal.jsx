import React, { useState, useEffect } from 'react';
import { Modal, Form, Input, Select, DatePicker, InputNumber, message } from 'antd';
import { createOrder, updateOrder } from '../../api/order';
import { getRooms } from '../../api/room';
import { ORDER_SOURCE, PAYMENT_METHOD } from '../../utils/orderConstants';
import dayjs from 'dayjs';

const { TextArea } = Input;
const { RangePicker } = DatePicker;

const OrderFormModal = ({ visible, order, onClose, onSuccess }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [rooms, setRooms] = useState([]);

  useEffect(() => {
    if (visible) {
      fetchRooms();
      if (order) {
        form.setFieldsValue({
          ...order,
          dateRange: [dayjs(order.checkInDate), dayjs(order.checkOutDate)],
        });
      } else {
        form.resetFields();
      }
    }
  }, [visible, order]);

  const fetchRooms = async () => {
    try {
      const res = await getRooms({ page: 1, pageSize: 100, status: 'available' });
      setRooms(res.data.records || []);
    } catch (error) {
      message.error('获取房间列表失败');
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      const data = {
        customerName: values.customerName,
        customerPhone: values.customerPhone,
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
      open={visible}
      onOk={handleSubmit}
      onCancel={onClose}
      confirmLoading={loading}
      width={600}
      destroyOnClose
    >
      <Form
        form={form}
        layout="vertical"
        initialValues={{
          source: 'front_desk',
          paidAmount: 0,
        }}
      >
        <Form.Item
          label="客户姓名"
          name="customerName"
          rules={[{ required: true, message: '请输入客户姓名' }]}
        >
          <Input placeholder="请输入客户姓名" />
        </Form.Item>

        <Form.Item
          label="客户手机号"
          name="customerPhone"
          rules={[
            { required: true, message: '请输入客户手机号' },
            { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' },
          ]}
        >
          <Input placeholder="请输入客户手机号" />
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

        <Form.Item label="已付金额" name="paidAmount">
          <InputNumber
            style={{ width: '100%' }}
            min={0}
            precision={2}
            placeholder="请输入已付金额"
            addonBefore="¥"
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
