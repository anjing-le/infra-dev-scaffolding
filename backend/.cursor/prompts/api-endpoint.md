# 生成 API 端点

请在 **[ControllerName]Controller** 中添加一个新的 API 端点。

## 端点信息

- **功能描述**: [功能描述]
- **HTTP 方法**: [GET/POST/PUT/DELETE]
- **路径**: [/api/xxx]
- **所属 Controller**: [ControllerName]Controller

## 请求参数

| 参数名 | 位置 | 类型 | 是否必填 | 说明 |
|--------|------|------|---------|------|
| [name] | [Body/Query/Path/Header] | [Type] | [是/否] | [说明] |

## 响应数据

```json
{
  "code": "0",
  "message": "操作成功",
  "requestId": "由后端自动填充",
  "data": {
    // 描述返回的数据结构
  }
}
```

## 要求

- 返回 `APIResponse<T>` 格式
- 分页响应使用 `APIResponse<PageResult<T>>` 或字段一致的 PageVO：`records`、`current`、`size`、`total`
- 无数据成功消息使用 `APIResponse.successMessage(...)`，String 数据使用 `APIResponse.successData(...)`
- API 路径引用或补充 `ApiConstants`，不要在注解中直接写字符串路径
- 如果是新增前端需要调用的接口，同时提醒补充 `src/api/paths.ts` 的 `ApiPaths`
- 如果新增稳定运行接口，同步扩展 `scripts/check-api-path-parity.js` 的映射表
- 添加 Javadoc 注释
- 参数使用 Jakarta Validation 校验
- 异常使用 BizException 抛出
- 新增错误码时遵循 `project_document/ERROR_CODE_GUIDE.md`，不要复用已有 code
- 添加必要业务日志；不要手动生成 requestId/traceId
- 需要读取请求上下文时使用 `GlobalRequestContextHolder.current()`
- 需要 HTTP 调用下游服务时使用 `RemoteHttpClient` / `RemoteHttpRequest`
- 需要自定义 Feign / Dubbo / WebClient adapter 时使用 `RemoteCallWrapper.serviceCallHeaders(callerId)` 生成上下文请求头
- 涉及可选中间件或基础能力状态时，复用 `MiddlewareManager` 的 `disabled/configured/ready/degraded` 语义，并参考 `project_document/FEATURE_STATUS_GUIDE.md`
- 时间处理使用 UTC 默认策略和 `DateUtils`，接口时间字符串优先使用 `DateUtils.nowIso()`
- 真实业务端点不要放进 `TestController` 或 `com.anjing.example`
- 真实业务代码不要添加 `@ScaffoldSample`
