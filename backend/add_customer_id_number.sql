-- 添加客户身份证号字段到订单表
ALTER TABLE booking_order ADD COLUMN customer_id_number VARCHAR(50) COMMENT '客户身份证号' AFTER customer_phone;
