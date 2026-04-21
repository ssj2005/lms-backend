# 快速配置指南 - 连接到 192.168.150.1 数据库

## 📋 连接信息

- **服务器IP**: `192.168.150.1`
- **端口**: `3306`
- **数据库名**: `lms`
- **用户名**: `lms_user` 或 `root`
- **密码**: `lms2024` 或 `123456`

---

## 🔧 数据库拥有者配置 (192.168.150.1)

### 步骤 1: 配置 MySQL 远程访问权限

**方式一：使用配置脚本（推荐）**

1. 以管理员身份打开命令提示符
2. 登录MySQL：
```bash
mysql -u root -p
# 输入密码: 123456
```

3. 执行配置脚本：
```sql
source D:\grade3term2\LMS\lms-backend\mysql-remote-setup.sql
```

**方式二：手动执行SQL命令**

```sql
-- 登录MySQL后执行：
CREATE USER 'lms_user'@'%' IDENTIFIED BY 'lms2024';
GRANT ALL PRIVILEGES ON lms.* TO 'lms_user'@'%';
FLUSH PRIVILEGES;
```

### 步骤 2: 开放防火墙端口

**自动配置（推荐）：**
1. 右键点击 `open-firewall.bat`
2. 选择 "以管理员身份运行"
3. 按提示操作

**手动配置：**
1. Win + R → 输入 `wf.msc` → 回车
2. 点击 "入站规则" → "新建规则"
3. 规则类型：端口
4. 协议：TCP，端口：3306
5. 操作：允许连接
6. 配置文件：全选
7. 名称：MySQL 3306 远程访问

### 步骤 3: 验证配置

```bash
# 查看3306端口是否监听
netstat -ano | findstr "3306"

# 测试本地连接
mysql -u lms_user -plms2024 -h 192.168.150.1
```

---

## 👥 队友配置 (连接到 192.168.150.1)

### 方式一：使用配置文件（推荐）

1. 配置文件已经创建好：`application-local.yml`
2. 在IDEA中激活 `local` 配置文件：
   - Run → Edit Configurations
   - Active profiles: `local`

### 方式二：测试数据库连接

**使用 MySQL 客户端测试：**
```bash
mysql -u lms_user -plms2024 -h 192.168.150.1
```

**使用telnet测试端口：**
```bash
telnet 192.168.150.1 3306
```

### 方式三：启动应用验证

启动应用后，如果看到以下日志说明连接成功：
```
Initialization Sequence datacenterId:21 workerId:21
 _ _   |_  _ _|_. ___ _ |    _
| | |\/|_)(_| | |_\  |_)||_|_\
     /               |
                        3.5.7
```

---

## ❓ 常见问题

### Q1: 无法连接到数据库
**检查清单：**
- [ ] MySQL服务是否启动
- [ ] 防火墙是否开放3306端口
- [ ] 用户权限是否正确配置
- [ ] 网络是否通畅：`ping 192.168.150.1`

### Q2: Public Key Retrieval is not allowed
连接URL已添加 `allowPublicKeyRetrieval=true`，如果还报错，请检查：
- 用户密码是否正确
- MySQL版本是否是8.0+

### Q3: 连接超时
1. 检查是否在同一局域网
2. 确认IP地址正确：`192.168.150.1`
3. 关闭VPN尝试

### Q4: Access denied for user
```sql
-- 重新设置密码
ALTER USER 'lms_user'@'%' IDENTIFIED BY 'lms2024';
FLUSH PRIVILEGES;
```

---

## 🔒 安全提示

⚠️ **重要安全建议：**

1. **开发完成后关闭远程访问**
   ```sql
   -- 删除远程用户
   DROP USER 'lms_user'@'%';
   ```

2. **不要在公共WiFi下使用**

3. **定期更换数据库密码**

4. **生产环境务必：**
   - 使用强密码
   - 限制允许访问的IP段
   - 启用SSL连接
   - 不要使用root账户

---

## 📞 联系信息

如有问题，请联系数据库管理员：
- IP: 192.168.150.1
- 微信/钉钉: [你的联系方式]
