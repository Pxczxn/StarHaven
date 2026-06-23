import React, { useState } from 'react';
import { Modal, Form, Input } from 'antd';

const CancelOrderModal = ({ open, order, onClose, onSuccess }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);
      await onSuccess(values);
      form.resetFields();
    } catch (error) {
      console.error('取消订单失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    form.resetFields();
    onClose();
  };

  return (
    <Modal
      title="取消订单"
      open={open}
      onOk={handleOk}
      onCancel={handleCancel}
      confirmLoading={loading}
      okText="确认取消"
      cancelText="返回"
      destroyOnHidden
    >
      <Form form={form} layout="vertical">
        <Form.Item
          label="取消原因"
          name="reason"
          rules={[{ required: true, message: '请输入取消原因' }]}
        >
          <Input.TextArea rows={4} placeholder="请说明取消订单的原因" />
        </Form.Item>
      </Form>
      <p style={{ color: '#999', fontSize: '12px' }}>
        注意：取消订单后，如果客人已入住，房间状态将变为清洁中。
      </p>
    </Modal>
  );
};

export default CancelOrderModal;
