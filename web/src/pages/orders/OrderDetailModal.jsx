import React from 'react';
import { Modal, Descriptions, Tag } from 'antd';
import { formatDateTime, formatMoney } from '../../utils/format';

const OrderDetailModal = ({ open, order, onClose }) => {
  if (!order) return null;

  const statusMap = {
    pending: { text: '待确认', color: 'orange' },
    reserved: { text: '已预订', color: 'blue' },
    occupied: { text: '已入住', color: 'green' },
    checked_out: { text: '已退房', color: 'default' },
    cancelled: { text: '已取消', color: 'red' }
  };

  const paymentStatusMap = {
    unpaid: { text: '未支付', color: 'red' },
    partial: { text: '部分支付', color: 'orange' },
    paid: { text: '已支付', color: 'green' },
    refunded: { text: '已退款', color: 'default' }
  };

  return (
    <Modal
      title="订单详情"
      open={open}
      onCancel={onClose}
      footer={null}
      width={800}
    >
      <Descriptions bordered column={2}>
        <Descriptions.Item label="订单号" span={2}>{order.orderNo}</Descriptions.Item>
        <Descriptions.Item label="客户姓名">{order.customerName}</Descriptions.Item>
        <Descriptions.Item label="客户手机">{order.customerPhone}</Descriptions.Item>
        <Descriptions.Item label="房间号">{order.roomNo}</Descriptions.Item>
        <Descriptions.Item label="入住天数">{order.nights} 晚</Descriptions.Item>
        <Descriptions.Item label="入住日期">{order.checkInDate}</Descriptions.Item>
        <Descriptions.Item label="退房日期">{order.checkOutDate}</Descriptions.Item>
        <Descriptions.Item label="订单状态">
          <Tag color={statusMap[order.status]?.color}>
            {statusMap[order.status]?.text || order.status}
          </Tag>
        </Descriptions.Item>
        <Descriptions.Item label="支付状态">
          <Tag color={paymentStatusMap[order.paymentStatus]?.color}>
            {paymentStatusMap[order.paymentStatus]?.text || order.paymentStatus}
          </Tag>
        </Descriptions.Item>
        <Descriptions.Item label="订单金额">{formatMoney(order.totalAmount)}</Descriptions.Item>
        <Descriptions.Item label="已支付">{formatMoney(order.paidAmount)}</Descriptions.Item>
        <Descriptions.Item label="创建时间" span={2}>{formatDateTime(order.createdAt)}</Descriptions.Item>
        {order.remark && (
          <Descriptions.Item label="备注" span={2}>{order.remark}</Descriptions.Item>
        )}
      </Descriptions>
    </Modal>
  );
};

export default OrderDetailModal;
