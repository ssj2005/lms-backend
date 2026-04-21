# 数据库配置说明

## 本地开发配置

### 方式一：连接本地数据库

默认配置会连接你本地的MySQL数据库，无需修改配置文件。

**前提条件：**
1. 安装 MySQL 5.7+ 或 8.0+
2. 创建数据库：`lms`
3. 默认用户名：`root`
4. 默认密码：`123456`

### 方式二：连接队友的数据库

#### 📍 数据库拥有者配置（让其他人能连接）

**1. 获取本机内网IP地址**
```bash
# Windows
ipconfig

# 查找 "IPv4 地址"，例如：192.168.1.100
```

**2. 配置MySQL允许远程连接**

```sql
-- 登录MySQL
mysql -u root -p

-- 创建允许远程连接的用户（推荐方式）
CREATE USER 'lms_user'@'%' IDENTIFIED BY '你的密码';
GRANT ALL PRIVILEGES ON lms.* TO 'lms_user'@'%';
FLUSH PRIVILEGES;

-- 或者允许现有用户远程连接（不推荐生产环境）
UPDATE mysql.user SET host='%' WHERE user='root' AND host='localhost';
FLUSH PRIVILEGES;
```

**3. 配置MySQL配置文件（my.ini 或 my.cnf）**
```ini
[mysqld]
# 绑定所有IP地址
bind-address = 0.0.0.0

# 或者指定具体网段
# bind-address = 0.0.0.0
```

**4. 重启MySQL服务**
```bash
# Windows
net stop MySQL
net start MySQL

# 或者在服务管理器中重启MySQL服务
```

**5. 检查防火墙**
```bash
# Windows防火墙 - 添加入站规则
# 1. 打开 "Windows Defender 防火墙高级设置"
# 2. 新建入站规则 -> 端口 -> TCP -> 特定本地端口：3306
# 3. 允许连接
# 4. 勾选所有配置文件
# 5. 命名规则："MySQL 3306"
```

**6. 将连接信息告知队友**
- IP地址：例如 `192.168.1.100`
- 端口：`3306`
- 数据库名：`lms`
- 用户名：`lms_user` 或 `root`
- 密码：你设置的密码

#### 📍 客户端配置（连接队友数据库）

**方式一：使用环境变量（推荐）**

在IDE中配置环境变量：

**IntelliJ IDEA：**
1. Run → Edit Configurations
2. 选择你的Spring Boot应用
3. Environment variables 中添加：
```
DB_URL=jdbc:mysql://192.168.1.100:3306/lms?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
DB_USERNAME=lms_user
DB_PASSWORD=队友提供的密码
```

**方式二：创建本地配置文件**

1. 复制 `application.yml` 并重命名为 `application-local.yml`
2. 修改数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://192.168.1.100:3306/lms?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: lms_user
    password: 队友提供的密码
```
3. 激活配置文件：
   - IDEA: Run → Edit Configurations → Active profiles: `local`
   - 命令行: `java -jar app.jar --spring.profiles.active=local`

**方式三：直接修改application.yml（不推荐，会被git追踪）**

```yaml
spring:
  datasource:
    url: jdbc:mysql://192.168.1.100:3306/lms?...
    username: lms_user
    password: 密码
```

## 🚫 重要提示

### 安全建议
1. **不要在GitHub上提交包含密码的配置文件**
2. **不要在生产环境使用root账户远程连接**
3. **团队成员开发完成后，应各自使用本地数据库**
4. **考虑使用VPN或内网穿透工具（如ngrok）进行远程开发**

### Git忽略配置
确保 `.gitignore` 包含：
```
# 本地配置文件
application-local.yml
application-dev.yml

# IDEA配置
.idea/
*.iml
```

## 测试连接

启动应用后，如果看到以下日志说明连接成功：
```
Initialization Sequence datacenterId:21 workerId:21
 _ _   |_  _ _|_. ___ _ |    _ 
| | |\/|_)(_| | |_\  |_)||_|_\ 
     /               |         
                        3.5.7
```

## 常见问题

### Q: 无法连接到数据库
A: 检查：
1. 数据库服务是否启动
2. 防火墙是否开放3306端口
3. MySQL是否允许远程连接
4. 用户名和密码是否正确
5. 网络是否连通（ping数据库IP）

### Q: 连接被拒绝
A:
1. 确认MySQL用户的host设置是否正确（`SELECT user, host FROM mysql.user;`）
2. 检查MySQL bind-address配置
3. 查看MySQL错误日志

### Q: Public Key Retrieval is not allowed
A: 在连接URL中添加 `allowPublicKeyRetrieval=true`：
```yaml
url: jdbc:mysql://192.168.1.100:3306/lms?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
```
