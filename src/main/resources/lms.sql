-- 图书管理系统数据库脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS lms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE lms;

-- 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '电话',
    email VARCHAR(100) COMMENT '邮箱',
    role TINYINT NOT NULL COMMENT '角色：0-读者，1-图书管理员，2-管理员',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 读者表
CREATE TABLE reader (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '读者ID',
    user_id BIGINT NOT NULL COMMENT '关联sys_user.id',
    reader_no VARCHAR(20) UNIQUE NOT NULL COMMENT '读者证号',
    id_card VARCHAR(18) COMMENT '身份证号',
    college VARCHAR(100) COMMENT '学院',
    major VARCHAR(100) COMMENT '专业',
    grade VARCHAR(20) COMMENT '年级',
    borrow_limit INT DEFAULT 5 COMMENT '借阅上限',
    current_borrow INT DEFAULT 0 COMMENT '当前借阅数',
    fine_balance DECIMAL(10,2) DEFAULT 0 COMMENT '罚款余额',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='读者表';

-- 图书表
CREATE TABLE book (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '图书ID',
    isbn VARCHAR(20) UNIQUE NOT NULL COMMENT 'ISBN编号',
    title VARCHAR(200) NOT NULL COMMENT '书名',
    author VARCHAR(100) COMMENT '作者',
    publisher VARCHAR(100) COMMENT '出版社',
    publish_date DATE COMMENT '出版日期',
    category VARCHAR(50) COMMENT '分类',
    price DECIMAL(10,2) COMMENT '价格',
    description TEXT COMMENT '内容简介',
    cover_url VARCHAR(255) COMMENT '封面图片URL',
    total_quantity INT DEFAULT 0 COMMENT '总册数',
    available_quantity INT DEFAULT 0 COMMENT '可借册数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- 图书库存表
CREATE TABLE book_stock (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '库存ID',
    book_id BIGINT NOT NULL COMMENT '关联book.id',
    barcode VARCHAR(50) UNIQUE NOT NULL COMMENT '条形码',
    location VARCHAR(50) COMMENT '书架位置',
    status TINYINT DEFAULT 1 COMMENT '状态：0-丢失，1-在馆，2-借出，3-预约中',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (book_id) REFERENCES book(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书库存表';

-- 借阅记录表
CREATE TABLE loan_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '借阅记录ID',
    reader_id BIGINT NOT NULL COMMENT '读者ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    stock_id BIGINT COMMENT '库存ID',
    loan_date DATETIME NOT NULL COMMENT '借阅日期',
    due_date DATETIME NOT NULL COMMENT '应还日期',
    return_date DATETIME COMMENT '实际归还日期',
    renew_count INT DEFAULT 0 COMMENT '续借次数',
    fine DECIMAL(10,2) DEFAULT 0 COMMENT '罚款金额',
    status TINYINT DEFAULT 0 COMMENT '状态：0-借阅中，1-已归还，2-逾期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (reader_id) REFERENCES reader(id),
    FOREIGN KEY (book_id) REFERENCES book(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

-- 预约记录表
CREATE TABLE reservation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
    reader_id BIGINT NOT NULL COMMENT '读者ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    reserve_date DATETIME NOT NULL COMMENT '预约日期',
    expire_date DATETIME NOT NULL COMMENT '过期日期（预约后3天）',
    status TINYINT DEFAULT 0 COMMENT '状态：0-等待中，1-已到书，2-已取消，3-已过期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (reader_id) REFERENCES reader(id),
    FOREIGN KEY (book_id) REFERENCES book(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约记录表';

-- 通知公告表
CREATE TABLE notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    publisher VARCHAR(50) COMMENT '发布人',
    type TINYINT DEFAULT 0 COMMENT '类型：0-系统通知，1-活动公告',
    status TINYINT DEFAULT 1 COMMENT '状态：0-草稿，1-已发布',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

-- 系统配置表
CREATE TABLE system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(50) UNIQUE NOT NULL COMMENT '配置键',
    config_value VARCHAR(500) COMMENT '配置值',
    description VARCHAR(200) COMMENT '描述',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 扫码日志表
CREATE TABLE scan_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '扫码日志ID',
    reader_id BIGINT COMMENT '读者ID',
    book_id BIGINT COMMENT '图书ID',
    scan_type TINYINT NOT NULL COMMENT '扫码类型：0-借阅，1-归还，2-预约',
    barcode VARCHAR(50) COMMENT '条形码',
    operator VARCHAR(50) COMMENT '操作人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='扫码日志表';

-- 插入默认管理员账号（用户名：admin，密码：123456）
INSERT INTO sys_user (username, password, real_name, role) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 2);

-- 插入测试读者账号（用户名：reader，密码：123456）
INSERT INTO sys_user (username, password, real_name, role) VALUES
('reader', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试读者', 0);

-- 插入测试读者信息
INSERT INTO reader (user_id, reader_no, college, major, grade) VALUES
(2, 'R20240001', '计算机学院', '软件工程', '2024级');

-- 插入系统配置
INSERT INTO system_config (config_key, config_value, description) VALUES
('borrow_limit', '5', '借阅上限'),
('borrow_days', '30', '借阅期限（天）'),
('renew_times', '1', '续借次数'),
('renew_days', '15', '续借期限（天）'),
('fine_per_day', '0.1', '逾期罚款（元/天）'),
('reserve_days', '3', '预约保留天数');

-- 插入测试图书数据
INSERT INTO book (isbn, title, author, publisher, publish_date, category, price, description, total_quantity, available_quantity) VALUES
('9787111544937', 'Java核心技术 卷I 基础知识', 'Cay S. Horstmann', '机械工业出版社', '2017-12-01', '计算机', 119.00, 'Java核心技术经典著作', 5, 5),
('9787111452801', '深入理解Java虚拟机', '周志明', '机械工业出版社', '2019-08-01', '计算机', 89.00, 'JVM调优必备', 3, 3),
('9787121315843', 'Vue.js设计与实现', '霍春阳', '电子工业出版社', '2022-06-01', '计算机', 79.00, 'Vue源码解析', 4, 4);

-- 插入测试公告
INSERT INTO notice (title, content, publisher, type, status) VALUES
('欢迎使用图书管理系统', '这是一个功能完善的图书管理系统，支持图书管理、借阅管理、库存管理等功能。', '系统管理员', 0, 1),
('图书馆开放时间通知', '图书馆开放时间：周一至周五 8:00-22:00，周末 9:00-21:00。', '系统管理员', 1, 1);
