# 生成 CRUD 模块

请为 **[模块名称]** 生成完整的 CRUD 模块代码。

## 模块信息

- **模块名称**: [模块名称]（如：用户管理）
- **实体名称**: [EntityName]（如：User）
- **表名**: [table_name]（如：t_user）
- **API 前缀**: /api/[resource]（如：/api/users）
- **所属包**: com.anjing
- **业务包要求**: 真实业务代码放在 `controller`、`service`、`repository`、`entity` 等业务目录中，不要放进 `example` 包

## 实体字段

| 字段名 | 类型 | 说明 | 校验规则 |
|--------|------|------|---------|
| id | Long | 主键 | 自增 |
| [fieldName] | [Type] | [说明] | [校验] |

## 需要生成的文件

1. **Entity** - `entity/[EntityName].java`
   - 使用 JPA 注解
   - 包含 @CreatedDate, @LastModifiedDate 审计字段
   - 使用 Lombok @Data

2. **Repository** - `repository/[EntityName]Repository.java`
   - 继承 JpaRepository
   - 包含常用查询方法

3. **Service** - `service/[EntityName]Service.java`
   - CRUD 业务逻辑
   - 事务管理
   - 参数校验

4. **Controller** - `controller/[EntityName]Controller.java`
   - RESTful API
   - 统一返回 APIResponse
   - API 路径引用 `ApiConstants`，并在 `ApiConstants` 中新增模块 `BASE`、子路径和必要的 `*_FULL`
   - 若该模块进入运行面，同步在 `contracts/service-boundaries.json` 增加 boundary 和 routes
   - 参数校验注解
   - 不要添加 `@ScaffoldSample`

5. **Request DTO** - `model/request/[EntityName]Request.java`
   - 创建和更新请求
   - Jakarta Validation 注解

6. **Search Request** - `model/request/[EntityName]SearchRequest.java`
   - 列表查询请求
   - 分页字段使用 `current` 和 `size`，对齐前端 `useTable`
   - 支持模块常用搜索字段

7. **Response VO** - `model/response/[EntityName]VO.java`
   - 返回给前端的数据
   - 不要用 `Map<String, Object>` 代替真实业务响应模型
   - 可补充 `@Schema` 说明，方便 `/v3/api-docs` 和后续前端类型生成

8. **Page VO** - `model/response/[EntityName]PageVO.java`
   - 列表响应字段使用 `records`、`current`、`size`、`total`
   - 对齐前端 `PaginatedResponse<T>` 和 `useTable` 默认响应适配

9. **ErrorCode** - `model/errorcode/[EntityName]ErrorCode.java`
   - 模块专用错误码
   - 按 `project_document/ERROR_CODE_GUIDE.md` 分配 code；新业务模块优先使用 `2400-2999`

## 代码规范

- 统一返回 `APIResponse<T>`
- 真实业务接口使用明确 Request / Response DTO，不要用 `Map<String, Object>` 作为请求体或响应数据
- 新增或复用 `ApiConstants` 中的路径常量，Controller 注解不要硬编码 URL
- 新增运行模块前确认 `contracts/service-boundaries.json` 的服务边界，避免和 auth/common/admin/integration 等未来服务冲突
- 同步提醒前端在 `src/api/paths.ts` 的 `ApiPaths` 中补齐对应路径
- 列表接口 `GET /api/[resource]` 使用 `@ModelAttribute [EntityName]SearchRequest`
- 列表接口返回 `APIResponse<PageResult<[EntityName]VO>>`，或返回字段完全一致的 `[EntityName]PageVO`
- 分页字段必须是 `records`、`current`、`size`、`total`，不要使用旧字段 `datas/currentPage/pageSize`
- 异常使用 `BizException(ErrorCode)` 
- 直接返回错误时使用 `APIResponse.error(ErrorCode)`；无数据成功消息使用 `APIResponse.successMessage(...)`
- 错误码必须实现 `ErrorCode`，不要复用已有 code；远程/超时类错误不要放进业务模块错误码
- 日志使用 `@Slf4j`，不要手动拼 requestId/traceId；日志格式会从 MDC 输出
- 依赖注入使用 `@RequiredArgsConstructor`
- DTO / VO / ErrorCode 如果属于可复用契约，不能依赖 Spring Web、Servlet、JPA 或运行时层；共享边界参考 `project_document/SHARED_KERNEL_GUIDE.md`
- 时间字段优先使用 `Instant` / `OffsetDateTime`；需要格式化或当前时间时使用 `DateUtils`
- 如需读取请求上下文，使用 `GlobalRequestContextHolder.current()`，不要在业务代码中重新生成 requestId
- 如需 HTTP 调用下游服务，优先使用 `RemoteHttpClient` / `RemoteHttpRequest`
- 如需自定义 Feign / Dubbo / WebClient adapter，使用 `RemoteCallWrapper.serviceCallHeaders(callerId)` 生成上下文请求头
- 参考 `AuthController` 的统一响应、校验和日志风格
- `TestController` 和 `example/` 是教学示例，只能作为 API 演示参考，不能把生成的业务代码放入这些位置
- 生成后需要通过 `mvn -q -DskipTests package`
