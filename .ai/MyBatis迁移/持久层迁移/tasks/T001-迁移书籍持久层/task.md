# T001 迁移书籍持久层

## 基本信息

| 字段 | 值 |
|---|---|
| Task ID | T001 |
| 类型 | CHANGE |
| 状态 | DONE |
| 优先级 | P1 |
| 所属 Spec | [SPEC-001](../../spec.md) |
| 基于 Spec 版本 | 1.0 |
| 覆盖 Requirement | [REQ-001、REQ-002、REQ-003](../../spec.md#需求详细说明) |
| 依赖 Task | — |
| 创建时间 | 2026-09-19 17:57 |
| 最后更新时间 | 2026-09-19 18:01 |

## 任务目标

以 MyBatis Mapper 和 XML 替换书籍 CRUD 的手写 JDBC Repository，同时保留所有外部业务行为。

## 任务上下文

| 适用类型 | 必填信息 | 内容 |
|---|---|---|
| CHANGE | 变更来源、是否修改 Spec、关联 Spec 版本 | 用户提出学习型持久层迁移；新增 Spec 1.0；关联版本 1.0。 |

## 工作范围

### 包含内容

- 增加 MyBatis 依赖、Mapper 接口和 XML。
- 替换 BookService 和单元测试中的依赖接口。
- 移除 JdbcBookRepository。
- 更新 README 和本任务 Verify。

### 不包含内容

- 调整表结构、前端、HTTP API 或现有请求校验改动。
- 替换启动阶段的数据库创建逻辑。

## 技术方案与风险

Mapper 使用 `@Mapper` 自动注册；XML 使用具名参数、`resultMap` 构造不可变 `Book`。数据库创建仍在 Mapper 运行前由 DatabaseInitializer 使用 JDBC 执行。风险是 Mapper XML 仅在运行时才解析，因此增加不依赖本机 MySQL 的解析测试；完整 CRUD 由本地 MySQL 联调验证。

## 验收标准

- [x] T001-AC-01：`BookMapper` 和 Mapper XML 覆盖原有的列表、计数、单项查询、新增、更新和删除行为。
- [x] T001-AC-02：BookService 仅依赖 Mapper，旧 JdbcBookRepository 不再存在。
- [x] T001-AC-03：`mvn test` 通过，且 Mapper XML 解析测试验证全部语句 ID。
- [x] T001-AC-04：README 说明 MyBatis 的关键文件、参数绑定、结果映射与初始化边界。

## 实施清单

- [x] 增加 MyBatis starter 与配置。
- [x] 新增 Mapper 接口、XML 和 XML 解析测试。
- [x] 迁移 BookService、业务单元测试并删除 JDBC Repository。
- [x] 更新 README，运行测试及静态检查。
- [x] 更新 Maven artifactId，移除项目名称中的 JDBC 表述。

## Verify 要求

- 逐项引用全部验收 ID。
- 执行 Maven 测试、Mapper XML 解析测试和差异检查并记录实际证据。

## 执行记录

| 时间 | 状态变化 | 说明 |
|---|---|---|
| 2026-09-19 17:57 | — → IN_PROGRESS | Task 创建并开始迁移。 |
| 2026-09-19 18:01 | IN_PROGRESS → IMPLEMENTED | Mapper、XML、业务层替换与文档已完成。 |
| 2026-09-19 18:01 | IMPLEMENTED → VERIFYING | 执行 Maven 测试、XML 解析和差异检查。 |
| 2026-09-19 18:01 | VERIFYING → DONE | 最新 Verify 为 PASS，Spec Review 无发现。 |

## Findings

- 无。

## 完成总结

书籍 CRUD 已迁移为 MyBatis Mapper + XML；业务 API 和验证规则保持不变。
