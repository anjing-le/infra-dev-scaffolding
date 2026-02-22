# Agent Dev Scaffolding

**企业级全栈开发脚手架** — 所有 Agent 项目的基础骨架，开箱即用，专注业务

<p align="center">
  <img src="https://img.shields.io/badge/Vue-3.5-4FC08D?style=flat-square&logo=vue.js" alt="Vue">
  <img src="https://img.shields.io/badge/TypeScript-5.6-3178C6?style=flat-square&logo=typescript" alt="TypeScript">
  <img src="https://img.shields.io/badge/Vite-7-646CFF?style=flat-square&logo=vite" alt="Vite">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.5-6DB33F?style=flat-square&logo=springboot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/JDK-17-ED8B00?style=flat-square&logo=openjdk" alt="JDK">
  <img src="https://img.shields.io/badge/Node.js-20+-339933?style=flat-square&logo=node.js" alt="Node.js">
</p>

---

## 这是什么？

这是一个**企业级全栈开发脚手架**，作为所有 Agent 项目（知识库、AIGC、智能客服等）的基础骨架。

你可以通过这个项目：

1. **熟悉工程化** — 理解前后端分离架构、分层设计、统一响应等企业级实践
2. **学会 AI 协作** — 掌握 Cursor Rules 和 Prompts，用 AI 提升 10 倍开发效率
3. **掌握扩展能力** — 在此基础上快速搭建新的业务项目

### 能干什么？

- **前端**：完整的后台管理系统 UI，支持游客模式直接体验（无需后端）
- **后端**：Spring Boot 基础设施 + API 示例，包含全局异常处理、参数校验、CRUD 演示
- **AI 工程化**：内置 Cursor Rules（编码规范）和 Prompts（代码生成模板）

---

## 技术栈

| 层级 | 前端 | 后端 |
|------|------|------|
| 框架 | Vue 3.5 + TypeScript | Spring Boot 3.4.5 + JDK 17 |
| 构建 | Vite 7 | Maven |
| 状态/ORM | Pinia 3 + Vue Router 4 | Spring Data JPA + Hibernate |
| UI/数据库 | Element Plus + Tailwind CSS 4 | MySQL 8.0 + Druid |
| 图表 | ECharts 6 | - |
| 缓存 | - | Redis + Redisson（可选） |
| 国际化 | vue-i18n | - |

---

## 环境要求

### 必需

| 环境 | 版本 | 说明 |
|------|------|------|
| Node.js | **20+** | 前端运行环境 |
| pnpm | **10+** | 前端包管理器 |
| JDK | **17+** | 后端运行环境 |
| Maven | **3.8+** | 后端构建工具 |
| MySQL | **8.0+** | 数据库 |

### 可选（按需启用）

| 环境 | 版本 | 说明 |
|------|------|------|
| Redis | 6.0+ | 缓存 / 分布式锁 |
| Kafka | 3.0+ | 消息队列 |
| MinIO | - | 对象存储 |

---

## 快速开始

### 方式一：仅体验前端（游客模式，无需后端）

```bash
cd frontend
pnpm install
pnpm dev
# 浏览器打开 http://localhost:13006
# 在登录页点击「游客访问」即可体验完整 UI
```

### 方式二：前后端完整启动

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS anjing DEFAULT CHARACTER SET utf8mb4;"

# 2. 配置后端环境变量（修改数据库密码）
cd backend
# 编辑 src/main/resources/application.yml 中的 DB_PASSWORD
# 或通过环境变量传入：export DB_PASSWORD=your_password

# 3. 启动后端
mvn spring-boot:run
# 后端运行在 http://localhost:18080
# Druid 监控面板：http://localhost:18080/druid

# 4. 启动前端（新终端）
cd frontend
pnpm install
pnpm dev
# 前端运行在 http://localhost:13006
```

### 测试后端 API

```bash
# 健康检查
curl http://localhost:18080/api/test/health

# Ping
curl http://localhost:18080/api/test/ping

# 登录（Mock：admin / admin123）
curl -X POST http://localhost:18080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# CRUD 示例 - 创建
curl -X POST http://localhost:18080/api/test/items \
  -H "Content-Type: application/json" \
  -d '{"name":"测试项目","description":"这是一个测试"}'

# CRUD 示例 - 查询列表
curl http://localhost:18080/api/test/items
```

---

## 项目结构

```
agent-dev-scaffolding/
├── README.md                        # 项目说明（本文件）
├── .gitignore                       # Git 忽略规则
│
├── frontend/                        # 前端工程（Vue 3 + Vite）
│   ├── .cursor/                     # Cursor AI 配置
│   │   ├── rules/                   # 12 条编码规范（Vue/TS/Style/Router...）
│   │   └── prompts/                 # 4 个代码生成模板
│   ├── src/
│   │   ├── api/                     # API 接口层
│   │   ├── assets/                  # 静态资源（图片/图标/样式）
│   │   ├── components/              # 组件
│   │   │   ├── core/                # 核心组件（布局/导航/主题）
│   │   │   └── business/            # 业务组件
│   │   ├── config/                  # 应用配置
│   │   ├── directives/              # 自定义指令（权限/角色/波纹）
│   │   ├── enums/                   # 枚举定义
│   │   ├── hooks/                   # 组合式函数（useAuth/useTheme...）
│   │   ├── locales/                 # 国际化（中/英）
│   │   ├── mock/                    # Mock 数据
│   │   ├── plugins/                 # 插件（ECharts）
│   │   ├── router/                  # 路由配置
│   │   │   ├── guard/               # 路由守卫
│   │   │   └── routes/              # 路由定义（静态 + 动态）
│   │   ├── store/                   # Pinia 状态管理
│   │   ├── types/                   # TypeScript 类型定义
│   │   ├── utils/                   # 工具函数
│   │   └── views/                   # 页面视图
│   │       ├── auth/                # 认证（登录/注册/忘记密码）
│   │       ├── dashboard/           # 仪表盘
│   │       ├── system/              # 系统管理（用户/角色/菜单）
│   │       ├── result/              # 结果页（成功/失败）
│   │       ├── exception/           # 异常页（403/404/500）
│   │       └── theme/               # 主题预览
│   ├── scripts/                     # 构建脚本
│   ├── .env                         # 通用环境变量
│   ├── .env.development             # 开发环境变量
│   ├── package.json
│   ├── vite.config.ts
│   └── tsconfig.json
│
├── backend/                         # 后端工程（Spring Boot）
│   ├── .cursor/                     # Cursor AI 配置
│   │   ├── rules/                   # 4 条编码规范（Java/SpringBoot/API/Git）
│   │   └── prompts/                 # 2 个代码生成模板
│   ├── src/main/java/com/anjing/
│   │   ├── controller/              # 控制器层（Auth + Test 示例）
│   │   ├── config/                  # 配置类
│   │   │   ├── http/                # Web/CORS 配置
│   │   │   ├── jackson/             # JSON 序列化配置
│   │   │   ├── jpa/                 # JPA 配置
│   │   │   ├── redis/               # Redis/Redisson 配置
│   │   │   ├── lock/                # 分布式锁配置
│   │   │   ├── middleware/          # 中间件管理
│   │   │   ├── properties/          # 配置属性
│   │   │   └── condition/           # 条件注解
│   │   ├── model/                   # 数据模型
│   │   │   ├── request/             # 请求 DTO
│   │   │   ├── response/            # 响应对象（APIResponse/PageResponse）
│   │   │   ├── constants/           # 常量（API 路径）
│   │   │   ├── enums/               # 枚举
│   │   │   ├── errorcode/           # 错误码（Auth/Common/Lock...）
│   │   │   ├── exception/           # 异常（BizException/SystemException）
│   │   │   └── validation/          # 自定义校验注解
│   │   ├── aspect/                  # AOP 切面（日志/锁/门面）
│   │   ├── annotation/              # 自定义注解（@Facade/@DistributeLock）
│   │   ├── example/                 # 功能示例（远程调用/校验/状态机）
│   │   ├── statemachine/            # 状态机基类
│   │   ├── util/                    # 工具类
│   │   └── Advice/                  # 全局异常处理器
│   ├── src/main/resources/
│   │   ├── application.yml          # 应用配置
│   │   └── logback-spring.xml       # 日志配置
│   ├── .env.example                 # 环境变量模板
│   └── pom.xml
│
└── docs/                            # 文档目录
    └── teaching/                    # 教学资料
```

---

## 中间件开关

在 `backend/src/main/resources/application.yml` 中按需启用：

```yaml
app:
  features:
    redis:
      enabled: true        # Redis 缓存
    distributed-lock:
      enabled: true        # 分布式锁（依赖 Redis）
    database:
      enabled: true        # 数据库
    middleware:
      kafka:
        enabled: false     # 消息队列
      minio:
        enabled: false     # 对象存储
      oss:
        enabled: false     # 云存储
```

> 未启用的中间件不会初始化，即使未安装也不会报错

---

## 游客模式

前端内置**游客模式**，无需后端即可体验完整 UI：

1. 确保 `frontend/.env` 中 `VITE_ACCESS_MODE = frontend`
2. 启动前端后，在登录页点击「游客访问」
3. 游客可访问：结果页、异常页、主题预览
4. 游客不可访问：仪表盘、系统管理（需要 ADMIN 角色）

---

## 基于脚手架创建新项目

### 步骤 1：复制项目

```bash
cp -r agent-dev-scaffolding my-new-project
cd my-new-project
```

### 步骤 2：修改项目标识

**后端 `pom.xml`**：
```xml
<artifactId>my-new-project</artifactId>
<name>my-new-project</name>
```

**后端 `application.yml`**：
```yaml
spring:
  application:
    name: my-new-project
```

**前端 `package.json`**：
```json
{
  "name": "my-new-project"
}
```

### 步骤 3：清理示例代码（可选）

```bash
# 前端：运行清理脚本，移除演示页面和 Mock 数据
cd frontend && pnpm clean:dev
```

后端：删除 `example/` 目录和 `TestController`，保留 `AuthController` 作为参考。

### 步骤 4：添加业务代码

- **后端**：在 `controller/`、`service/`、`repository/` 下按模块添加
- **前端**：在 `views/` 下添加页面，在 `router/routes/modules/` 下注册路由
- 使用 Cursor Prompts 快速生成代码骨架

---

## Cursor AI 协作说明

本项目深度集成 [Cursor](https://cursor.com/) AI 编程助手，提供两种协作工具：

### Cursor Rules（编码规范）

规则文件位于 `.cursor/rules/`，Cursor 在编码时会自动参考这些规范：

| 项目 | Rules 数量 | 覆盖范围 |
|------|-----------|---------|
| 前端 | 12 条 | Vue 组件、TypeScript、路由、状态管理、样式、性能、Git 等 |
| 后端 | 4 条 | Java 风格、Spring Boot 规范、API 设计、Git 提交 |

### Cursor Prompts（代码生成模板）

Prompts 位于 `.cursor/prompts/`，可快速生成标准化代码：

| 项目 | 模板 | 功能 |
|------|------|------|
| 前端 | `vue-list-page.md` | 生成列表页面 |
| 前端 | `vue-modal-form.md` | 生成弹窗表单 |
| 前端 | `vue-business-component.md` | 生成业务组件 |
| 前端 | `api-module.md` | 生成 API 模块 |
| 后端 | `crud-module.md` | 生成完整 CRUD 模块 |
| 后端 | `api-endpoint.md` | 生成单个 API 端点 |

### 使用流程

1. 安装 [Cursor](https://cursor.com/) 并用它打开项目
2. Rules 会自动生效，无需手动配置
3. 使用 Prompts：`Cmd+Shift+P` → "Use Prompt Template" → 选择模板
4. 在模板中替换 `[占位符]` 为实际值，让 AI 生成代码

---

## 后端 API 速览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 健康检查 | GET | `/api/test/health` | 服务状态 |
| Ping | GET | `/api/test/ping` | 连通性测试 |
| 异常演示 | GET | `/api/test/exception/biz` | 业务异常处理 |
| 异常演示 | GET | `/api/test/exception/system` | 系统异常处理 |
| 创建记录 | POST | `/api/test/items` | CRUD 示例 |
| 查询列表 | GET | `/api/test/items` | CRUD 示例 |
| 查询详情 | GET | `/api/test/items/{id}` | CRUD 示例 |
| 更新记录 | PUT | `/api/test/items/{id}` | CRUD 示例 |
| 删除记录 | DELETE | `/api/test/items/{id}` | CRUD 示例 |
| 登录 | POST | `/api/auth/login` | Mock 认证 |
| 用户信息 | GET | `/api/auth/me` | Mock 用户信息 |
| 登出 | POST | `/api/auth/logout` | 登出 |
| 刷新 Token | POST | `/api/auth/refresh` | 刷新 Token |

---

## License

[MIT](LICENSE)

---

<p align="center">
  <sub>Made with care by Anjing Team</sub>
</p>
