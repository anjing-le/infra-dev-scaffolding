# 讲解大纲 — Agent Dev Scaffolding

> 预估总时长：90-120 分钟  
> 目标：学员掌握脚手架结构、能独立启动项目、理解 AI 协作开发模式

---

## Part 1：项目介绍与定位（10 分钟）

### 1.1 开场（3 分钟）
- 这个项目是什么：所有 Agent 项目的基础骨架
- 为什么需要脚手架：统一规范、快速起步、减少重复工作
- 项目与后续课程的关系：知识库 → AIGC → 客服，都从这里生长

### 1.2 技术栈概览（4 分钟）
- 前端：Vue 3.5 + TypeScript + Vite 7 + Element Plus + Tailwind CSS
- 后端：Spring Boot 3.4.5 + JDK 17 + JPA + MySQL
- 为什么选这套技术栈（企业主流 + 社区活跃 + AI 友好）

### 1.3 项目结构快速浏览（3 分钟）
- 打开 IDE，展示项目目录
- 前端 `frontend/` 和后端 `backend/` 分离
- 关键目录简单过一遍

---

## Part 2：前端工程详解（25 分钟）

### 2.1 启动项目 + 游客模式演示（5 分钟）
- `pnpm install && pnpm dev`
- 打开浏览器，点击「游客访问」
- 浏览各个页面：主题切换、国际化、响应式
- 讲解游客模式的价值：快速体验、无需后端

### 2.2 前端架构（8 分钟）
- **路由系统**：静态路由（登录/404）+ 动态路由（业务页面）
- **权限模型**：角色（R_SUPER/R_ADMIN/R_GUEST）→ 菜单过滤
- **状态管理**：Pinia stores（user/menu/setting）
- **API 层**：axios 封装 + 统一请求/响应拦截

### 2.3 游客模式实现原理（5 分钟）
- 登录页 `handleGuestLogin()` 的实现
- `guest_token` 设置 + `R_GUEST` 角色
- 路由守卫中的特殊处理
- 菜单过滤：有 roles 限制的路由对游客不可见

### 2.4 前端工程化亮点（7 分钟）
- 环境变量管理（`.env` / `.env.development`）
- 权限模式切换（`VITE_ACCESS_MODE`）
- 国际化方案（`locales/langs/`）
- 自定义指令（`v-auth` / `v-roles`）
- TypeScript 类型系统
- 代码规范工具链（ESLint + Prettier + Stylelint + Commitizen）

---

## Part 3：后端工程详解（25 分钟）

### 3.1 启动后端 + API 测试（5 分钟）
- `mvn spring-boot:run`
- 使用 curl 或 Postman 测试：
  - `/api/test/health` — 健康检查
  - `/api/test/ping` — 连通性
  - `/api/auth/login` — 登录
  - `/api/test/items` — CRUD 全流程

### 3.2 后端架构（8 分钟）
- 分层架构：Controller → Service → Repository → Entity
- 统一响应：`APIResponse<T>` 的设计
- 全局异常处理：`GlobalExceptionHandler` + `BizException` + `ErrorCode` 枚举
- 参数校验：Jakarta Validation + 自定义注解（`@PhoneNumber` / `@EmailAddress`）

### 3.3 中间件开关机制（5 分钟）
- `FeatureProperties` 配置属性类
- 条件化加载：`@ConditionalOnProperty`
- `MiddlewareManager` 启动健康检查
- 演示：启用/禁用 Redis 的效果

### 3.4 后端功能示例（7 分钟）
- `example/` 目录的三个示例：
  - `RemoteCallExampleService` — 远程调用封装
  - `ValidationExampleService` — 分组校验 + @Facade 注解
  - `OrderStateMachine` — 状态机模式
- AOP 切面：控制器日志、SQL 日志、分布式锁

---

## Part 4：Cursor AI 协作开发（20 分钟）

### 4.1 Cursor Rules 讲解（8 分钟）
- 什么是 Cursor Rules：让 AI 理解项目规范的配置文件
- 前端 12 条 Rules 概览（`.cursor/rules/`）
- 后端 4 条 Rules 概览
- 实际效果演示：有 Rules 和没 Rules 的代码质量对比

### 4.2 Cursor Prompts 实战（8 分钟）
- 什么是 Prompts：代码生成模板，一键生成标准代码
- 演示1：用 `vue-list-page.md` 生成一个新的列表页
- 演示2：用 `crud-module.md` 生成后端 CRUD 模块
- 演示3：让 AI 参考现有代码风格生成新功能

### 4.3 AI 开发最佳实践（4 分钟）
- 先写 Rules，再写代码
- 用 Prompts 生成骨架，手动微调细节
- 让 AI 做重复工作，人做决策工作
- 代码审查：AI 生成的代码也需要 review

---

## Part 5：实战演练（15 分钟）

### 5.1 任务：基于脚手架创建一个新模块（15 分钟）

现场演示"添加一个公告管理模块"的完整流程：

1. **后端**：使用 Cursor Prompt 生成 CRUD 模块
   - Entity、Repository、Service、Controller
   - 测试接口是否正常

2. **前端**：使用 Cursor Prompt 生成列表页
   - 创建路由配置
   - 创建页面组件
   - 连接 API

3. **联调**：前后端联调，展示完整功能

---

## Part 6：总结与答疑（10 分钟）

### 6.1 知识点回顾（5 分钟）
- 脚手架的核心价值
- 前后端关键架构点
- AI 协作开发的正确姿势

### 6.2 后续学习路径（2 分钟）
- 下一步：知识库项目（在此脚手架上添加 RAG 功能）
- 推荐阅读：Vue 3 官方文档、Spring Boot 官方指南

### 6.3 答疑（3 分钟）
