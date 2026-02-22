# 生成 CRUD 模块

请为 **[模块名称]** 生成完整的 CRUD 模块代码。

## 模块信息

- **模块名称**: [模块名称]（如：用户管理）
- **实体名称**: [EntityName]（如：User）
- **表名**: [table_name]（如：t_user）
- **API 前缀**: /api/[resource]（如：/api/users）
- **所属包**: com.anjing

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
   - 参数校验注解

5. **Request DTO** - `model/request/[EntityName]Request.java`
   - 创建和更新请求
   - Jakarta Validation 注解

6. **Response VO** - `model/response/[EntityName]VO.java`
   - 返回给前端的数据

7. **ErrorCode** - `model/errorcode/[EntityName]ErrorCode.java`
   - 模块专用错误码

## 代码规范

- 统一返回 `APIResponse<T>`
- 异常使用 `BizException(ErrorCode)` 
- 日志使用 `@Slf4j`
- 依赖注入使用 `@RequiredArgsConstructor`
- 参考现有的 `AuthController` 和 `TestController` 风格
