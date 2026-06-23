-- 星栖民宿管理系统初始化数据
-- 注意：密码已使用 BCrypt 加密
-- admin 密码：admin123
-- landlord 密码：landlord123

-- 初始化用户账号
INSERT OR IGNORE INTO user (username, password, real_name, phone, role, status, created_at, updated_at) VALUES
('admin', '$2a$10$rQ7MmXkzFqZvHCkWqGqFTeMZVVYYBGGV6YEPXoXz.QZqLwLJI0TJe', '系统管理员', '13800000001', 'admin', 'enabled', datetime('now'), datetime('now')),
('landlord', '$2a$10$8K6PHF.w2mGQVJ5LZNHNVeBqE1Z/1z5oQQKX.XWFY0Y8wKZN5xLOm', '房东', '13800000002', 'landlord', 'enabled', datetime('now'), datetime('now'));

-- 初始化房型数据
INSERT INTO room_type (name, default_price, capacity, bed_type, breakfast, description, image_url, status, created_at, updated_at)
SELECT '星河大床房', 399, 2, '1.8m 大床', '双早', '适合情侣或单人旅行入住，房间设有独立观星天窗，配备投影设备和浴缸。', '', 'enabled', datetime('now'), datetime('now')
WHERE NOT EXISTS (SELECT 1 FROM room_type WHERE name = '星河大床房');

INSERT INTO room_type (name, default_price, capacity, bed_type, breakfast, description, image_url, status, created_at, updated_at)
SELECT '星空双床房', 369, 2, '1.2m 双床', '双早', '适合朋友或家人同住，房间宽敞明亮，配备独立卫浴。', '', 'enabled', datetime('now'), datetime('now')
WHERE NOT EXISTS (SELECT 1 FROM room_type WHERE name = '星空双床房');

INSERT INTO room_type (name, default_price, capacity, bed_type, breakfast, description, image_url, status, created_at, updated_at)
SELECT '星辰套房', 599, 4, '1.8m 大床 + 1.2m 双床', '四早', '豪华套房，适合家庭出游，独立客厅和卧室，配备智能家居系统。', '', 'enabled', datetime('now'), datetime('now')
WHERE NOT EXISTS (SELECT 1 FROM room_type WHERE name = '星辰套房');

-- 初始化房间数据
INSERT OR IGNORE INTO room (room_no, name, room_type_id, floor, area, capacity, price, status, image_url, facilities, remark, created_at, updated_at) VALUES
('101', '星河大床房-101', 1, '1F', 32, 2, 399, 'available', '', '独立卫浴,投影,浴缸,观星天窗,智能音响', '', datetime('now'), datetime('now')),
('102', '星河大床房-102', 1, '1F', 32, 2, 399, 'available', '', '独立卫浴,投影,浴缸,观星天窗,智能音响', '', datetime('now'), datetime('now')),
('201', '星空双床房-201', 2, '2F', 28, 2, 369, 'available', '', '独立卫浴,空调,电视,WiFi', '', datetime('now'), datetime('now')),
('202', '星空双床房-202', 2, '2F', 28, 2, 369, 'available', '', '独立卫浴,空调,电视,WiFi', '', datetime('now'), datetime('now')),
('203', '星空双床房-203', 2, '2F', 28, 2, 369, 'available', '', '独立卫浴,空调,电视,WiFi', '', datetime('now'), datetime('now')),
('301', '星辰套房-301', 3, '3F', 65, 4, 599, 'available', '', '独立卫浴,客厅,厨房,投影,浴缸,阳台,智能家居', '', datetime('now'), datetime('now')),
('302', '星辰套房-302', 3, '3F', 65, 4, 599, 'available', '', '独立卫浴,客厅,厨房,投影,浴缸,阳台,智能家居', '', datetime('now'), datetime('now'));

-- 初始化演示客户数据
INSERT INTO customer (name, gender, phone, id_type, id_number, birthday, source, level, remark, created_at, updated_at)
SELECT '张三', '男', '13900000001', '身份证', '110101199001011234', '1990-01-01', 'h5', 'normal', '', datetime('now'), datetime('now')
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE phone = '13900000001');

INSERT INTO customer (name, gender, phone, id_type, id_number, birthday, source, level, remark, created_at, updated_at)
SELECT '李四', '女', '13900000002', '身份证', '110101199202025678', '1992-02-02', 'wechat', 'normal', '', datetime('now'), datetime('now')
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE phone = '13900000002');
