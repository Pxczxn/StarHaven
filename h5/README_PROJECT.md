# 星栖民宿管理系统 - 租客端 H5

## 技术栈

- React 18
- Vite 6
- React Router
- Axios
- dayjs (待安装)

## 项目结构

```
h5/
├── src/
│   ├── api/                    # 接口请求封装
│   ├── assets/                 # 静态资源
│   ├── components/             # 公共组件
│   ├── layouts/                # 布局组件
│   ├── pages/                  # 页面组件
│   ├── router/                 # 路由配置
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
cd h5
npm install
```

2. 启动开发服务器：

```bash
npm run dev
```

3. 访问地址：

```
http://localhost:5174
```

## 端口配置

- 开发端口：5174
- API 代理：`/api` → `http://localhost:8888`

## 功能模块

- 首页品牌展示
- 房间列表
- 房间详情
- 日期选择
- 在线预订
- 预订成功
- 订单查询
- 订单详情
- 联系我们

## 设计特点

- 移动端优先
- 星空主题
- 轻量简洁
- 无需登录即可浏览和预订
