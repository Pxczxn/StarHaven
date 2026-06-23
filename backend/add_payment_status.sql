-- 添加支付状态字段
ALTER TABLE booking_order ADD COLUMN payment_status VARCHAR(20) DEFAULT 'unpaid';

-- 更新现有数据
UPDATE booking_order SET payment_status = 'unpaid' WHERE payment_status IS NULL;
