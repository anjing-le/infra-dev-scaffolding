# Vue 列表页面生成 Prompt

## 使用说明

用于生成标准的 Vue 列表页面，包含搜索、表格、操作按钮等常用功能。

## Prompt 模板

### 创建新的列表页面

```
请为 [模块名称] 创建一个完整的 Vue 列表页面，需要包含：

### 基础要求：
1. **技术栈**：Vue 3 + TypeScript + Composition API + Element Plus
2. **文件位置**：src/views/[模块名称]/index.vue
3. **样式**：优先使用 Tailwind CSS，复杂局部样式使用 SCSS

### 功能要求：
1. **数据展示**：
   - 优先使用 `ArtTable` 展示数据列表
   - 使用 `ArtTableHeader` 提供刷新、列控制和左侧操作区
   - 使用 `useTable` 管理分页、loading、刷新和查询参数
   - 支持加载状态显示

2. **搜索功能**：
   - 搜索表单建议拆到 `src/views/[模块名称]/modules/[module-name]-search.vue`
   - 支持多条件筛选
   - 使用 ArtSearchBar 组件（如果适用）

3. **操作功能**：
   - 新增按钮（打开新增弹窗/抽屉）
   - 编辑按钮（打开编辑弹窗/抽屉）
   - 删除按钮（确认删除操作）
   - 其他自定义操作按钮

4. **状态管理**：
   - 使用 ref/reactive 管理组件状态
   - 包含 loading 状态
   - 列表数据的响应式管理

### 数据结构：
[在此描述数据字段和结构]

### 表格列配置：
[在此描述需要显示的表格列]

### 搜索条件：
[在此描述搜索条件字段]

### 操作按钮：
[在此描述需要的操作按钮]

请遵循项目现有的代码规范，包括：
- 使用 Composition API script setup 语法
- API 调用使用 `src/api/[module-name].ts` 中导出的 `fetch*` 函数
- 类型从 `src/api/model/[moduleName]Model.ts` 或 `Api.*` 命名空间导入
- API 路径必须由 API 层引用 `ApiPaths`，页面不要直接写接口 URL
- 列表 API 返回 `PaginatedResponse<T>`，字段为 `records`、`current`、`size`、`total`
- 页面不解析响应 envelope；`request` 已经返回后端 `data`
- 时间列使用 `@/utils/time` 的 `formatDateTime` / `formatDate` 展示
- 合理的组件拆分和复用
- 完善的错误处理和用户体验
- 统一的代码风格和命名规范
```

### 更新现有列表页面

```
请更新 [模块名称] 的列表页面，需要：

1. **新增功能**：
   [描述需要新增的功能]

2. **修改现有功能**：
   [描述需要修改的功能]

3. **优化建议**：
   [描述需要优化的地方]

请保持与现有代码风格一致。
```

## 代码结构模板

```vue
<script setup lang="ts">
import { h, ref } from 'vue'
import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
import { useTable } from '@/hooks/core/useTable'
import { fetchGet[ModuleName]List, fetchDelete[ModuleName] } from '@/api/[module-name]'
import type { [ModuleName]ListItem, [ModuleName]SearchParams } from '@/api/model/[moduleName]Model'
import { ElMessage, ElMessageBox } from 'element-plus'

defineOptions({ name: '[ModuleName]' })

const searchForm = ref<[ModuleName]SearchParams>({
  current: 1,
  size: 20
})

const {
  columns,
  columnChecks,
  data,
  loading,
  pagination,
  getData,
  searchParams,
  resetSearchParams,
  handleSizeChange,
  handleCurrentChange,
  refreshData
} = useTable({
  core: {
    apiFn: fetchGet[ModuleName]List,
    apiParams: searchForm.value,
    columnsFactory: () => [
      { type: 'index', width: 60, label: '序号' },
      // 按业务字段补充列配置
      {
        prop: 'operation',
        label: '操作',
        width: 120,
        fixed: 'right',
        formatter: (row: [ModuleName]ListItem) =>
          h('div', [
            h(ArtButtonTable, { type: 'edit', onClick: () => openDialog('edit', row) }),
            h(ArtButtonTable, { type: 'delete', onClick: () => handleDelete(row) })
          ])
      }
    ]
  }
})

const handleSearch = (params: [ModuleName]SearchParams) => {
  Object.assign(searchParams, params)
  getData()
}

const openDialog = (type: 'add' | 'edit', row?: [ModuleName]ListItem) => {
  // 打开新增/编辑弹窗
}

const handleDelete = async (row: [ModuleName]ListItem) => {
  await ElMessageBox.confirm('确认删除这条数据吗？', '提示', { type: 'warning' })
  await fetchDelete[ModuleName](row.id)
  ElMessage.success('删除成功')
  refreshData()
}

const handleReset = () => {
  resetSearchParams()
  getData()
}
</script>

<template>
  <div class="[module-name]-page art-full-height">
    <!-- 搜索组件：<[ModuleName]Search v-model="searchForm" @search="handleSearch" @reset="handleReset" /> -->

    <ElCard class="art-table-card" shadow="never">
      <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
        <template #left>
          <ElButton @click="openDialog('add')" v-ripple>新增[模块名称]</ElButton>
        </template>
      </ArtTableHeader>

      <ArtTable
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="pagination"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      />
    </ElCard>
  </div>
</template>

<style lang="scss" scoped>
/* 页面样式 */
</style>
```

## 最佳实践

1. **性能优化**：使用 v-loading 指令显示加载状态
2. **用户体验**：删除等危险操作前显示确认对话框
3. **错误处理**：合理的 try-catch 和错误提示
4. **代码复用**：抽取公共逻辑为 composables
5. **类型安全**：充分利用 TypeScript 类型检查
