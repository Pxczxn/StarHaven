-- 星栖民宿管理系统数据库表结构 (MySQL版本)

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `phone` VARCHAR(20) COMMENT '手机号',
    `role` VARCHAR(20) NOT NULL COMMENT '角色',
    `status` VARCHAR(20) NOT NULL DEFAULT 'enabled' COMMENT '状态',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 房型表
CREATE TABLE IF NOT EXISTS `room_type` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL COMMENT '房型名称',
    `default_price` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '默认价格',
    `capacity` INT NOT NULL DEFAULT 1 COMMENT '容纳人数',
    `bed_type` VARCHAR(50) COMMENT '床型',
    `breakfast` VARCHAR(20) COMMENT '早餐',
    `description` TEXT COMMENT '描述',
    `image_url` VARCHAR(255) COMMENT '图片地址',
    `status` VARCHAR(20) NOT NULL DEFAULT 'enabled' COMMENT '状态',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房型表';

-- 房间表
CREATE TABLE IF NOT EXISTS `room` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `room_no` VARCHAR(20) NOT NULL UNIQUE COMMENT '房间号',
    `name` VARCHAR(100) NOT NULL COMMENT '房间名称',
    `room_type_id` BIGINT NOT NULL COMMENT '房型ID',
    `floor` VARCHAR(10) COMMENT '楼层',
    `area` DECIMAL(6,2) COMMENT '面积',
    `capacity` INT NOT NULL DEFAULT 1 COMMENT '容纳人数',
    `price` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '价格',
    `status` VARCHAR(20) NOT NULL DEFAULT 'available' COMMENT '状态',
    `image_url` VARCHAR(255) COMMENT '图片地址',
    `facilities` TEXT COMMENT '设施',
    `remark` TEXT COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`room_type_id`) REFERENCES `room_type`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间表';

-- 客户表
CREATE TABLE IF NOT EXISTS `customer` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL COMMENT '客户姓名',
    `phone` VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    `id_type` VARCHAR(20) COMMENT '证件类型',
    `id_number` VARCHAR(50) COMMENT '证件号码',
    `gender` VARCHAR(10) COMMENT '性别',
    `birthday` DATE COMMENT '生日',
    `source` VARCHAR(20) NOT NULL DEFAULT 'h5' COMMENT '来源',
    `level` VARCHAR(20) NOT NULL DEFAULT 'normal' COMMENT '会员等级',
    `remark` TEXT COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- 订单表
CREATE TABLE IF NOT EXISTS `booking_order` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    `customer_id` BIGINT COMMENT '客户ID',
    `customer_name` VARCHAR(50) NOT NULL COMMENT '客户姓名',
    `customer_phone` VARCHAR(20) NOT NULL COMMENT '客户手机号',
    `room_id` BIGINT NOT NULL COMMENT '房间ID',
    `room_no` VARCHAR(20) COMMENT '房间号',
    `check_in_date` DATE NOT NULL COMMENT '入住日期',
    `check_out_date` DATE NOT NULL COMMENT '退房日期',
    `nights` INT NOT NULL COMMENT '住宿天数',
    `total_amount` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '总金额',
    `paid_amount` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '已支付金额',
    `payment_status` VARCHAR(20) NOT NULL DEFAULT 'unpaid' COMMENT '支付状态',
    `payment_method` VARCHAR(20) COMMENT '支付方式',
    `source` VARCHAR(20) NOT NULL DEFAULT 'h5' COMMENT '订单来源',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '订单状态',
    `cancel_reason` TEXT COMMENT '取消原因',
    `remark` TEXT COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`customer_id`) REFERENCES `customer`(`id`),
    FOREIGN KEY (`room_id`) REFERENCES `room`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 运营任务表
CREATE TABLE IF NOT EXISTS `operation_task` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `room_id` BIGINT NOT NULL COMMENT '房间ID',
    `room_no` VARCHAR(20) NOT NULL COMMENT '房间号',
    `task_type` VARCHAR(20) NOT NULL COMMENT '任务类型',
    `title` VARCHAR(100) NOT NULL COMMENT '任务标题',
    `description` TEXT COMMENT '任务描述',
    `priority` VARCHAR(20) NOT NULL DEFAULT 'normal' COMMENT '优先级',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态',
    `assigned_to` VARCHAR(50) COMMENT '负责人',
    `due_date` DATETIME COMMENT '截止时间',
    `completed_at` DATETIME COMMENT '完成时间',
    `remark` TEXT COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`room_id`) REFERENCES `room`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运营任务表';

-- 创建索引（先删除再创建，避免重复）
DROP INDEX IF EXISTS idx_room_type ON `room`;
CREATE INDEX idx_room_type ON `room`(`room_type_id`);

DROP INDEX IF EXISTS idx_room_status ON `room`;
CREATE INDEX idx_room_status ON `room`(`status`);

DROP INDEX IF EXISTS idx_order_customer ON `booking_order`;
CREATE INDEX idx_order_customer ON `booking_order`(`customer_id`);

DROP INDEX IF EXISTS idx_order_room ON `booking_order`;
CREATE INDEX idx_order_room ON `booking_order`(`room_id`);

DROP INDEX IF EXISTS idx_order_dates ON `booking_order`;
CREATE INDEX idx_order_dates ON `booking_order`(`check_in_date`, `check_out_date`);

DROP INDEX IF EXISTS idx_order_status ON `booking_order`;
CREATE INDEX idx_order_status ON `booking_order`(`status`);

DROP INDEX IF EXISTS idx_customer_phone ON `customer`;
CREATE INDEX idx_customer_phone ON `customer`(`phone`);

DROP INDEX IF EXISTS idx_operation_task_room ON `operation_task`;
CREATE INDEX idx_operation_task_room ON `operation_task`(`room_id`);

DROP INDEX IF EXISTS idx_operation_task_status ON `operation_task`;
CREATE INDEX idx_operation_task_status ON `operation_task`(`status`);
