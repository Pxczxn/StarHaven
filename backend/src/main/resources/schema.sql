-- 星栖民宿管理系统数据库表结构

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
    gender TEXT,
    phone TEXT NOT NULL,
    id_type TEXT,
    id_number TEXT,
    birthday TEXT,
    source TEXT,
    level TEXT,
    remark TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

-- 订单表
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
    room_id INTEGER,
    type TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    status TEXT NOT NULL DEFAULT 'pending',
    assignee TEXT,
    completed_at TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    FOREIGN KEY (room_id) REFERENCES room(id)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_user_username ON user(username);
CREATE INDEX IF NOT EXISTS idx_user_role ON user(role);
CREATE INDEX IF NOT EXISTS idx_room_status ON room(status);
CREATE INDEX IF NOT EXISTS idx_room_type_id ON room(room_type_id);
CREATE INDEX IF NOT EXISTS idx_room_no ON room(room_no);
CREATE INDEX IF NOT EXISTS idx_order_no ON booking_order(order_no);
CREATE INDEX IF NOT EXISTS idx_order_room_date ON booking_order(room_id, check_in_date, check_out_date);
CREATE INDEX IF NOT EXISTS idx_order_customer_phone ON booking_order(customer_phone);
CREATE INDEX IF NOT EXISTS idx_order_status ON booking_order(status);
CREATE INDEX IF NOT EXISTS idx_customer_phone ON customer(phone);
CREATE INDEX IF NOT EXISTS idx_operation_task_room ON operation_task(room_id);
CREATE INDEX IF NOT EXISTS idx_operation_task_status ON operation_task(status);
