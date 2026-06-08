# infra-dev-scaffolding

Anjing 开源项目的全栈工程母版。

它提供一套可启动、可复制、可被 AI 协作工具理解的前后端基础工程。新项目可以从这里出发，再按业务场景裁剪和扩展。

## 技术栈

- Frontend: Vue 3.5 + TypeScript + Vite 7
- Backend: Spring Boot 3.4.5 + Java 17
- Dev/Test: 后端默认 H2 轻启动，生产可接 MySQL

## 你可以用它做什么

- 阅读：快速理解 Anjing 工程母版的结构、边界和约束。
- 使用：复制为新项目，改名、换端口、换业务模块后直接启动。
- 迭代：按统一 API、响应、OpenAPI、时间、上下文、UI 和质量门禁继续扩展。
- 接入：把 [接入提示词](./project_document/SCAFFOLD_ADOPTION_PROMPT.md) 复制给 Codex，让旧项目按本母版逐步重构。

## 快速开始

前端：

```bash
cd frontend
pnpm install
pnpm dev
```

打开 `http://localhost:13006`，可以用游客模式进入工作台。

后端：

```bash
cd backend
mvn spring-boot:run
```

后端默认端口是 `18080`。默认 dev profile 使用 H2，不需要本地 MySQL、Redis 或其他中间件。

## 质量门禁

提交前优先跑完整门禁：

```bash
./scripts/quality-gate.sh
```

只做文档或轻量调整时，可以先跑：

```bash
./scripts/check-template.sh
./scripts/check-contracts.sh
```

GitHub Actions 模板在 [project_document/ci/quality-gate.yml](./project_document/ci/quality-gate.yml)，启用后复用同一套 `./scripts/quality-gate.sh`。

## 迭代入口

- 先看 [project_document/PROJECT_CONSTRAINTS.md](./project_document/PROJECT_CONSTRAINTS.md)：母版能放什么、不能放什么。
- 新增模块看 [project_document/NEW_MODULE_GUIDE.md](./project_document/NEW_MODULE_GUIDE.md)：前后端交付顺序和 AI Prompt 约束。
- 改 UI 看 [project_document/UI_DESIGN_GUIDE.md](./project_document/UI_DESIGN_GUIDE.md)：极简、虚线、轻玻璃、少文字。
- 发布或录制看 [project_document/DEMO_EVIDENCE.md](./project_document/DEMO_EVIDENCE.md)：截图、probe 和质量门禁证据。
- 改造旧项目看 [project_document/SCAFFOLD_ADOPTION_PROMPT.md](./project_document/SCAFFOLD_ADOPTION_PROMPT.md)：可复制给 Codex 的接入提示词。

## 常用文档

- [当前状态](./project_document/STATUS.md)
- [复制为新项目](./project_document/COPY_GUIDE.md)
- [发布检查](./project_document/RELEASE_CHECKLIST.md)
- [接入提示词](./project_document/SCAFFOLD_ADOPTION_PROMPT.md)
- [API 契约](./project_document/API_CONTRACT_GUIDE.md)
- [服务边界](./project_document/SERVICE_BOUNDARY_GUIDE.md)
- [项目文档索引](./project_document/README.md)
- [贡献说明](./CONTRIBUTING.md)

## 目录

```text
frontend/           Vue 前端工程
backend/            Spring Boot 后端工程
contracts/          平台契约和服务边界 manifest
project_document/   路线图、约束、指南和状态记录
scripts/            自检、生成、复制和质量门禁脚本
```

## License

MIT. 前端工程基于 Art Design Pro 定制，保留 `frontend/LICENSE` 中的上游许可说明。
