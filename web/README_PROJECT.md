# 星栖民宿管理系统 - 后台管理端

## 技术栈

- React 18
- Vite 6
- React Router
- Axios
- Ant Design (待安装)
- ECharts (待安装)

## 项目结构

```
web/
├── src/
│   ├── api/                    # 接口请求封装
│   ├── assets/                 # 静态资源
│   ├── components/             # 公共组件
│   ├── layouts/                # 布局组件
│   ├── pages/                  # 页面组件
│   ├── router/                 # 路由配置
│   ├── store/                  # 状态管理
│   ├── styles/                 # 全局样式
│   ├── utils/                  # 工具函数
│   ├── App.jsx                 # 根组件
│   └── main.jsx                # 入口文件
├── public/                     # 公共文件
├── index.html                  # HTML 模板
├── vite.config.js              # Vite 配置
└── package.json                # 依赖配置
```

## 本地开发

### 环境要求

- Node.js 24+
- npm 11+

### 启动步骤

1. 安装依赖：

```bash
cd web
npm install
```

2. 启动开发服务器：

```bash
npm run dev
```

3. 访问地址：

```
http://localhost:5173
```

## 端口配置

- 开发端口：5173
- API 代理：`/api` → `http://localhost:8888`

## 功能模块

- 登录认证
- 数据看板
- 房间管理
- 房型管理
- 订单管理
- 客户管理
- 财务统计
- 运营任务
- 账号管理
- 系统设置

## UI 主题

- 深色背景：#030014
- 品牌紫色：#4C1D95
- 星蓝强调色：#2563EB
- 现代暗色星空风格
