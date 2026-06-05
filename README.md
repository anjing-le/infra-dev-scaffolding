# infra-dev-scaffolding

> 类型：传统 infra | 定位：所有项目的工程起点

Anjing 开源项目的全栈工程母版。

它提供一套可启动、可演示、可复制的前后端基础工程，内置 AI 协作流程（Cursor Rules + Prompts）。后续所有 infra 和 agent 项目都应基于这套结构生长，再按业务场景裁剪和扩展。

## 技术栈

- Vue 3.5 + TypeScript + Vite 7（前端）
- Spring Boot 3.4.5 + MySQL（后端，dev/test 内置 H2 轻启动）

## 项目目标

- 开箱能跑：前端游客模式无需后端即可体验完整 UI。
- 工程规范稳定：统一目录、类型、路由、权限、请求、响应、异常、日志和环境配置。
- 可复制：新项目可以从这里复制并快速改名、换端口、换业务模块。
- 可教学：支撑脚手架课程、项目演示和后续 Agent 项目讲解。
- 可被 AI 协作：让 Cursor Rules / Prompts 成为生成标准代码的入口。

## 核心能力

- 登录 / 工作台 / 系统管理
- 主题切换、国际化、路由守卫、权限控制
- 游客模式，无后端可体验完整 UI
- 前端 11 条 Cursor Rules、后端 4 条 Cursor Rules
- 前端 4 个 Prompts、后端 2 个 Prompts

## 端口

| 模块 | 默认端口 | 说明 |
|------|----------|------|
| frontend | `13006` | Vite dev server |
| backend | `18080` | Spring Boot API |

## 快速开始

### 前端

```bash
cd frontend
pnpm install
pnpm dev
```

打开 `http://localhost:13006`，点击“游客访问”进入工作台。

### 后端

```bash
cd backend
mvn spring-boot:run
```

默认 `dev` profile 使用内存 H2，无需本地 MySQL/Redis 即可启动。需要连接 MySQL 时，复制或参考 `backend/.env.example` 配置 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD`。

## 验证

```bash
./scripts/check-template.sh
```

```bash
./scripts/check-contracts.sh
```

```bash
node scripts/check-api-constants.js
```

```bash
node scripts/check-api-path-parity.js
```

```bash
node scripts/generate-platform-contract-backend.js --check
```

```bash
node scripts/generate-platform-contract-frontend.js --check
```

```bash
node scripts/check-platform-contract.js
```

```bash
node scripts/check-error-codes.js
```

```bash
node scripts/check-shared-kernel.js
```

```bash
./scripts/smoke-copy.sh
```

```bash
cd frontend
pnpm build
```

```bash
cd backend
mvn -q -DskipTests package
```

```bash
./scripts/probe-backend-dev.sh
```

## 项目文档

- [项目路线图](./project_document/ROADMAP.md)
- [架构演进蓝图](./project_document/ARCHITECTURE_EVOLUTION.md)
- [API 契约指南](./project_document/API_CONTRACT_GUIDE.md)
- [API 路径指南](./project_document/API_PATH_GUIDE.md)
- [平台契约 Manifest 指南](./project_document/PLATFORM_CONTRACT_GUIDE.md)
- [共享内核指南](./project_document/SHARED_KERNEL_GUIDE.md)
- [环境 Profile 指南](./project_document/ENVIRONMENT_PROFILE_GUIDE.md)
- [本地启动指南](./project_document/LOCAL_STARTUP_GUIDE.md)
- [可选能力状态指南](./project_document/FEATURE_STATUS_GUIDE.md)
- [远程调用指南](./project_document/REMOTE_CALL_GUIDE.md)
- [错误码分段指南](./project_document/ERROR_CODE_GUIDE.md)
- [当前状态](./project_document/STATUS.md)
- [发布检查清单](./project_document/RELEASE_CHECKLIST.md)
- [复制为新项目指南](./project_document/COPY_GUIDE.md)
- [母版边界说明](./project_document/TEMPLATE_BOUNDARIES.md)
- [AI 资产清单](./project_document/AI_ASSETS.md)
- [教学资料](./docs/teaching/README.md)
- [贡献说明](./CONTRIBUTING.md)

## 后续复用方向

- `agent-aigc`：复用前端控制台、API 层、权限和环境配置，扩展多模型调度。
- `agent-customer-service`：复用工程结构和后端基础能力，扩展客服会话与规则兜底。
- `agent-knowledge`：复用前后端骨架，扩展 RAG、文档解析和引用回传。
- `infra-auth` / `infra-api-gateway` / `infra-llm-gateway` / `infra-skill-hub`：从本项目抽象出独立基础设施能力。

## License

MIT. 前端工程基于 Art Design Pro 定制，保留 `frontend/LICENSE` 中的上游许可说明。
