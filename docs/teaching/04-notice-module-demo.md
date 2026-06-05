# 公告管理模块 Prompt 实战

本文用于录制或现场教学时演示：如何用本项目的 Cursor Prompts 生成第一组标准业务模块。

目标不是把公告管理做成底座功能，而是验证脚手架能稳定支撑一个新业务模块从后端到前端落地。

## 演示目标

- 后端生成 `Notice` CRUD 模块。
- 前端生成公告 API、列表页和新增/编辑弹窗。
- 生成代码不进入 `example` 包，也不添加 `@ScaffoldSample`。
- 生成后能通过后端打包、前端构建和模板自检。

## 准备

先确认母版本身是干净可用的：

```bash
./scripts/check-template.sh
cd backend && mvn -q -DskipTests package
cd ../frontend && pnpm build
```

如果是课程演示，可以在一个临时分支或复制出的项目中操作，避免把演示业务长期留在母版。

## 1. 后端 CRUD

在 Cursor 中选择 `backend/.cursor/prompts/crud-module.md`，填入下面参数：

| 参数 | 值 |
|------|----|
| 模块名称 | 公告管理 |
| 实体名称 | Notice |
| 表名 | t_notice |
| API 前缀 | /api/notices |
| 所属包 | com.anjing |

实体字段建议使用：

| 字段名 | 类型 | 说明 | 校验规则 |
|--------|------|------|---------|
| id | Long | 主键 | 自增 |
| title | String | 公告标题 | 必填，最长 80 |
| content | String | 公告内容 | 必填 |
| status | String | 状态：DRAFT / PUBLISHED | 必填 |
| priority | Integer | 排序优先级 | 0-999 |
| publishTime | LocalDateTime | 发布时间 | 可为空 |

期望生成文件：

- `backend/src/main/java/com/anjing/entity/Notice.java`
- `backend/src/main/java/com/anjing/repository/NoticeRepository.java`
- `backend/src/main/java/com/anjing/service/NoticeService.java`
- `backend/src/main/java/com/anjing/controller/NoticeController.java`
- `backend/src/main/java/com/anjing/model/request/NoticeRequest.java`
- `backend/src/main/java/com/anjing/model/request/NoticeSearchRequest.java`
- `backend/src/main/java/com/anjing/model/response/NoticeVO.java`
- `backend/src/main/java/com/anjing/model/response/NoticePageVO.java`
- `backend/src/main/java/com/anjing/model/errorcode/NoticeErrorCode.java`

后端人工检查点：

- `NoticeController` 返回 `APIResponse<T>`。
- 列表接口使用 `NoticeSearchRequest` 接收 `current`、`size` 和搜索条件。
- 列表响应使用 `NoticePageVO`，字段为 `records`、`current`、`size`、`total`，对齐前端 `PaginatedResponse<T>`。
- 业务异常使用 `BizException(NoticeErrorCode.*)`。
- 写操作使用事务。
- 真实业务代码在 `controller`、`service`、`repository`、`entity` 等业务目录中。
- 不要放入 `backend/src/main/java/com/anjing/example`。
- 不要给真实业务类添加 `@ScaffoldSample`。

后端验证：

```bash
cd backend
mvn -q -DskipTests package
```

接口 smoke 示例：

```bash
curl -X POST http://localhost:18080/api/notices \
  -H "Content-Type: application/json" \
  -d '{"title":"系统维护通知","content":"今晚 22:00-23:00 进行维护","status":"PUBLISHED","priority":10}' | python3 -m json.tool

curl "http://localhost:18080/api/notices?current=1&size=20" | python3 -m json.tool

curl -X PUT http://localhost:18080/api/notices/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"系统维护通知（更新）","content":"维护窗口调整为 22:30-23:30","status":"PUBLISHED","priority":20}' | python3 -m json.tool

curl -X DELETE http://localhost:18080/api/notices/1
```

## 2. 前端 API

在 Cursor 中选择 `frontend/.cursor/prompts/api-module.md`，填入：

| 参数 | 值 |
|------|----|
| 模块名称 | 公告管理 |
| API 文件 | `src/api/notice.ts` |
| Model 文件 | `src/api/model/noticeModel.ts` |
| API 前缀 | `/api/notices` |

期望导出函数：

- `fetchGetNoticeList`
- `fetchGetNoticeDetail`
- `fetchCreateNotice`
- `fetchUpdateNotice`
- `fetchDeleteNotice`

期望导出类型：

- `NoticeList`
- `NoticeListItem`
- `NoticeSearchParams`
- `CreateNoticeParams`
- `UpdateNoticeParams`

前端 API 人工检查点：

- API 函数统一使用 `fetch*` 前缀。
- Model 文件使用 `PaginatedResponse<T>` 和 `CommonSearchParams`。
- API 路径统一带 `/api` 前缀。
- 删除请求使用项目 HTTP 工具的 `request.del` 方法。
- API 层只定义请求，不在这里弹消息或处理页面状态。

## 3. 列表页

在 Cursor 中选择 `frontend/.cursor/prompts/vue-list-page.md`，填入：

| 参数 | 值 |
|------|----|
| 模块名称 | 公告管理 |
| 组件名称 | Notice |
| 文件位置 | `src/views/notice/index.vue` |
| 搜索组件 | `src/views/notice/modules/notice-search.vue` |

表格列建议：

- 标题：`title`
- 状态：`status`
- 优先级：`priority`
- 发布时间：`publishTime`
- 创建时间：`createTime`
- 操作：编辑、删除

搜索条件建议：

- 标题关键词：`title`
- 状态：`status`
- 发布时间范围：`publishTimeRange`

列表页人工检查点：

- 使用 `ArtTable` 展示数据。
- 使用 `ArtTableHeader` 提供刷新和列控制。
- 使用 `useTable` 管理分页、loading 和刷新。
- 调用 `fetchGetNoticeList` 和 `fetchDeleteNotice`。
- 页面内不要保留 `[ModuleName]`、`[module-name]` 这类占位符。

## 4. 新增/编辑弹窗

在 Cursor 中选择 `frontend/.cursor/prompts/vue-modal-form.md`，填入：

| 参数 | 值 |
|------|----|
| 模块名称 | 公告管理 |
| 组件名称 | NoticeDialog |
| 文件位置 | `src/views/notice/components/NoticeDialog.vue` |
| 组件类型 | Dialog |

表单字段建议：

- `title`：文本输入，必填，最长 80。
- `content`：多行文本，必填。
- `status`：下拉选择，DRAFT / PUBLISHED。
- `priority`：数字输入，0-999。
- `publishTime`：日期时间选择，可为空。

弹窗人工检查点：

- `visible` 使用 `defineModel`。
- 新增调用 `fetchCreateNotice`。
- 编辑调用 `fetchUpdateNotice`。
- 成功后 emit `success`，列表页刷新。
- 表单校验覆盖必填和长度规则。

## 5. 路由与菜单

新增路由建议放在：

- `frontend/src/router/modules/notice.ts`

路由建议：

```typescript
import { AppRouteRecord } from '@/types/router'

export const noticeRoutes: AppRouteRecord = {
  path: '/notice',
  name: 'NoticeRoot',
  component: '/index/index',
  meta: {
    title: '公告管理',
    icon: 'ri:notification-3-line',
    roles: ['R_SUPER', 'R_ADMIN']
  },
  children: [
    {
      path: 'list',
      name: 'Notice',
      component: '/notice/index',
      meta: {
        title: '公告列表',
        keepAlive: true,
        roles: ['R_SUPER', 'R_ADMIN']
      }
    }
  ]
}
```

同时在 `frontend/src/router/modules/index.ts` 中导入并加入 `routeModules`。如果演示项目使用后端动态菜单，则同步新增菜单数据；如果只做课程演示，可以临时加入静态路由验证页面渲染。

## 6. 完整验收

生成并微调后，运行：

```bash
./scripts/check-template.sh
cd backend && mvn -q -DskipTests package
cd ../frontend && pnpm build
```

演示结束后，如果公告模块只是教学产物，按下面方式处理：

- 录制用分支：保留提交，作为课程示例。
- 母版主分支：删除公告业务代码，只保留本演示文档。
- 新业务项目：保留公告模块，并把 README 和路由菜单改成业务项目口径。
