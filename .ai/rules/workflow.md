# 工作流 V2

## 对象关系

~~~text
PRD 1:N Spec
Spec 1:N Task
Task 1:1 Verify
Spec 1:1 Review
Review 1:N CR Batch
~~~

Task 直接归属一个 Spec，并记录所依据的 Spec 版本。一个 Task 始终复用同一个 **verify.md**，一个 Spec 始终复用同一个 **review.md**；不再设置独立 Plan 阶段或 **plan.md**。

## Task 类型

**FEATURE | BUGFIX | CHANGE | INTERRUPT | INVESTIGATION**

- FEATURE：记录来源 Requirement 与预期业务能力。
- BUGFIX：记录预期结果、实际结果、复现步骤和证据。
- CHANGE：记录变更来源、是否修改 Spec 及关联 Spec 版本。
- INTERRUPT：记录插入原因、优先级依据和被打断 Task；被打断 Task 转为 PAUSED。
- INVESTIGATION：记录待回答问题、调查边界和预期决策输出。

## 生命周期

Spec 状态只能是：**DRAFT | CONFIRMED | CHANGED | DONE | CANCELLED**。

Task 标准流转：

~~~text
READY -> IN_PROGRESS -> IMPLEMENTED -> VERIFYING -> DONE
                            |             |
                            +-- FAIL --> NEEDS_FIX -> IN_PROGRESS
IN_PROGRESS -> PAUSED -> IN_PROGRESS
READY or IN_PROGRESS -> BLOCKED -> READY or IN_PROGRESS
~~~

Task 状态只能是：**READY | IN_PROGRESS | PAUSED | BLOCKED | IMPLEMENTED | VERIFYING | NEEDS_FIX | DONE | CANCELLED**。DONE 表示实现完成且最新 Verify 为 PASS；Task 不经过默认 REVIEWING。每次状态变化追加到执行记录。

## Task 执行与 Verify

Spec 确认后即可直接拆分并执行 Task，无需单独的计划审批。Task 是所有开发工作的统一执行入口。每个产生代码变更的 Task 都必须 Verify：先将 Task 置为 VERIFYING，在同层首次创建或追加 **verify.md**，逐项引用验收 ID 并记录自动化与必要的手工证据。无证据不得 PASS；“没有搜索到”不能证明行为不存在或验证通过。

Verify 状态只能是 **NOT_RUN | PASS | FAIL**。FAIL 时记录 Finding 并把原 Task 置为 NEEDS_FIX；修复后在同一文件追加新轮次，禁止覆盖历史失败。PASS 时 Task 进入 DONE。

## Spec CR

Code Review 默认绑定完整 Spec 或具有独立业务/技术意义的 CR Batch，不按固定 Task 数机械拆分。CR 前该批次覆盖 Task 的最新 Verify 必须全部 PASS，否则拒绝开始并列出未满足条件的 Task。全部强制 CR Batch PASS 后 Spec 才能进入 DONE。

Review 状态只能是 **NOT_STARTED | IN_PROGRESS | PASS | CHANGES_REQUESTED**，Finding 严重程度只能是 **BLOCKER | MAJOR | MINOR | SUGGESTION**。CR 修改代码后，相关 Task 的旧 Verify 失效并必须重新 Verify。

权限、安全、认证、核心计算、数据迁移、公共基础设施、跨端兼容、紧急 Hotfix、低置信度或自动化无法覆盖关键行为时，可以在 Task 目录临时创建 **review.md** 单独评审；这是风险驱动例外。

## 动态工作与 Finding

当前 Task 内遗漏、Verify 失败或可明确归属原 Task 的 Review Finding：记录 Finding → 原 Task NEEDS_FIX → 增加 Fix Item → 修复 → 重新 Verify，不默认创建一级 Task。

Spec CR 可以把已经 DONE 的原 Task 重新打开为 NEEDS_FIX；如果 Finding 超出原 Task 范围，则在同一 Spec 新建修复 Task。

超出原 Task 范围的独立缺陷在最相关 Spec 下新增 BUGFIX Task；小型且不改变验收基线的调整新增 CHANGE Task；紧急插入新增 INTERRUPT Task 并暂停被打断 Task；原因或方案不明确时新增 INVESTIGATION Task。

需求验收基线变化时，先提升 Spec 版本并记录变更，再分析 Task 影响，只调整受影响 Task。Task 依据旧 Spec 版本且影响未确认时，禁止继续执行。
