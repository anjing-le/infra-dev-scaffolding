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
- API 路径优先引用或补充 `ApiConstants`
- 添加 Javadoc 注释
- 参数使用 Jakarta Validation 校验
- 异常使用 BizException 抛出
- 添加必要业务日志；不要手动生成 requestId/traceId
- 需要读取请求上下文时使用 `GlobalRequestContextHolder.current()`
- 时间处理使用 UTC 默认策略和 `DateUtils`
- 真实业务端点不要放进 `TestController` 或 `com.anjing.example`
- 真实业务代码不要添加 `@ScaffoldSample`
