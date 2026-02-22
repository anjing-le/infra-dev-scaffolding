# Cursor Prompts - 后端代码生成模板

## 使用方式

在 Cursor 中使用 `Cmd+Shift+P` 打开命令面板，选择 "Use Prompt Template"，然后选择对应的模板。

## 可用模板

### crud-module.md
生成完整的 CRUD 模块，包含：
- Controller（RESTful API）
- Service（业务逻辑）
- Repository（JPA 数据访问）
- Entity（JPA 实体）
- Request DTO
- Response VO
- ErrorCode 枚举

### api-endpoint.md  
生成单个 API 端点，适合在已有 Controller 中添加新接口。

## 使用说明

1. 替换模板中的 `[占位符]` 为实际值
2. 根据实际需求删减不需要的部分
3. 生成后检查导入和命名是否正确
