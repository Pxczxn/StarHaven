-- 星栖民宿管理系统初始化数据 (MySQL版本)

-- 插入管理员用户 (密码: admin123)
INSERT INTO `user` (`id`, `username`, `password`, `real_name`, `phone`, `role`, `status`, `created_at`, `updated_at`)
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', '13800138000', 'admin', 'enabled', NOW(), NOW())
ON DUPLICATE KEY UPDATE `username` = `username`;

-- 插入房型数据
INSERT INTO `room_type` (`id`, `name`, `default_price`, `capacity`, `bed_type`, `breakfast`, `description`, `image_url`, `status`, `created_at`, `updated_at`) VALUES
(1, '星河大床房', 399.00, 2, '大床', '含早', '宽敞舒适的星河主题大床房，配备观星天窗', '/uploads/rooms/room-default.png', 'enabled', NOW(), NOW()),
(2, '星空双床房', 369.00, 2, '双床', '不含早', '温馨的星空主题双床房，适合家庭入住', '/uploads/rooms/room-default.png', 'enabled', NOW(), NOW()),
(3, '星辰套房', 599.00, 4, '大床+沙发床', '含早', '豪华星辰主题套房，带客厅和厨房', '/uploads/rooms/room-default.png', 'enabled', NOW(), NOW())
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `image_url` = VALUES(`image_url`);

-- 插入房间数据
INSERT INTO `room` (`id`, `room_no`, `name`, `room_type_id`, `floor`, `area`, `capacity`, `price`, `status`, `image_url`, `facilities`, `remark`, `created_at`, `updated_at`) VALUES
(1, '101', '星河大床房-101', 1, '1F', 32.00, 2, 399.00, 'available', '/uploads/rooms/room-101.png', '独立卫浴,投影,浴缸,观星天窗,智能音响', '', NOW(), NOW()),
(2, '102', '星河大床房-102', 1, '1F', 32.00, 2, 399.00, 'available', '/uploads/rooms/room-102.png', '独立卫浴,投影,浴缸,观星天窗,智能音响', '', NOW(), NOW()),
(3, '201', '星空双床房-201', 2, '2F', 28.00, 2, 369.00, 'available', '/uploads/rooms/room-201.png', '独立卫浴,空调,电视,WiFi', '', NOW(), NOW()),
(4, '202', '星空双床房-202', 2, '2F', 28.00, 2, 369.00, 'available', '/uploads/rooms/room-202.png', '独立卫浴,空调,电视,WiFi', '', NOW(), NOW()),
(5, '203', '星空双床房-203', 2, '2F', 28.00, 2, 369.00, 'available', '/uploads/rooms/room-203.png', '独立卫浴,空调,电视,WiFi', '', NOW(), NOW()),
(6, '301', '星辰套房-301', 3, '3F', 65.00, 4, 599.00, 'available', '/uploads/rooms/room-301.png', '独立卫浴,客厅,厨房,投影,浴缸,阳台,智能家居', '', NOW(), NOW()),
(7, '302', '星辰套房-302', 3, '3F', 65.00, 4, 599.00, 'available', '/uploads/rooms/room-302.png', '独立卫浴,客厅,厨房,投影,浴缸,阳台,智能家居', '', NOW(), NOW())
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `image_url` = VALUES(`image_url`);

-- 插入测试客户数据
INSERT INTO `customer` (`id`, `name`, `phone`, `id_type`, `id_number`, `gender`, `birthday`, `source`, `level`, `remark`, `created_at`, `updated_at`) VALUES
(1, '张三', '13900000001', '身份证', '110101199001011234', '男', '1990-01-01', 'h5', 'normal', '', NOW(), NOW()),
(2, '李四', '13900000002', '身份证', '110101199202025678', '女', '1992-02-02', 'wechat', 'normal', '', NOW(), NOW())
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);
