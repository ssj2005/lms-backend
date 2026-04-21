@echo off
echo =====================================================
echo MySQL 远程访问配置 - 防火墙设置
echo =====================================================
echo.
echo 此脚本将开放 MySQL 3306 端口的防火墙访问权限
echo 允许队友连接到你本地的数据库
echo.
pause

echo.
echo 正在添加防火墙入站规则...
netsh advfirewall firewall add rule name="MySQL 3306 远程访问" dir=in action=allow protocol=TCP localport=3306

if %errorlevel% equ 0 (
    echo.
    echo =====================================================
    echo ✓ 防火墙规则添加成功！
    echo =====================================================
    echo.
    echo 下一步操作：
    echo 1. 执行 MySQL 配置脚本: mysql-remote-setup.sql
    echo    登录MySQL: mysql -u root -p
    echo    执行脚本: source D:\grade3term2\LMS\lms-backend\mysql-remote-setup.sql
    echo.
    echo 2. 或者手动在MySQL中执行：
    echo    CREATE USER 'lms_user'@'%' IDENTIFIED BY 'lms2024';
    echo    GRANT ALL PRIVILEGES ON lms.* TO 'lms_user'@'%';
    echo    FLUSH PRIVILEGES;
    echo.
    echo 3. 告诉队友以下连接信息：
    echo    IP地址: 192.168.150.1
    echo    端口: 3306
    echo    用户名: lms_user
    echo    密码: lms2024
    echo.
) else (
    echo.
    echo =====================================================
    echo ✗ 防火墙规则添加失败！
    echo =====================================================
    echo.
    echo 请尝试以下手动操作：
    echo 1. 以管理员身份运行此脚本
    echo 2. 或手动添加防火墙规则：
    echo    - 打开 "Windows Defender 防火墙高级设置"
    echo    - 点击 "入站规则" → "新建规则"
    echo    - 选择 "端口" → "TCP" → "特定本地端口: 3306"
    echo    - 选择 "允许连接"
    echo.
)

pause
