# Project Document

本目录存放 `infra-dev-scaffolding` 的定位、规划、验收标准和进度记录。

## 文档

- [ROADMAP.md](./ROADMAP.md)：项目定位、阶段规划、边界和成功标准。
- [../contracts/platform-contract.json](../contracts/platform-contract.json)：机器可读平台契约 manifest。
- [STATUS.md](./STATUS.md)：当前阶段状态、验证证据和下一步复用方向。
- [RELEASE_CHECKLIST.md](./RELEASE_CHECKLIST.md)：每轮发布/录制/复制前的检查清单。
- [COPY_GUIDE.md](./COPY_GUIDE.md)：复制为新项目时的改名和验证步骤。
- [TEMPLATE_BOUNDARIES.md](./TEMPLATE_BOUNDARIES.md)：母版能力、示例能力和旧残留边界。
- [PLATFORM_CONTRACT_GUIDE.md](./PLATFORM_CONTRACT_GUIDE.md)：机器可读平台契约 manifest 的范围和维护方式。
- [OPENAPI_CONTRACT_GUIDE.md](./OPENAPI_CONTRACT_GUIDE.md)：OpenAPI 运行接口契约、生产开关和前端 schema 类型生成链路。
- [SERVICE_BOUNDARY_GUIDE.md](./SERVICE_BOUNDARY_GUIDE.md)：服务/模块边界、API 归属和未来微服务拆分方式。
- [SHARED_KERNEL_GUIDE.md](./SHARED_KERNEL_GUIDE.md)：未来共享包可抽取类和运行时依赖边界。
- [AI_ASSETS.md](./AI_ASSETS.md)：Cursor Rules / Prompts 的资产清单和维护规则。
- [../docs/teaching/04-notice-module-demo.md](../docs/teaching/04-notice-module-demo.md)：用 Prompts 生成公告管理模块的完整演示流程。

## 维护原则

- 所有规划要服务于“开源工程母版”这个目标。
- 文档里的启动命令、端口、验证命令必须和当前代码一致。
- 新增功能如果不能被后续 Agent / Infra 项目复用，应放到示例或教学资料中，而不是污染底座。
