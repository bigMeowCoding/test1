# Markdown 文档协议

## 格式与标识

Spec、Task、Verify、Review 和索引只使用 **.md**；禁止 YAML Front Matter，JSON 只能作为程序内部缓存。每份文档只有一个一级标题，固定模块使用二级标题，Requirement、Finding 和 CR Batch 使用三级或四级标题。

详情文档必须包含固定基本信息表。ID 创建后不可修改、复用或随文件夹改名变化：**SPEC-001**、**REQ-001**、**T001**、**T001-AC-01**、**VF-001**、**RF-001**、**CR-B01**。日期使用 **YYYY-MM-DD**，需要时间时使用 **YYYY-MM-DD HH:mm**。

## 目录与命名

需求目录直接位于 **.ai/<需求名称>/**。需求名称由 Agent 根据业务语义生成，必须简短、明确且在仓库内唯一；不得使用 **rules**、**templates**、**requirements**、**work** 或 **specs** 等保留名称，也不得覆盖已有目录。

每个 Spec 使用 **.ai/<需求名称>/<Spec 语义目录>/**。Spec 目录名由 Agent 根据实际业务语义和范围动态生成，同一需求内唯一，不强制包含 **SPEC-001** 等 ID 或机械前缀。目录名不作为 Spec ID；权威 ID 只记录在 **spec.md** 及其引用中。目录改名时必须同步更新全部相对链接并执行一致性检查。

## 关联与单一事实源

固定关系为 PRD 1:N Spec、Spec 1:N Task、Task 1:1 Verify、Spec 1:1 Review。

关系必须同时记录对象 ID 和相对路径链接。Task 关联所属 Spec、Spec 版本和 Requirement；Verify 关联 Task；Review 关联 Spec，并列出覆盖 Task 和代码范围。

| 信息 | 权威来源 |
|---|---|
| 需求行为、业务规则、验收基线 | **spec.md** |
| Task 状态、范围与执行记录 | **task.md** |
| Task 验证结果与证据 | **verify.md** |
| Spec CR 与 Review Finding | **review.md** |
| 汇总视图 | **index.md**，不得覆盖详情事实 |

## Spec 规则

Requirement 必须具有唯一 ID、原子化并可判定 **PASS | FAIL | UNVERIFIED | OPEN**。使用“必须、禁止、可以”等明确措辞，不使用“合理处理、适当提示、根据情况”等不可验证表述。改变验收基线时提升 Spec 版本并记录原因和受影响 Task。

## Task、Verify 与 Review 规则

Task 的工作范围同时写明包含与不包含内容；技术方案、依赖、风险和实施清单直接记录在对应 **task.md**，验收标准和实施清单不得混用。首次正式 Verify 才创建 **verify.md**，首次 Spec CR 才创建 **review.md**，不得预建空阶段文档。

Verify 每轮记录时间、代码版本、范围、逐项验收、自动化检查、手工验证和 Finding。Review 每个 CR Batch 记录覆盖 Task、代码范围、前置 Verify、检查项、Finding 和结论。
