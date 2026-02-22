# 演示步骤详解

> 本文档按录制顺序，提供每个演示环节的详细操作步骤。

---

## 演示 1：前端游客模式体验

### 准备
- 确保已安装 Node.js 20+ 和 pnpm 10+
- 终端定位到 `frontend/` 目录

### 操作步骤

```bash
# 1. 安装依赖
pnpm install

# 2. 启动开发服务器
pnpm dev
```

1. 浏览器自动打开 `http://localhost:13006`
2. 在登录页面，点击 **「游客访问」** 按钮
3. 系统自动登录并跳转到首页

### 功能展示

| 展示内容 | 操作 | 要点 |
|---------|------|------|
| 主题切换 | 点击右上角主题图标 | 亮色 / 暗色 / 跟随系统 |
| 国际化 | 点击语言切换 | 中文 ↔ English |
| 响应式 | 缩小浏览器窗口 | 侧边栏自动折叠 |
| 结果页 | 导航到「结果 → 成功」 | 标准结果页组件 |
| 异常页 | 导航到「异常 → 404」 | 美观的异常页面 |
| 权限控制 | 观察菜单 | 游客看不到「控制台」「系统管理」 |

---

## 演示 2：后端 API 启动与测试

### 准备
- 确保已安装 JDK 17+ 和 Maven 3.8+
- MySQL 8.0+ 运行中，已创建 `anjing` 数据库
- 终端定位到 `backend/` 目录

### 操作步骤

```bash
# 1. 确认数据库存在
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS anjing DEFAULT CHARACTER SET utf8mb4;"

# 2. 启动后端
mvn spring-boot:run
```

### API 测试流程

按以下顺序逐个演示：

```bash
# 1. 健康检查 — 验证服务正常
curl http://localhost:18080/api/test/health | python3 -m json.tool

# 2. Ping — 最简单的接口
curl http://localhost:18080/api/test/ping

# 3. 登录 — 获取 Token
curl -X POST http://localhost:18080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | python3 -m json.tool

# 4. 登录失败 — 演示统一异常处理
curl -X POST http://localhost:18080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"wrong"}' | python3 -m json.tool

# 5. 获取用户信息
curl http://localhost:18080/api/auth/me | python3 -m json.tool

# 6. CRUD — 创建
curl -X POST http://localhost:18080/api/test/items \
  -H "Content-Type: application/json" \
  -d '{"name":"公告1","content":"这是第一条公告"}' | python3 -m json.tool

# 7. CRUD — 再创建一条
curl -X POST http://localhost:18080/api/test/items \
  -H "Content-Type: application/json" \
  -d '{"name":"公告2","content":"这是第二条公告"}' | python3 -m json.tool

# 8. CRUD — 查询列表
curl http://localhost:18080/api/test/items | python3 -m json.tool

# 9. CRUD — 查询单条
curl http://localhost:18080/api/test/items/1 | python3 -m json.tool

# 10. CRUD — 更新
curl -X PUT http://localhost:18080/api/test/items/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"公告1（已更新）","content":"内容已修改"}' | python3 -m json.tool

# 11. CRUD — 删除
curl -X DELETE http://localhost:18080/api/test/items/2

# 12. 异常处理演示 — 业务异常
curl http://localhost:18080/api/test/exception/biz | python3 -m json.tool

# 13. 异常处理演示 — 系统异常
curl http://localhost:18080/api/test/exception/system | python3 -m json.tool

# 14. 查询不存在的记录 — 404 业务异常
curl http://localhost:18080/api/test/items/999 | python3 -m json.tool
```

### 关键讲解点
- 统一响应格式：所有接口都返回 `{code, message, data, timestamp}`
- 全局异常处理：业务异常和系统异常分别处理
- 错误码体系：每种错误有明确的错误码

---

## 演示 3：Cursor Rules 效果展示

### 对比演示

**无 Rules 场景**：
1. 新建一个空项目（或临时禁用 Rules）
2. 让 Cursor 生成一个 Vue 列表组件
3. 观察代码风格：可能不使用 Element Plus、不遵循项目结构

**有 Rules 场景**：
1. 在脚手架项目中
2. 让 Cursor 生成一个 Vue 列表组件
3. 观察代码风格：自动使用 Element Plus、遵循 TypeScript 规范、使用 Pinia

### 展示 Rules 文件
1. 打开 `frontend/.cursor/rules/README.mdc` — 总览
2. 打开 `frontend/.cursor/rules/vue-component.mdc` — Vue 组件规范
3. 打开 `backend/.cursor/rules/spring-boot.mdc` — Spring Boot 规范

---

## 演示 4：Cursor Prompts 实战

### 演示：用 Prompt 生成后端 CRUD 模块

1. `Cmd+Shift+P` → "Use Prompt Template" → 选择 `crud-module.md`
2. 填写参数：
   - 模块名称：公告管理
   - 实体名称：Notice
   - 表名：t_notice
   - API 前缀：/api/notices
3. 让 Cursor 生成代码
4. 检查生成的文件：Entity、Repository、Service、Controller
5. 运行后端，测试新接口

### 演示：用 Prompt 生成前端列表页

1. `Cmd+Shift+P` → "Use Prompt Template" → 选择 `vue-list-page.md`
2. 填写参数：
   - 模块名称：公告管理
   - 组件名称：NoticeList
3. 让 Cursor 生成代码
4. 配置路由
5. 在浏览器中查看新页面

---

## 演示 5：基于脚手架创建新项目

### 操作步骤

```bash
# 1. 复制项目
cp -r agent-dev-scaffolding my-notice-app
cd my-notice-app

# 2. 修改 backend/pom.xml
# artifactId: my-notice-app

# 3. 修改 backend/src/main/resources/application.yml
# spring.application.name: my-notice-app

# 4. 修改 frontend/package.json
# "name": "my-notice-app"

# 5.（可选）运行前端清理脚本
cd frontend && pnpm clean:dev

# 6. 初始化 Git
cd .. && git init && git add . && git commit -m "init: 基于脚手架初始化项目"
```

### 讲解要点
- 为什么用复制而不是 fork：脚手架是起点，不是上游
- 清理脚本的作用：移除演示页面，保留核心骨架
- 保留什么：Cursor Rules、配置类、工具类、异常处理体系
- 删除什么：TestController、example 目录、演示页面

---

## 演示环境检查清单

录制前请确认：

- [ ] Node.js 20+ 已安装（`node -v`）
- [ ] pnpm 10+ 已安装（`pnpm -v`）
- [ ] JDK 17+ 已安装（`java -version`）
- [ ] Maven 3.8+ 已安装（`mvn -v`）
- [ ] MySQL 8.0+ 运行中，`anjing` 数据库已创建
- [ ] Cursor 已安装并登录
- [ ] 浏览器开发者工具准备好（可选）
- [ ] 终端字体大小调大（录制用）
- [ ] IDE 字体大小调大（录制用）
