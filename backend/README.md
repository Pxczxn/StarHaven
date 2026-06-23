# 星栖民宿管理系统 - 后端服务

## 技术栈

- JDK 21
- Spring Boot 3.2.0
- MyBatis-Plus 3.5.5
- SQLite 3.45.0
- JWT 0.12.3

## 项目结构

```
backend/
├── src/main/java/com/xingqi/
│   ├── XingqiApplication.java          # 启动类
│   ├── common/                         # 公共类
│   ├── config/                         # 配置类
│   ├── controller/                     # 控制器
│   ├── service/                        # 业务逻辑
│   ├── mapper/                         # 数据访问
│   ├── entity/                         # 实体类
│   ├── dto/                           # 数据传输对象
│   └── security/                       # 安全认证
├── src/main/resources/
│   ├── application.yml                 # 配置文件
│   ├── schema.sql                      # 数据库表结构
│   └── data.sql                        # 初始化数据
└── pom.xml                            # Maven 配置
```

## 本地开发

### 环境要求

- JDK 21+
- Maven 3.9+

### 启动步骤

1. 确认环境：

```bash
java -version
mvn -version
```

2. 启动项目：

```bash
cd backend
mvn spring-boot:run
```

3. 访问健康检查接口：

```
http://localhost:8888/api/health
```

## 端口配置

- 默认端口：8888
- 数据库文件：项目根目录 `data/xingqi.db`

## 接口文档

### 健康检查

- URL：`GET /api/health`
- 响应示例：

```json
{
  "status": "UP",
  "application": "xingqi-backend",
  "timestamp": "2026-06-23T00:00:00"
}
```

## 注意事项

- 首次启动会自动创建 SQLite 数据库文件，但父目录 `data` 必须存在
- 默认管理员账号：admin / 123456
- 默认房东账号：landlord / 123456
- 开发环境密码仅供测试使用
