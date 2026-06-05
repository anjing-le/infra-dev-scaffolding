# Infra Dev Scaffolding Frontend

> Anjing 开源项目的 Vue 3 + TypeScript 中后台前端母版。

## ✨ 功能介绍

这是一个基于 Vue 3 + TypeScript + Vite 的现代化中后台管理系统模板，专为 Anjing 后续 Infra / Agent 项目复用而整理。项目内置权限、路由、状态管理、国际化、主题和 AI 协作资产。

### 核心特性

- 🎨 **现代化 UI** - 基于 Element Plus + Tailwind CSS 4，提供精美的界面设计
- 📦 **开箱即用** - 内置权限管理、路由配置、状态管理等核心功能
- 🔧 **完善的开发工具** - 集成 ESLint、Prettier、Stylelint 等代码质量工具
- 🌈 **丰富的组件库** - 提供 50+ 业务组件和核心组件
- 🔐 **权限系统** - 完整的 RBAC 权限控制方案
- 🌍 **国际化支持** - 内置 i18n 多语言解决方案
- 📊 **数据可视化** - 集成 ECharts 图表库
- 🎯 **TypeScript** - 完整的类型定义，提供更好的开发体验
- 🔥 **AI 友好** - 内置 Cursor Rules 和 Prompts，提升 AI 辅助开发效率

### 技术栈

- **框架**: Vue 3.5 + TypeScript 5.6
- **构建工具**: Vite 7
- **状态管理**: Pinia 3 + Pinia Plugin Persistedstate
- **路由**: Vue Router 4
- **UI 框架**: Element Plus 2.11 + Tailwind CSS 4
- **HTTP 客户端**: Axios
- **图表库**: ECharts 6
- **代码规范**: ESLint 9 + Prettier + Stylelint
- **提交规范**: Commitizen + Commitlint
- **包管理器**: pnpm 10

## 🎯 基于开源框架

本项目基于优秀的开源框架 [Art Design Pro](https://www.artd.pro/#/) 进行定制和优化。

Art Design Pro 是一个功能强大的 Vue 3 中后台管理系统解决方案，我们在此基础上：

- ✅ 优化了项目结构和代码组织
- ✅ 添加了完善的 TypeScript 类型定义
- ✅ 集成了 AI 辅助开发工具（Cursor Rules & Prompts）
- ✅ 完善了开发文档和最佳实践指南
- ✅ 优化了性能和用户体验

感谢 Art Design Pro 团队的开源贡献！🙏

## 🤖 Cursor AI 增强

项目内置了完善的 Cursor Rules 和 Prompts，帮助您更高效地使用 AI 辅助开发。

### 📁 目录结构

```
.cursor/
├── rules/           # Cursor 开发规范和最佳实践
│   ├── README.mdc                 # 规范总览
│   ├── vue-component.mdc          # Vue 组件开发规范
│   ├── typescript.mdc             # TypeScript 类型定义规范
│   ├── code-style.mdc             # 代码风格规范
│   ├── state-management.mdc       # Pinia 状态管理规范
│   ├── router.mdc                 # 路由配置规范
│   ├── api-development.mdc        # API 接口开发规范
│   ├── style-development.mdc      # 样式开发规范
│   ├── performance.mdc            # 性能优化指南
│   ├── best-practices.mdc         # 最佳实践
│   ├── troubleshooting.mdc        # 问题排查指南
│   └── git-commit.mdc             # Git 提交规范
│
└── prompts/         # 场景化的 AI 提示词模板
    ├── README.md                  # Prompts 使用说明
    ├── vue-list-page.md           # 列表页面生成
    ├── vue-modal-form.md          # 弹窗表单生成
    ├── vue-business-component.md  # 业务组件生成
    └── api-module.md              # API 模块生成
```

### 🎯 Rules 覆盖范围

- **Vue 组件开发**: Composition API、组件结构、Props/Emits、生命周期
- **TypeScript**: 类型定义、泛型使用、API 类型规范
- **代码风格**: 命名规范、格式化要求、注释规范
- **状态管理**: Pinia Store 定义、使用、持久化
- **路由配置**: 路由守卫、导航、动态路由
- **API 开发**: 接口定义、错误处理、类型安全
- **样式开发**: Tailwind CSS、SCSS、主题切换
- **性能优化**: 组件优化、打包优化、内存管理
- **最佳实践**: 项目配置、代码质量、安全性
- **问题排查**: 常见问题解决方案

### 💡 Prompts 使用场景

通过预定义的 Prompts 模板，快速生成标准化的代码：

- **列表页面**: 生成带有搜索、分页、操作的完整列表页
- **弹窗表单**: 生成新增/编辑的弹窗表单组件
- **业务组件**: 生成符合项目规范的业务组件
- **API 模块**: 生成类型安全的 API 接口模块

## 🚀 快速启动

### 环境要求

- **Node.js**: >= 20.19.0
- **pnpm**: >= 8.8.0

### 安装步骤

1. **克隆项目**

```bash
git clone <repository-url>
cd infra-dev-scaffolding/frontend
```

2. **安装依赖**

```bash
pnpm install
```

3. **启动开发服务器**

```bash
pnpm dev
```

项目将自动在浏览器中打开，默认地址：`http://localhost:13006`

### 其他命令

```bash
# 构建生产环境
pnpm build

# 预览构建结果
pnpm serve

# 代码检查
pnpm lint

# 代码格式化
pnpm fix

# 检查复制后旧模板残留（默认 dry-run）
pnpm clean:dev

# 确认后删除命中的旧残留
pnpm clean:dev -- --apply

# 提交代码（使用 Commitizen）
pnpm commit
```

## 📂 项目结构

```
frontend/
├── .cursor/              # Cursor AI 规范和提示词
├── public/               # 静态资源
├── scripts/              # 构建脚本
├── src/
│   ├── api/             # API 接口定义
│   ├── assets/          # 资源文件（图片、样式、SVG）
│   ├── components/      # 组件库
│   │   ├── business/   # 业务组件
│   │   └── core/       # 核心组件
│   ├── config/          # 配置文件
│   ├── directives/      # 自定义指令
│   ├── enums/           # 枚举定义
│   ├── hooks/           # 组合式函数
│   ├── locales/         # 国际化
│   ├── plugins/         # 插件
│   ├── router/          # 路由配置
│   ├── store/           # 状态管理
│   ├── types/           # TypeScript 类型定义
│   ├── utils/           # 工具函数
│   ├── views/           # 页面视图
│   ├── App.vue          # 根组件
│   └── main.ts          # 入口文件
├── .gitignore
├── package.json
├── tsconfig.json
├── vite.config.ts
└── README.md
```

## 📝 开发指南

### 代码规范

项目已集成完整的代码规范工具链：

- **ESLint**: JavaScript/TypeScript 代码检查
- **Prettier**: 代码格式化
- **Stylelint**: CSS/SCSS 样式检查
- **Commitlint**: Git 提交信息检查

建议安装以下 VSCode 插件：

- ESLint
- Prettier - Code formatter
- Stylelint
- Vue Language Features (Volar)

### Git 提交规范

项目使用 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

```bash
# 使用 Commitizen 交互式提交
pnpm commit

# 或手动编写符合规范的提交信息
git commit -m "feat: 添加用户管理页面"
```

提交类型：

- `feat`: 新功能
- `fix`: 修复 Bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `perf`: 性能优化
- `test`: 测试相关
- `chore`: 构建/工具链相关

### 使用 Cursor AI

1. 打开项目后，Cursor 会自动加载 `.cursor/rules/` 下的开发规范
2. 在需要生成代码时，可以参考 `.cursor/prompts/` 下的模板
3. 使用 `@` 符号在 Cursor 中引用特定的 rules 文件

示例：

```
@vue-component 帮我创建一个用户列表组件
@api-module 生成用户管理相关的 API 接口
```

## 📄 License

[MIT License](./LICENSE)

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

---

**Happy Coding! 🎉**
