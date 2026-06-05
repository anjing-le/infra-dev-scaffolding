# Remote Call Guide

本文档定义脚手架里的远程调用默认路径。当前母版仍保持单体可运行，但所有新增远程调用都应按未来服务拆分后的方式设计。

## 默认选择

- HTTP 服务间调用：优先使用 `RemoteHttpClient` / `RemoteHttpRequest`。
- 内部服务调用：优先传 `serviceId + path`，服务 base URL 在 `app.remote-http.service-base-urls` 中统一配置。
- 第三方外部 API：可以继续传绝对 `url`，但不要在业务代码里拼接内部服务地址。
- Feign / Dubbo / WebClient 自定义适配器：使用 `RemoteCallWrapper.serviceCallHeaders(callerId)` 生成透传头。
- 只需要包装已有 Java 方法调用：使用 `RemoteCallWrapper.call(...)` 或 `callWithRetry(...)`。

## HTTP 调用示例

```java
RemoteHttpRequest request = RemoteHttpRequest.builder()
        .method(HttpMethod.GET)
        .serviceId(ServiceBoundaryConstants.Auth.OWNER)
        .path(ApiConstants.Auth.ME_FULL)
        .callerId(ServiceBoundaryConstants.APPLICATION_ID)
        .retryCount(1)
        .checkResponse(true)
        .build();

APIResponse<CurrentUserResponse> response = remoteHttpClient.exchange(
        request,
        new ParameterizedTypeReference<APIResponse<CurrentUserResponse>>() {}
);
```

简单响应可以继续使用 `Class<T>` 重载；标准响应、分页响应或列表响应优先使用 `ParameterizedTypeReference<T>`，避免 `APIResponse<PageResult<Xxx>>` 这类泛型在服务间调用中退化为裸类型。

## 统一透传头

`RemoteHttpClient` 会按照 `contracts/platform-contract.json` 的 `backendPropagatedHeaders` 自动透传：

- `X-Request-Id`
- `X-Trace-Id`
- `X-Tenant-Id`
- `X-User-Id`
- `X-User-Name`
- `X-User-Roles`
- `X-Caller-Id`
- `X-Time-Zone`
- `Accept-Language`

没有入站请求上下文时，会生成新的 `X-Request-Id` 和 `X-Trace-Id`，适用于定时任务、异步任务和本地工具入口。

浏览器端只允许透传 `frontendPropagatedHeaders` 中的 requestId、traceId、timezone、language；租户、用户和 caller 这类身份上下文由网关、认证层或后端服务间调用链路生成和透传。

## 配置

默认配置在 `app.remote-http`：

```yaml
app:
  remote-http:
    default-caller-id: infra-dev-scaffolding
    connect-timeout-ms: 3000
    read-timeout-ms: 5000
    default-retry-count: 0
    default-retry-interval-ms: 1000
    service-base-urls:
      infra-dev-scaffolding: http://localhost:18080
      infra-auth: ${INFRA_AUTH_BASE_URL:}
```

`service-base-urls` 是未来 API Gateway、Service Discovery 或部署环境注入服务地址前的最小配置入口。母版不默认引入注册中心；下游项目可以先用环境变量切换服务地址，再在真实微服务项目中替换成网关或发现客户端。

## 日志与安全

`RemoteHttpClient` 交给 `RemoteCallWrapper` 的日志对象只包含 method、targetService、url 和 callerId，不输出请求体和 headers，避免 Authorization、Cookie、Token 或业务敏感参数进入日志。

## 重试策略

默认重试仍由 `RemoteCallWrapper` 判断：

- `1800-1899` 远程网络、连接、超时类错误可重试。
- `1600-1799` 远程契约或响应校验错误默认不重试。
- `2xxx` / `3xxx` / `4xxx` 业务、参数、权限错误默认不重试。

HTTP 状态映射：

- `408` / `504` -> `REMOTE_CALL_TIMEOUT`
- `429` / `502` / `503` -> `REMOTE_CALL_NETWORK_ERROR`
- 其他非 2xx 状态 -> `REMOTE_RESPONSE_STATUS_FAILED`
