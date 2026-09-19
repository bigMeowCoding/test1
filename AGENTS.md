<!-- ai-dev:start -->
# AI 开发说明

本仓库使用 ai-dev Workflow V2。协议位于 **.ai/**，流程事实位于 **.ai/<需求名称>/<Spec 语义目录>/**。

开始或继续工作前：

1. 阅读 **.ai/rules/principles.md** 和 **.ai/rules/workflow.md**。
2. 按需读取 **.ai/rules/documents.md**、**.ai/rules/indexes.md** 和 **.ai/rules/migration.md**。
3. 检查 **.ai/<需求名称>/** 中相关 Spec、Task、Verify 与 Review 的当前事实。

对话历史不能作为开发事实源。详情文档是权威来源，**index.md** 只提供可重建的汇总视图。

## 规则加载

- 创建或更新流程文档：读取 **.ai/rules/documents.md**。
- 执行 Task、Verify 或 Spec CR：读取 **.ai/rules/workflow.md** 和 **.ai/config.md**。
- 重建索引或检查一致性：读取 **.ai/rules/indexes.md**。
- 发现旧 YAML、旧 **.ai/work/**、1.0.0 的 **.ai-dev/requirements/** 或 1.0.1 的 **.ai/requirements/**：读取 **.ai/rules/migration.md**，保留原文件并显式报告。

除非开发者明确要求升级协议，否则 **.ai/rules/** 和 **.ai/templates/** 为只读。Coding Agent 只在执行已确认工作时维护 **.ai/<需求名称>/** 下的工作文档。
<!-- ai-dev:end -->
