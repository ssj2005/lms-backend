@echo off
echo =====================================================
echo 测试连接到数据库 (192.168.150.1:3306)
echo =====================================================
echo.

echo [1/4] 测试网络连通性...
ping -n 2 192.168.150.1
if %errorlevel% neq 0 (
    echo.
    echo ✗ 无法连接到 192.168.150.1
    echo 请检查：
    echo 1. 是否在同一局域网
    echo 2. IP地址是否正确
    echo 3. 网络连接是否正常
    echo.
    pause
    exit /b 1
)
echo ✓ 网络连通正常
echo.

echo [2/4] 测试MySQL端口 (3306)...
powershell -command "Test-NetConnection -ComputerName 192.168.150.1 -Port 3306 | Select-Object -Property ComputerName, RemoteAddress, TcpTestSucceeded"
echo.

echo [3/4] 测试MySQL连接...
echo 请输入MySQL用户名 (默认: lms_user):
set /p MYSQL_USER=
if "%MYSQL_USER%"=="" set MYSQL_USER=lms_user

echo 请输入MySQL密码 (默认: lms2024):
set /p MYSQL_PWD=
if "%MYSQL_PWD%"=="" set MYSQL_PWD=lms2024

mysql -u%MYSQL_USER% -p%MYSQL_PWD% -h 192.168.150.1 -e "SELECT VERSION(); SHOW DATABASES;" 2>nul
if %errorlevel% equ 0 (
    echo.
    echo =====================================================
    echo ✓ 数据库连接成功！
    echo =====================================================
    echo.
    echo 连接信息：
    echo IP: 192.168.150.1
    echo 端口: 3306
    echo 用户名: %MYSQL_USER%
    echo.
    echo 现在可以启动应用了！
    echo.
) else (
    echo.
    echo =====================================================
    echo ✗ 数据库连接失败！
    echo =====================================================
    echo.
    echo 可能的原因：
    echo 1. 用户名或密码错误
    echo 2. MySQL未配置远程访问权限
    echo 3. 防火墙未开放3306端口
    echo 4. MySQL服务未启动
    echo.
    echo 请联系数据库管理员检查配置
    echo.
)

pause
