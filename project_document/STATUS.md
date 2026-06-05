# Status

更新时间：2026-06-05

本文记录 `infra-dev-scaffolding` 当前作为 Anjing 开源工程母版的阶段状态和可验证证据。

## 阶段状态

| 阶段 | 状态 | 证据 |
|------|------|------|
| S0 构建与入口收口 | Ready | 前端 `pnpm build`、后端 `mvn -q -DskipTests package` 已通过 |
| S1 工程母版收口 | Ready | `./scripts/check-template.sh` 和 `./scripts/smoke-copy.sh` 已通过 |
| S2 AI 协作资产收口 | Ready | Notice prompt smoke 已在临时复制项目验证，母版只保留演示文档和 Prompt 契约 |
| S3 后续项目复用验证 | In progress | `infra-skill-hub` 已从本母版接入骨架，完成 H0 / H1 / H2 验证，并推进 H3 HTTP/MCP/INTERNAL 调度层、全局与注册级协议配置、凭据托管与选择体验、权限身份头联动、策略批量管理、默认策略模板、调用治理、审计查询、治理指标与前端治理面 |

## 当前证据链

母版发布前至少运行：

```bash
./scripts/check-template.sh
./scripts/smoke-copy.sh
(cd backend && mvn -q -DskipTests package)
(cd frontend && pnpm build)
(cd frontend && pnpm -s clean:dev)
```

AI 协作验证：

- `docs/teaching/04-notice-module-demo.md` 给出公告管理模块的完整 Prompt 演示流程。
- Notice prompt smoke 已验证后端 CRUD、前端 API、列表页、搜索组件、弹窗和路由能在复制项目中通过构建。
- 验证后回补的关键契约：
  - 后端列表响应字段为 `records`、`current`、`size`、`total`。
  - 前端删除请求使用项目 HTTP 工具的 `request.del`。

下游复用验证：

- `infra-skill-hub` 已从本母版复制接入前后端骨架。
- `infra-skill-hub` 已完成项目名、后端 `artifactId`、`spring.application.name`、数据库名和前端包名切换。
- `infra-skill-hub` 已通过自检、复制烟测、后端打包、前端构建和 clean dry-run。
- `infra-skill-hub` 已落地 `SkillRegistry` 后端 CRUD 和前端管理页，验证母版 Prompt 契约可支撑真实 Infra 领域模块。
- `infra-skill-hub` 已落地 `SkillDiscoveryController`、统一调用入口、`SkillInvocationLog` 和基础统计，验证母版可承载第二阶段领域演进。
- `infra-skill-hub` 已开始 H3，落地 `SkillProtocolDispatcher`、`SkillProtocolAdapter`、`HttpSkillProtocolAdapter` 和 `McpSkillProtocolAdapter`，验证母版可承载协议适配层演进。
- `infra-skill-hub` 已落地 `SkillGovernancePolicy` 和 `SkillGovernanceService`，支持启停、调用方白名单、日配额和失败熔断，验证母版可承载调用治理演进。
- `infra-skill-hub` 已落地 `SkillInvocationLogPageVO` 和 `/api/skill-governance/invocations`，支持调用审计与失败追踪查询，验证母版可承载运维查询能力演进。
- `infra-skill-hub` 已落地前端 `SkillGovernance` 页面、`SkillPolicyDialog` 和 `SkillInvocationDetailDialog`，验证母版可承载治理管理面的真实迭代。
- `infra-skill-hub` 已落地 `InternalSkillProtocolAdapter`、`InternalSkillHandler` 和 `SkillHubEchoInternalSkillHandler`，内置 `skill-hub.echo` 诊断 handler，验证母版可承载内部能力适配层演进。
- `infra-skill-hub` 已落地 `SkillGovernanceMetricsVO`、`/api/skill-governance/metrics` 和 `SkillGovernanceMetricsPanel`，验证母版可承载治理指标看板演进。
- `infra-skill-hub` 已落地 `SkillProtocolProperties`、`SkillProtocolMetadataResolver` 和 `SkillProtocolOptions`，支持 HTTP/MCP timeout、retry、authorization 和 headers 配置，验证母版可承载生产协议配置演进。
- `infra-skill-hub` 已落地 `SkillCredentialProperties`、`SkillCredentialResolver`、`SkillCredentialSummaryVO` 和 `SkillCredentialDrawer`，支持 `credentialRef` 引用后端托管凭据并提供脱敏查询，验证母版可承载凭据托管演进。
- `infra-skill-hub` 已在 `SkillRegistry` 注册模型中落地 `protocolConfig`，支持注册级协议默认配置并允许调用级 metadata 覆盖，验证母版可承载领域配置模型演进。
- `infra-skill-hub` 已在 Skill 注册弹窗中落地凭据引用选择体验，支持从脱敏凭据列表选择 `credentialRef` 并写入 `protocolConfig`，验证母版可承载配置选择型管理面演进。
- `infra-skill-hub` 已落地 `SkillInvokeCallerContext` 和请求身份头治理匹配，支持 `X-Skill-Caller-Id`、`X-User-Id`、`X-User-Name`、`X-User-Roles` 与 `role:R_ADMIN` 等白名单 token，验证母版可承载权限联动演进。
- `infra-skill-hub` 已落地 `SkillGovernancePolicyBatchRequest`、策略列表接口、批量启停接口和前端 `SkillPolicyDrawer`，验证母版可承载批量管理型治理页面演进。
- `infra-skill-hub` 已落地 `SkillGovernanceProperties`、`SkillGovernancePolicyApplyDefaultRequest`、`SkillGovernancePolicyTemplateVO` 和默认策略模板批量应用，验证母版可承载配置驱动的治理模板演进。

## 当前发布判断

当前状态适合作为 Anjing 内部后续开源项目的母版起点。

对外发布前仍建议补充：

- 手工 UI 录制或截图证据。
- 后端需要数据库的本地启动演示记录。

## 下一步

1. 继续在 `infra-skill-hub` 中推进认证中心 / API Gateway 职责边界和凭据生命周期管理。
2. 记录复用时还需要回流到底座的类型、路由、环境变量和 Prompt 契约问题。
3. 将可复用能力留在母版，将具体领域能力放进独立 Infra 项目。
