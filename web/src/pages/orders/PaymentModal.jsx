import React, { useEffect } from 'react';
import { Modal, Form, Select, message } from 'antd';
import { PAYMENT_METHOD } from '../../utils/constants';
import { paymentOrder } from '../../api/order';

const PaymentModal = ({ open, order, onCancel, onSuccess }) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (open && order) {
      form.resetFields();
    }
  }, [open, order, form]);

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      const remainingAmount = order.totalAmount - order.paidAmount;

      await paymentOrder(order.id, {
        amount: remainingAmount,  // 直接收取待支付金额
        paymentMethod: values.paymentMethod
      });
      onSuccess();
    } catch (error) {
      if (error.errorFields) {
        return;
      }
      message.error(error.message || '收款失败');
    }
  };

  const remainingAmount = order ? (order.totalAmount - order.paidAmount).toFixed(2) : 0;

  return (
    <Modal
      title="收款"
      open={open}
      onOk={handleOk}
      onCancel={onCancel}
      okText="确认收款"
      cancelText="取消"
      width={600}
    >
      {order && (
        <div style={{
          background: 'rgba(139, 92, 246, 0.1)',
          border: '1px solid rgba(139, 92, 246, 0.3)',
          borderRadius: '12px',
          padding: '20px',
          marginBottom: '24px'
        }}>
          <div style={{ marginBottom: '12px', fontSize: '15px' }}>
            <span style={{ color: 'rgba(255, 255, 255, 0.6)' }}>订单总额：</span>
            <span style={{ fontSize: '18px', fontWeight: 600, color: '#fff' }}>
              ¥{order.totalAmount.toFixed(2)}
            </span>
          </div>
          <div style={{ marginBottom: '12px', fontSize: '15px' }}>
            <span style={{ color: 'rgba(255, 255, 255, 0.6)' }}>已支付：</span>
            <span style={{ color: '#4ade80', fontSize: '16px', fontWeight: 500 }}>
              ¥{order.paidAmount.toFixed(2)}
            </span>
          </div>
          <div style={{ fontSize: '15px' }}>
            <span style={{ color: 'rgba(255, 255, 255, 0.6)' }}>待支付：</span>
            <span style={{ color: '#f87171', fontSize: '18px', fontWeight: 600 }}>
              ¥{remainingAmount}
            </span>
          </div>
        </div>
      )}

      <Form form={form} layout="vertical">
        <Form.Item
          label="支付方式"
          name="paymentMethod"
          rules={[{ required: true, message: '请选择支付方式' }]}
        >
          <Select placeholder="请选择支付方式" size="large">
            {Object.entries(PAYMENT_METHOD).map(([value, label]) => (
              <Select.Option key={value} value={value}>
                {label}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default PaymentModal;
