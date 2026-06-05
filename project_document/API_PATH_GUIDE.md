# API Path Guide

本文档定义前后端 API 路径的集中管理方式。路径是未来 OpenAPI、网关转发、微服务拆分和 AI 生成代码的共同契约。

## Backend

后端路径统一写入 `ApiConstants`：

```java
@RequestMapping(ApiConstants.Auth.BASE)
public class AuthController {
    @PostMapping(ApiConstants.Auth.LOGIN)
    public APIResponse<LoginVO> login(...) {
        ...
    }
}
```

约定：

- 每个模块提供 `BASE`。
- 子路径使用相对路径，例如 `/login`、`/{id}`。
- 需要给远程调用或文档使用完整路径时，提供 `*_FULL`。
- Controller 注解不要直接写 `"/api/..."`。
- 新增接口时同步补齐 `ApiConstants`。

## Frontend

前端路径统一写入 `src/api/paths.ts`：

```typescript
export const ApiPaths = {
  auth: {
    login: '/api/auth/login',
    me: '/api/auth/me'
  }
} as const
```

约定：

- API 模块只引用 `ApiPaths`，不要直接写 `url: '/api/...'`。
- 路径参数使用函数，并通过 `encodeURIComponent` 处理。
- 页面组件不直接拼接口路径。
- 新增后端接口时同步确认 `ApiConstants` 和 `ApiPaths` 命名一致。

## Current Runtime Paths

| 模块 | 后端常量 | 前端路径 |
|------|----------|----------|
| 登录 | `ApiConstants.Auth.LOGIN_FULL` | `ApiPaths.auth.login` |
| 当前用户 | `ApiConstants.Auth.ME_FULL` | `ApiPaths.auth.me` |
| 登出 | `ApiConstants.Auth.LOGOUT_FULL` | `ApiPaths.auth.logout` |
| 刷新 Token | `ApiConstants.Auth.REFRESH_FULL` | `ApiPaths.auth.refresh` |
| 健康检查 | `ApiConstants.Test.HEALTH_FULL` | `ApiPaths.test.health` |
| 可选能力状态 | `ApiConstants.Test.FEATURES_FULL` | `ApiPaths.test.features` |
| Ping | `ApiConstants.Test.PING_FULL` | `ApiPaths.test.ping` |
| 教学 items | `ApiConstants.Test.ITEMS_FULL` | `ApiPaths.test.items` |

## Future Direction

当后续接入 OpenAPI / 类型生成时，应以 `ApiConstants` 和 Controller 注解作为后端事实来源，生成或校验前端 `ApiPaths`。当前母版先用手写路径注册表保证可读、可复制、可被 AI 正确复用。

## Verification

发布母版或复制项目前运行：

```bash
./scripts/check-contracts.sh
```

该脚本会阻止运行 Controller 直接写 `"/api/..."`，也会阻止前端 API 模块绕过 `ApiPaths` 直接写接口 URL。

如果只想验证前后端运行路径是否一致，可以运行：

```bash
node scripts/check-api-path-parity.js
```

当前脚本会比对 `ApiConstants.Auth/Test` 和 `ApiPaths.auth/test` 的稳定运行接口。后续新增模块进入运行面时，应同步扩展该脚本的映射表。
