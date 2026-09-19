# 索引与一致性规则

## Task Index 重建

扫描当前 Spec 的 **tasks/Txxx-*/task.md**，读取固定基本信息表；存在时读取同层 **verify.md** 的最新结论。按“状态可执行性 → 优先级 → 依赖满足 → 创建顺序”排序，重建 **tasks/index.md** 的五个固定分区并保留相对链接。

重建只能改写索引，禁止修改 Task 或 Verify 详情。字段缺失、未知状态或重复 ID 时停止写入并给出带路径的错误。

## Requirement Index 重建

扫描当前需求目录的直接子目录，以其中存在的 **spec.md** 识别 Spec，不依赖目录名称或 ID 前缀；只同步需求 **index.md** 中的 Spec 汇总表。原始 PRD、负责人、整体风险和待确认问题是人工维护区，禁止覆盖或丢失。

## 一致性检查

创建、执行、Verify、Review、重建索引和迁移后，至少检查：

- Task 是否只属于一个 Spec、基于的 Spec 版本是否存在，引用的 Requirement 和依赖 Task 是否存在；
- Task 依赖是否存在明显依赖循环；
- Task 验收 ID 和所有对象 ID 是否重复；
- 所有状态和 Task 类型是否属于固定枚举；
- Verify 是否覆盖 Task 的全部验收 ID，最新结论是否与 Task 状态一致；
- Review 覆盖 Task 的最新 Verify 是否全部 PASS；
- 所有 ID + 相对路径链接是否指向相符对象。

发现错误时不得猜测、静默修复或写入伪造 PASS；列出对象 ID、路径、失败规则和建议的下一步。
