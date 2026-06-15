# infra-dev-scaffolding

Anjing 全栈工程母版：能启动，能复制，能被 AI 接手。

它把前端、后端、契约、约束和质量门禁放在一起，让新项目从第一天就有清晰边界。

`Vue 3.5` · `TypeScript` · `Vite 7` · `Spring Boot 3.4.5` · `Java 17` · `OpenAPI` · `H2 / MySQL`

## 它解决什么

| 场景 | 提供 |
| --- | --- |
| 快速开始 | 一套可运行的 Vue + Spring Boot 基线 |
| 复制新项目 | 改名、换端口、换业务模块的迁移路径 |
| AI 协作 | 可复制给 Codex 的接入提示词和项目约束 |
| 持续迭代 | API、时间、上下文、UI、服务边界等统一规则 |
| 避免漂移 | 本地脚本和 CI 模板复用同一套质量门禁 |

## 快速开始

前端：

```bash
cd frontend
pnpm install
pnpm dev
```

打开 `http://localhost:13006`。

后端：

```bash
cd backend
mvn spring-boot:run
```

后端端口是 `18080`。默认 dev profile 使用 H2，本地不需要 MySQL、Redis 或其他中间件。

## 技术栈

| 层 | 技术 |
| --- | --- |
| Frontend | Vue 3.5, TypeScript, Vite 7, Pinia, Vue Router |
| UI | Element Plus, SCSS, Tailwind CSS 4 |
| Backend | Spring Boot 3.4.5, Java 17, Maven |
| Data | H2 for dev/test, MySQL-ready |
| Contract | OpenAPI, generated types, API path checks |
| Governance | Shell/Node scripts, scaffold constraints, CI template |

## 使用入口

| 想做什么 | 看这里 |
| --- | --- |
| 复制成新项目 | [COPY_GUIDE.md](./project_document/COPY_GUIDE.md) |
| 让 Codex 接手 | [SCAFFOLD_ADOPTION_PROMPT.md](./project_document/SCAFFOLD_ADOPTION_PROMPT.md) |
| 理解项目约束 | [PROJECT_CONSTRAINTS.md](./project_document/PROJECT_CONSTRAINTS.md) |
| 新增业务模块 | [NEW_MODULE_GUIDE.md](./project_document/NEW_MODULE_GUIDE.md) |
| 调整界面风格 | [UI_DESIGN_GUIDE.md](./project_document/UI_DESIGN_GUIDE.md) |
| 发布前检查 | [DEMO_EVIDENCE.md](./project_document/DEMO_EVIDENCE.md) |

## 质量门禁

重要提交前跑完整门禁：

```bash
./scripts/quality-gate.sh
```

文档或轻量模板调整：

```bash
./scripts/check-template.sh
./scripts/check-contracts.sh
```

GitHub Actions 模板在 [project_document/ci/quality-gate.yml](./project_document/ci/quality-gate.yml)。

## 目录

```text
frontend/           Vue 前端工程
backend/            Spring Boot 后端工程
contracts/          平台契约和服务边界 manifest
project_document/   路线图、约束、指南和状态记录
scripts/            自检、生成、复制和质量门禁脚本
```

## License

MIT. 前端工程基于 Art Design Pro 定制，保留 [frontend/LICENSE](./frontend/LICENSE) 中的上游许可说明。
