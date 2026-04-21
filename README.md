# 图书管理系统 - 后端

基于 Spring Boot 3.x 的图书管理系统后端项目。

## 技术栈

- Spring Boot 3.1.8
- Spring Security + JWT (JJWT 0.12.6)
- MyBatis Plus 3.5.7
- MySQL 8.0+
- Lombok
- Hutool 5.8.27

## 项目启动

### 前置要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 数据库配置

1. 启动MySQL数据库：

```bash
# 使用Docker启动
docker-compose up -d mysql

# 或使用本地MySQL
```

2. 导入数据库脚本：

```bash
mysql -u root -p123456 < src/main/resources/lms.sql
```

### 启动项目

```bash
# 方式1：使用Maven
mvn spring-boot:run

# 方式2：打包后运行
mvn clean package
java -jar target/lms-backend.jar
```

访问地址：http://localhost:8080/api

## API接口

### 认证接口

- POST /api/auth/login - 用户登录
- POST /api/auth/logout - 用户登出

### 图书接口

- GET /api/books - 获取图书列表
- GET /api/books/{id} - 获取图书详情
- POST /api/books - 添加图书
- PUT /api/books/{id} - 更新图书
- DELETE /api/books/{id} - 删除图书

### 借阅接口

- GET /api/loans - 获取借阅记录
- POST /api/loans - 创建借阅
- PUT /api/loans/{id}/return - 归还图书
- PUT /api/loans/{id}/renew - 续借图书

## 配置说明

### 默认配置

配置文件：`src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lms
    username: root
    password: 123456

jwt:
  secret: lms-secret-key-2024
  expiration: 86400000  # 24小时
```

### 团队协作配置

**🚀 快速配置指南：**

📖 **[连接到 192.168.150.1 数据库 (QUICK_START.md)](./QUICK_START.md)**

**📖 详细配置文档：**

📖 **[数据库配置说明 (DATABASE_SETUP.md)](./DATABASE_SETUP.md)**

**快速配置方式：**

1. **配置环境变量**（推荐）
   - IDEA: Run → Edit Configurations → Environment variables
   ```
   DB_URL=jdbc:mysql://队友IP:3306/lms?...
   DB_USERNAME=用户名
   DB_PASSWORD=密码
   ```

2. **创建本地配置文件**
   ```bash
   cp src/main/resources/application-local.yml.example \
      src/main/resources/application-local.yml
   # 编辑 application-local.yml 配置数据库信息
   ```
   - 激活配置: `--spring.profiles.active=local`

**重要提示：**
- ⚠️ 不要将包含密码的配置文件提交到GitHub
- ✅ `.gitignore` 已配置忽略敏感配置文件
- 🔒 生产环境请使用独立数据库和强密码

## 默认账号

- 管理员：admin / 123456
- 读者：reader / 123456

## 技术架构

- 分层架构：Controller → Service → Mapper
- RESTful API设计
- JWT认证
- 统一异常处理
- 统一响应格式
