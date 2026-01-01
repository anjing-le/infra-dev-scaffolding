# 🚀 Agent Dev Scaffolding

**企业级全栈开发脚手架** — 开箱即用，专注业务

<p align="center">
  <img src="https://img.shields.io/badge/Vue-3.5-4FC08D?style=flat-square&logo=vue.js" alt="Vue">
  <img src="https://img.shields.io/badge/TypeScript-5.6-3178C6?style=flat-square&logo=typescript" alt="TypeScript">
  <img src="https://img.shields.io/badge/Vite-7-646CFF?style=flat-square&logo=vite" alt="Vite">
  <img src="https://img.shields.io/badge/Node.js-20+-339933?style=flat-square&logo=node.js" alt="Node.js">
  <img src="https://img.shields.io/badge/pnpm-10-F69220?style=flat-square&logo=pnpm" alt="pnpm">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.5-6DB33F?style=flat-square&logo=springboot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/JDK-17-ED8B00?style=flat-square&logo=openjdk" alt="JDK">
</p>

---

## ✨ 特性

- 🎯 **开箱即用** — 前后端配置完备，clone 即可开发
- 🔌 **中间件开关** — Redis/Kafka/MinIO 按需启用，未配置不报错
- 👤 **游客模式** — 无需后端权限系统即可体验完整 UI
- 🌙 **主题切换** — 亮色/暗色/跟随系统
- 🌍 **国际化** — 中英文开箱支持
- 📱 **响应式** — 适配多端屏幕

---

## 📋 环境要求

### 必需

| 环境 | 版本 | 说明 |
|------|------|------|
| Node.js | **20+** | 前端运行环境 |
| pnpm | 10+ | 前端包管理器 |
| JDK | 17+ | 后端运行环境 |
| Maven | 3.8+ | 后端构建工具 |
| MySQL | 8.0+ | 数据库 |

### 可选（按需启用）

| 环境 | 版本 | 说明 |
|------|------|------|
| Redis | 6.0+ | 缓存/分布式锁 |
| Kafka | 3.0+ | 消息队列 |
| MinIO | - | 对象存储 |

---

## 🚀 快速开始

```bash
# 后端
cd backend && mvn spring-boot:run
# 访问 http://localhost:18080

# 前端
cd frontend && pnpm install && pnpm dev
# 访问 http://localhost:13006
```

---

## 🏗️ 技术栈

| 前端 | 后端 |
|------|------|
| Vue 3.5 + TypeScript | Spring Boot 3.4.5 |
| Vite 7 | JDK 17 |
| Pinia + Vue Router | Spring Data JPA |
| Element Plus + Tailwind CSS | Redis + Redisson |
| ECharts 6 | MySQL + Druid |

---

## 📁 项目结构

```
agent-dev-scaffolding/
├── frontend/                # 前端工程
│   ├── src/views/          # 📄 页面
│   ├── src/api/            # 🔗 接口
│   ├── src/components/     # 🧩 组件
│   └── src/store/          # 📦 状态
│
└── backend/                 # 后端工程
    └── src/main/java/com/anjing/
        ├── controller/     # 🎯 控制器
        ├── service/        # ⚙️ 服务
        └── config/         # 🔧 配置
```

---

## ⚙️ 中间件开关

在 `application.yml` 中按需启用：

```yaml
app:
  features:
    redis:
      enabled: true       # Redis 缓存
    distributed-lock:
      enabled: true       # 分布式锁
    middleware:
      kafka:
        enabled: false    # 消息队列
      minio:
        enabled: false    # 对象存储
```

> 💡 未启用的中间件不会初始化，无需担心连接报错

---

## 📝 License

[MIT](LICENSE)

---

<p align="center">
  <sub>Made with ❤️ by Anjing Team</sub>
</p>
