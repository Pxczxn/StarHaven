# StarHaven 星栖后端

JDK 21 + Spring Boot 3.3 + MyBatis-Plus + Sa-Token + Redis + Knife4j。

## 启动

1. 创建库并执行 `sql/V1.0.0__init_schema.sql`
2. 修改 `starhaven-api/src/main/resources/application.yml` 中的 MySQL / Redis
3. 启动 Redis、MySQL
4. 运行 **唯一入口** `starhaven-api`。同一进程提供用户端 `/api/v1`、管理端 `/admin` 和定时任务。

| 用途 | 端口 | 说明 |
|------|------|------|
| 小程序 / H5 开发页 | 5566 | uni-app `pnpm dev:h5`（`VITE_APP_PORT`） |
| 服务端 API | 5567 | 小程序、H5 代理都请求这里 |
| 管理端（Knife4j） | 5568 | 同一 JVM 再绑一个端口，不是第二个服务 |

```bash
mvn -pl starhaven-api -am spring-boot:run
```

用户端文档：http://127.0.0.1:5567/doc.html  
管理端文档：http://127.0.0.1:5568/doc.html

## 演示账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 房东 | host | 123456 |
| 用户 | staruser | 123456 |

短信验证码演示：任意已注册手机号 + `123456`。

## 小程序联调

用户端前缀 `/api/v1`，基址 `http://127.0.0.1:5567`。Token 放在 `Authorization: Bearer <token>` 或 `satoken`。微信开发者工具不要用 `localhost`。
