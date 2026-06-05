# Architecture Evolution

本文档记录 `infra-dev-scaffolding` 作为工程脚手架继续演进时的架构判断。

结论：脚手架需要提前考虑微服务、分布式、全球化、时区、统一 URL 和共享工具，但不能把所有未来能力一次性塞进母版。母版应该沉淀稳定契约、基础抽象、目录边界和最小默认实现；重型平台能力通过可选适配层或下游 Infra 项目验证后再回流。

## 核心原则

1. 先定契约，再定实现。

   API 响应、错误码、请求上下文、时间格式、URL 命名、权限头、日志字段这些一旦分散，后面迁移成本很高。母版应优先统一这些契约。

2. 保留扩展点，不预装复杂平台。

   API Gateway、服务注册发现、链路追踪、消息队列、多租户计费、权限中心都应该被考虑，但不应默认成为母版运行依赖。母版提供接口、配置样例和接入边界即可。

3. 单体优先可跑，分布式优先可演进。

   当前项目仍应保持一个前端加一个 Spring Boot 后端即可启动。面向未来的设计体现在包结构、请求头、配置、日志和工具层，而不是提前拆多个服务。

4. 工具类可以共享，业务逻辑不能共享。

   时间、ID、JSON、校验、URL、错误码、请求上下文、HTTP client wrapper 可以沉淀为共享能力。具体 Agent、Skill、订单、支付、客服等领域能力应留在下游项目。

## 当前状态判断

已有基础：

- 后端已有 `ApiConstants`、`GlobalRequestContext`、`DateUtils`、`RemoteCallWrapper`、统一响应、异常、分布式锁、Redis/中间件开关。
- 前端已有 `utils/http`、`api/`、`types/`、`locales/`、`config/`、`utils/constants/links`、路由核心和权限守卫。
- 文档中已有 Roadmap、Status、Template Boundaries、Copy Guide、Release Checklist。

主要缺口：

- 前端 API URL 已开始收口到 `ApiPaths`，后续需要继续扩大覆盖面，并评估与后端 `ApiConstants` / OpenAPI 的生成关系。
- 响应字段仍处于兼容期，存在 `msg` / `message`、`BaseResponse` / `APIResponse` 两套语义。
- 时间策略已开始转向 UTC 默认和客户端时区展示，后续需要继续把存量页面时间展示迁移到统一工具。
- 请求上下文已开始具备 `requestId`、`traceId`、语言和时区透传，并已接入日志格式；后续需要继续接入远程调用和权限上下文。
- 前端已有统一时间工具层，后续需要让日期控件、文件名、通知时间等存量逻辑逐步迁移。
- 中间件开关已有，但缺少 dev/test/prod/profile 矩阵和“启用后必须满足什么验证”的清单。
- 微服务远程调用已有包装工具，但还不是接口驱动的 client contract，没有统一超时、重试、熔断、调用方身份和审计契约。

## 母版应该内置

这些是脚手架层面的长期资产，应该逐步落地：

- 统一 API 契约：`code`、`message`、`data`、`timestamp`、`requestId`、分页字段、错误码命名。
- 统一 URL 管理：后端 `ApiConstants` 与前端 `ApiPaths` / `apiUrl()`，禁止业务页面手写 URL 字符串。
- 统一请求上下文：请求进入后生成或透传 `X-Request-Id`，保留 `X-Trace-Id`、`X-Tenant-Id`、`X-User-Id`、`X-Time-Zone`、`Accept-Language`。
- 统一时间策略：服务端存储 UTC，接口输出 ISO-8601，前端按用户时区格式化展示。
- 统一环境配置：前端 env、后端 yml、README、复制指南中的端口、API base、应用名、数据库名保持一致。
- 统一工具层：ID、JSON、校验、时间、URL、存储、错误处理、日志脱敏。
- 统一日志字段：应用名、环境、requestId、traceId、userId、tenantId、耗时、错误码。
- 统一 AI 生成契约：Rules / Prompts 必须告诉 AI 新模块应使用统一响应、统一 URL、统一时间工具和统一 API client。

## 母版只保留扩展点

这些能力要考虑，但不应成为默认运行依赖：

- API Gateway：母版定义服务前缀、认证头和转发约定，下游 `infra-api-gateway` 负责具体网关。
- Auth Center：母版保留 mock auth 和 token/header 契约，下游 `infra-auth` 负责真实账号、租户、权限。
- Service Discovery：母版保留服务名、调用方身份和远程调用 wrapper，不默认引入注册中心。
- Distributed Tracing：母版保留 trace/request id 字段，不默认绑定某个 tracing 平台。
- MQ / Event Bus：母版保留事件命名和幂等建议，不默认要求 Kafka/RabbitMQ 启动。
- Object Storage：母版保留上传接口契约和配置样例，不默认绑定 MinIO/OSS 业务实现。
- Multi-region / Global Deploy：母版保留时区、语言、区域配置契约，不实现真实多活部署。

## 不应进入母版

- 具体业务领域模型和流程。
- 具体 Agent / Skill / LLM 编排业务。
- 具体支付、订单、客服、知识库、RAG 逻辑。
- 只能在某个下游项目使用的页面和接口。
- 必须依赖外部平台才能启动的默认能力。

## 演进阶段

### S4: 契约与全球化基线

目标：单体仍可运行，但 API、时间、上下文和 URL 已经具备未来拆分基础。

验收：

- 后端统一响应只保留一个标准结构，前端无需同时兼容 `msg` 和 `message`。
- 前后端新增统一 API path 管理，不再新增散落 URL 字符串。
- 后端新增请求上下文过滤器或拦截器，生成并返回 `X-Request-Id`。
- 后端时间配置支持 `APP_TIME_ZONE`，默认建议 UTC；展示时区由前端或用户配置决定。
- 前端新增 `utils/time`，所有新增页面通过统一工具格式化时间。
- Cursor Rules / Prompts 增加 API path、时间和请求上下文约束。

### S5: 分布式可观测基线

目标：即使未来拆服务，也能追踪一次请求从前端到后端再到远程调用的链路。

验收：

- 日志统一输出 `requestId`、`traceId`、`userId`、`tenantId`、接口路径、耗时、错误码。
- `RemoteCallWrapper` 支持调用方、目标服务、超时、重试和 requestId 透传。
- 错误码按模块分段，有文档说明哪些错误能重试、哪些必须提示用户。
- 健康检查和中间件状态能区分 disabled、configured、ready、degraded。

当前已完成：

- `logback-spring.xml` 已输出 MDC 中的 `requestId`、`traceId`、`tenantId`、`userId`。
- `ControllerLogAspect` 已复用 `RequestContextFilter` 生成的 requestId。

### S6: 服务边界与可选适配层

目标：母版能自然裁剪出 `infra-auth`、`infra-api-gateway`、`infra-skill-hub` 这类独立项目。

验收：

- 明确 auth、gateway、common、admin、business 的 API prefix 和包边界。
- 新项目复制后可以只保留需要的模块，不破坏构建。
- 可选中间件有 profile 示例和验证命令。
- 下游项目验证出的通用工具回流母版，领域能力留在下游。

### S7: 契约生成与共享包

目标：减少手写重复契约，让 AI 和人都沿着同一套约定生成代码。

验收：

- 评估 OpenAPI / 类型生成，将后端接口契约生成前端 types 或 API client。
- 评估抽出 `anjing-common` / `anjing-web-common`，只放稳定工具和类型。
- AI Prompts 生成模块时可以自动引用统一 URL、统一响应和统一时间工具。

## 近期推荐任务

1. 收敛 API 响应结构。

   统一为 `code`、`message`、`data`、`timestamp`、`requestId`。前端保留一次兼容期，之后删除 `msg` 兼容。

2. 建立前端 `ApiPaths`。

   新建集中 URL 管理文件，先覆盖 auth、system、user 这些已有 API。页面和服务不再直接手写 `/api/...`。

3. 建立时间策略。

   后端新增 `TimeZoneProperties` 或 `AppProperties`，默认 UTC；`DateUtils` 增加 `Instant` / `OffsetDateTime` 支持。前端新增 `utils/time`。

4. 建立请求上下文。

   后端通过 filter/interceptor 统一读取和生成 requestId、traceId、locale、timeZone。前端请求拦截器透传语言和时区。

5. 更新 AI Rules / Prompts。

   让后续 AI 生成的 CRUD、列表页、API 模块默认遵守统一 URL、响应、时间和上下文契约。

## 决策答案

要考虑那么多，但不要一次性实现那么多。

脚手架真正要做的是把未来分布式和全球化一定会用到的“共同语言”先定下来：URL、响应、错误码、时间、请求上下文、日志、配置、工具层。至于微服务网关、认证中心、注册中心、链路追踪平台、消息队列这些重能力，母版只保留入口和契约，让下游 Infra 项目按真实需求实现，再把成熟的共性部分回流。
