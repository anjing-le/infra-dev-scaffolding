# 关键知识点速查

---

## 1. 前端核心概念

### 1.1 Vue 3 Composition API
- `setup()` 语法糖 `<script setup>`
- `ref` / `reactive` 响应式数据
- `computed` 计算属性
- `watch` / `watchEffect` 监听器
- 生命周期钩子：`onMounted` / `onUnmounted`

### 1.2 组件化开发
- 单文件组件（SFC）：`<template>` + `<script setup>` + `<style scoped>`
- Props / Emits 父子通信
- `defineProps` / `defineEmits` / `defineExpose`
- 插槽（Slots）

### 1.3 路由（Vue Router 4）
- 静态路由 vs 动态路由
- 路由守卫：`beforeEach` / `afterEach`
- 路由元信息（meta）：`title`、`roles`、`icon`
- 权限过滤：根据用户角色动态注册路由

### 1.4 状态管理（Pinia 3）
- `defineStore` 定义 Store
- `state` / `getters` / `actions`
- 持久化：`pinia-plugin-persistedstate`
- 核心 Store：`useUserStore` / `useMenuStore` / `useSettingStore`

### 1.5 权限模型
```
用户登录 → 获取角色（R_SUPER/R_ADMIN/R_GUEST）
         → 过滤路由（对比 meta.roles）
         → 生成菜单
         → 页面元素级权限（v-auth 指令）
```

### 1.6 游客模式原理
```
点击「游客访问」→ 设置 guest_token + R_GUEST
               → 路由守卫跳过 API 调用
               → 菜单过滤掉需要 ADMIN 角色的页面
               → 正常渲染可访问页面
```

### 1.7 国际化
- `vue-i18n` 库
- 语言文件：`locales/langs/zh.json` / `en.json`
- 组件中使用：`{{ $t('key') }}` 或 `const { t } = useI18n()`

---

## 2. 后端核心概念

### 2.1 Spring Boot 分层架构
```
Controller（接收请求、参数校验、返回响应）
    ↓
Service（业务逻辑、事务管理、异常处理）
    ↓
Repository（数据访问、JPA 查询）
    ↓
Entity（数据表映射）
```

### 2.2 统一响应体 APIResponse
```json
{
  "code": "0",        // "0" 成功，其他为错误码
  "message": "操作成功",
  "data": { ... },    // 业务数据
  "timestamp": 1700000000000
}
```

使用方式：
```java
APIResponse.success(data);              // 成功
APIResponse.success(data, "自定义消息"); // 成功 + 自定义消息
APIResponse.error("操作失败");           // 失败
throw new BizException(ErrorCode);       // 抛异常（被全局处理器拦截）
```

### 2.3 全局异常处理
```
BizException（业务异常）→ GlobalExceptionHandler → APIResponse.error(code, msg)
SystemException（系统异常）→ GlobalExceptionHandler → APIResponse.error(code, msg)
ValidationException → GlobalExceptionHandler → APIResponse.error("3000", details)
其他 Exception → GlobalExceptionHandler → APIResponse.error("1000", "系统内部错误")
```

### 2.4 错误码体系
| 范围 | 类别 | 示例 |
|------|------|------|
| 0 | 成功 | SUCCESS |
| 1xxx | 系统错误 | SYSTEM_ERROR, DATABASE_ERROR |
| 2xxx | 业务错误 | DATA_NOT_FOUND, USER_DISABLED |
| 3xxx | 参数错误 | PARAMETER_ERROR, PARAM_MISSING |
| 4xxx | 权限错误 | UNAUTHORIZED, TOKEN_EXPIRED |

### 2.5 参数校验
```java
// 内置注解
@NotBlank     // 字符串非空
@NotNull      // 非 null
@Size(min, max) // 长度范围
@Min / @Max   // 数值范围
@Pattern      // 正则

// 自定义注解
@PhoneNumber  // 手机号
@EmailAddress // 邮箱

// 分组校验
@Facade(validationGroups = ValidationGroups.Register.class)
```

### 2.6 中间件开关机制
```yaml
# application.yml
app:
  features:
    redis:
      enabled: true     # 启用 → RedisConfig 加载
                         # 禁用 → 完全跳过，不会连接 Redis
```

原理：`@ConditionalOnProperty(prefix = "app.features.redis", name = "enabled", havingValue = "true")`

### 2.7 AOP 切面编程
- `ControllerLogAspect`：自动记录接口请求/响应日志
- `SqlLogAspect`：SQL 执行日志
- `DistributeLockAspect`：`@DistributeLock` 注解实现分布式锁
- `FacadeAspect`：`@Facade` 注解实现统一校验 + 日志

---

## 3. 工程化概念

### 3.1 环境变量
- 前端：`.env` / `.env.development` / `.env.production`
- 后端：`application.yml` + `${ENV_VAR:default_value}` 语法
- 敏感信息不入库，使用环境变量注入

### 3.2 Git 规范
```
feat(scope): 新功能
fix(scope): 修复
docs(scope): 文档
refactor(scope): 重构
```

### 3.3 代码规范工具链
- 前端：ESLint（JS/TS）+ Prettier（格式化）+ Stylelint（CSS）
- 后端：遵循 Java 编码规范 + Lombok 减少样板代码
- 提交：Commitizen（`pnpm commit`）

---

## 4. Cursor AI 概念

### 4.1 Cursor Rules
- **位置**：`.cursor/rules/*.mdc`
- **作用**：让 AI 理解项目编码规范
- **生效方式**：`alwaysApply: true`（全局生效）或 `globs`（匹配特定文件）
- **核心价值**：AI 生成的代码自动符合项目规范

### 4.2 Cursor Prompts
- **位置**：`.cursor/prompts/*.md`
- **作用**：预定义的代码生成模板
- **使用**：`Cmd+Shift+P` → "Use Prompt Template"
- **核心价值**：一键生成标准化的代码骨架

### 4.3 AI 协作最佳实践
1. Rules 先行：在写第一行代码前就配好 Rules
2. Prompts 驱动：重复性代码用模板生成
3. 增量开发：让 AI 参考已有代码，保持风格一致
4. 人机分工：AI 负责骨架 + 重复逻辑，人负责架构 + 业务决策
