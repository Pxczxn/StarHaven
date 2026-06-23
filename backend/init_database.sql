-- 星栖民宿管理系统 - 完整建表语句

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    real_name TEXT,
    phone TEXT,
    role TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'enabled',
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

-- 房型表
CREATE TABLE IF NOT EXISTS room_type (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    default_price NUMERIC NOT NULL DEFAULT 0,
    capacity INTEGER NOT NULL DEFAULT 1,
    bed_type TEXT,
    breakfast TEXT,
    description TEXT,
    image_url TEXT,
    status TEXT NOT NULL DEFAULT 'enabled',
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

-- 房间表
CREATE TABLE IF NOT EXISTS room (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    room_no TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    room_type_id INTEGER NOT NULL,
    floor TEXT,
    area NUMERIC,
    capacity INTEGER NOT NULL DEFAULT 1,
    price NUMERIC NOT NULL DEFAULT 0,
    status TEXT NOT NULL DEFAULT 'available',
    image_url TEXT,
    facilities TEXT,
    remark TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    FOREIGN KEY (room_type_id) REFERENCES room_type(id)
);

-- 客户表
CREATE TABLE IF NOT EXISTS customer (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    phone TEXT NOT NULL UNIQUE,
    id_type TEXT,
    id_number TEXT,
    gender TEXT,
    birthday TEXT,
    source TEXT NOT NULL DEFAULT 'h5',
    level TEXT NOT NULL DEFAULT 'normal',
    remark TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

-- 订单表（包含 payment_status 字段）
CREATE TABLE IF NOT EXISTS booking_order (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_no TEXT NOT NULL UNIQUE,
    customer_id INTEGER,
    customer_name TEXT NOT NULL,
    customer_phone TEXT NOT NULL,
    room_id INTEGER NOT NULL,
    room_no TEXT,
    check_in_date TEXT NOT NULL,
    check_out_date TEXT NOT NULL,
    nights INTEGER NOT NULL,
    total_amount NUMERIC NOT NULL DEFAULT 0,
    paid_amount NUMERIC NOT NULL DEFAULT 0,
    payment_status TEXT NOT NULL DEFAULT 'unpaid',
    payment_method TEXT,
    source TEXT NOT NULL DEFAULT 'h5',
    status TEXT NOT NULL DEFAULT 'pending',
    cancel_reason TEXT,
    remark TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (room_id) REFERENCES room(id)
);

-- 运营任务表
CREATE TABLE IF NOT EXISTS operation_task (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    room_id INTEGER NOT NULL,
    room_no TEXT NOT NULL,
    task_type TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    priority TEXT NOT NULL DEFAULT 'normal',
    status TEXT NOT NULL DEFAULT 'pending',
    assigned_to TEXT,
    due_date TEXT,
    completed_at TEXT,
    remark TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    FOREIGN KEY (room_id) REFERENCES room(id)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_room_type ON room(room_type_id);
CREATE INDEX IF NOT EXISTS idx_room_status ON room(status);
CREATE INDEX IF NOT EXISTS idx_order_customer ON booking_order(customer_id);
CREATE INDEX IF NOT EXISTS idx_order_room ON booking_order(room_id);
CREATE INDEX IF NOT EXISTS idx_order_dates ON booking_order(check_in_date, check_out_date);
CREATE INDEX IF NOT EXISTS idx_order_status ON booking_order(status);
CREATE INDEX IF NOT EXISTS idx_customer_phone ON customer(phone);
CREATE INDEX IF NOT EXISTS idx_operation_task_room ON operation_task(room_id);
CREATE INDEX IF NOT EXISTS idx_operation_task_status ON operation_task(status);

-- 插入初始数据

-- 插入管理员用户 (密码: admin123)
INSERT OR IGNORE INTO user (id, username, password, real_name, phone, role, status, created_at, updated_at)
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', '13800138000', 'admin', 'enabled', datetime('now'), datetime('now'));

-- 插入房型数据
INSERT OR IGNORE INTO room_type (id, name, default_price, capacity, bed_type, breakfast, description, image_url, status, created_at, updated_at) VALUES
(1, '星河大床房', 399, 2, '大床', '含早', '宽敞舒适的星河主题大床房，配备观星天窗', '', 'enabled', datetime('now'), datetime('now')),
(2, '星空双床房', 369, 2, '双床', '不含早', '温馨的星空主题双床房，适合家庭入住', '', 'enabled', datetime('now'), datetime('now')),
(3, '星辰套房', 599, 4, '大床+沙发床', '含早', '豪华星辰主题套房，带客厅和厨房', '', 'enabled', datetime('now'), datetime('now'));

-- 插入房间数据
INSERT OR IGNORE INTO room (id, room_no, name, room_type_id, floor, area, capacity, price, status, image_url, facilities, remark, created_at, updated_at) VALUES
(1, '101', '星河大床房-101', 1, '1F', 32, 2, 399, 'available', '', '独立卫浴,投影,浴缸,观星天窗,智能音响', '', datetime('now'), datetime('now')),
(2, '102', '星河大床房-102', 1, '1F', 32, 2, 399, 'available', '', '独立卫浴,投影,浴缸,观星天窗,智能音响', '', datetime('now'), datetime('now')),
(3, '201', '星空双床房-201', 2, '2F', 28, 2, 369, 'available', '', '独立卫浴,空调,电视,WiFi', '', datetime('now'), datetime('now')),
(4, '202', '星空双床房-202', 2, '2F', 28, 2, 369, 'available', '', '独立卫浴,空调,电视,WiFi', '', datetime('now'), datetime('now')),
(5, '203', '星空双床房-203', 2, '2F', 28, 2, 369, 'available', '', '独立卫浴,空调,电视,WiFi', '', datetime('now'), datetime('now')),
(6, '301', '星辰套房-301', 3, '3F', 65, 4, 599, 'available', '', '独立卫浴,客厅,厨房,投影,浴缸,阳台,智能家居', '', datetime('now'), datetime('now')),
(7, '302', '星辰套房-302', 3, '3F', 65, 4, 599, 'available', '', '独立卫浴,客厅,厨房,投影,浴缸,阳台,智能家居', '', datetime('now'), datetime('now'));

-- 插入测试客户数据
INSERT OR IGNORE INTO customer (id, name, phone, id_type, id_number, gender, birthday, source, level, remark, created_at, updated_at) VALUES
(1, '张三', '13900000001', '身份证', '110101199001011234', '男', '1990-01-01', 'h5', 'normal', '', datetime('now'), datetime('now')),
(2, '李四', '13900000002', '身份证', '110101199202025678', '女', '1992-02-02', 'wechat', 'normal', '', datetime('now'), datetime('now'));
