# OpenAPI Contract Guide

OpenAPI 是后端运行接口给前端、AI Prompts、网关和未来服务调用方看的机器可读契约。母版先提供轻量 JSON 文档入口，不默认引入 Swagger UI；后续可以基于该入口接入前端类型或 API client 生成。

服务/模块归属由 `contracts/service-boundaries.json` 记录。OpenAPI 负责描述运行接口细节，service boundary 负责描述这些接口当前由谁承载、未来可能迁到哪个服务。

## Runtime Endpoint

开发和测试环境默认启用：

```text
/v3/api-docs
```

生产环境默认关闭，可通过环境变量显式开启：

```bash
OPENAPI_API_DOCS_ENABLED=true
```

当前依赖使用：

```xml
<artifactId>springdoc-openapi-starter-webmvc-api</artifactId>
```

`backend/src/main/java/com/anjing/config/openapi/OpenApiConfig.java` 负责：

- 只匹配 `PlatformContractConstants.API_PREFIX + "/**"`。
- 只扫描 `com.anjing.controller`。
- 给每个 operation 补充 requestId、traceId、tenantId、callerId、timeZone、language 等平台请求头。

## DTO / VO Rules

- 运行接口优先返回明确 DTO / VO，不要用 `Map<String, Object>` 承载真实业务 payload。
- 请求体使用明确 Request 类，并配合 Jakarta Validation。
- 返回体统一包在 `APIResponse<T>` 中。
- 分页响应使用 `PageResult<T>` 或字段完全一致的 VO。
- 教学接口可以保留 Map 演示，但真实业务 Controller 不应复制这种写法。
- 需要让 OpenAPI 更清晰时，可在 Request / Response 模型上补充 `@Schema`。

## Frontend Direction

当前前端仍保留手写类型，例如：

- `frontend/src/api/model/authModel.ts`
- `frontend/src/types/api/api.d.ts`

后续接入类型生成时，建议从 `/v3/api-docs` 生成到独立目录，例如 `frontend/src/contracts/openapi/`，再由 `src/api/**` 显式引用。不要让页面直接依赖生成目录，避免生成格式变化影响业务页面。

## Verification

静态契约检查：

```bash
node scripts/check-openapi-contract.js
```

开发环境运行检查会通过 `./scripts/probe-backend-dev.sh` 拉取 `/v3/api-docs`，确认 OpenAPI JSON、运行路径和平台请求头可用。

完整母版检查：

```bash
./scripts/check-contracts.sh
```
