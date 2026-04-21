-- =====================================================
-- MySQL 远程访问配置脚本
-- 用于允许队友连接到你本地的数据库
-- =====================================================

-- 注意：执行前请先登录MySQL
-- mysql -u root -p
-- 然后执行此脚本：source mysql-remote-setup.sql

USE mysql;

-- 1. 创建专门用于远程连接的用户（推荐方式）
-- 密码: lms2024
CREATE USER IF NOT EXISTS 'lms_user'@'%' IDENTIFIED BY 'lms2024';

-- 2. 授予 lms_user 用户对 lms 数据库的所有权限
GRANT ALL PRIVILEGES ON lms.* TO 'lms_user'@'%';

-- 3. 允许 root 用户远程连接（可选，不推荐生产环境）
-- 如果需要使用root账户远程连接，取消下面注释
-- UPDATE user SET host='%' WHERE user='root' AND host='localhost';
-- FLUSH PRIVILEGES;

-- 4. 刷新权限
FLUSH PRIVILEGES;

-- 5. 查看用户列表（验证是否创建成功）
SELECT user, host FROM mysql.user;

-- =====================================================
-- 配置完成后的使用说明
-- =====================================================
--
-- 队友连接信息：
-- IP地址: 192.168.150.1
-- 端口: 3306
-- 数据库名: lms
-- 用户名: lms_user
-- 密码: lms2024
--
-- 或使用 root 账户：
-- 用户名: root
-- 密码: 123456
--
-- 连接URL示例：
-- jdbc:mysql://192.168.150.1:3306/lms?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
--
-- =====================================================
-- Windows 防火墙配置（手动操作）
-- =====================================================
--
-- 1. 打开 "Windows Defender 防火墙高级设置"
-- 2. 点击 "入站规则" → "新建规则"
-- 3. 选择 "端口" → "TCP" → "特定本地端口: 3306"
-- 4. 选择 "允许连接"
-- 5. 勾选所有配置文件（域、专用、公用）
-- 6. 命名规则: "MySQL 3306 远程访问"
--
-- =====================================================
