# SPEC-001 持久层 MyBatis 化

## 基本信息

| 字段 | 值 |
|---|---|
| Spec ID | SPEC-001 |
| 状态 | DONE |
| 当前版本 | 1.0 |
| 来源 PRD | [需求索引](../index.md) |
| Task Index | [Task Index](./tasks/index.md) |
| Review | [Code Review](./review.md) |
| 创建时间 | 2026-09-19 17:57 |
| 最后更新时间 | 2026-09-19 18:01 |

## 背景

当前项目使用手写 JDBC Repository。用户希望通过同一业务场景学习 MyBatis 的 Mapper 接口、XML SQL、参数绑定和不可变对象结果映射。

## 目标

在不改变书籍 API、业务校验和数据库表结构的前提下，以 MyBatis Mapper 替换书籍 CRUD 的手写 JDBC 实现。

## 范围

- 新增 MyBatis Spring Boot Starter、Mapper 接口与 Mapper XML。
- 将 BookService 改为依赖 Mapper。
- 移除旧 JdbcBookRepository。
- 增加 Mapper XML 解析测试和学习文档。

## 角色与术语

- Mapper：由 MyBatis 生成实现的 Java 接口，方法与 XML SQL 语句按 ID 绑定。

## 需求清单

| Requirement ID | 名称 | 判定状态 |
|---|---|---|
| REQ-001 | MyBatis CRUD Mapper | PASS |
| REQ-002 | 保持既有业务契约 | PASS |
| REQ-003 | 提供可学习的配置说明 | PASS |

## 需求详细说明

### REQ-001

- 名称：MyBatis CRUD Mapper。
- 规则：书籍的查询、统计、新增、更新和删除必须由一个 `@Mapper` 接口及其 XML 映射执行；查询参数必须使用具名绑定。
- 验收：Mapper XML 可被 MyBatis 解析，并包含全部 CRUD 语句。

### REQ-002

- 名称：保持既有业务契约。
- 规则：`BookService` 的公开方法和其对 Mapper 的调用语义必须保持原有业务行为；SQL 失败仍转换为 `BusinessException`。
- 验收：现有 BookService 单元测试通过。

### REQ-003

- 名称：提供可学习的配置说明。
- 规则：README 必须说明 Mapper、XML、Spring Boot 配置和启动初始化的职责边界。
- 验收：README 可定位到上述四项说明。

## 验收标准

- [x] REQ-001：MyBatis 能识别完整书籍 Mapper XML。
- [x] REQ-002：既有业务层测试通过。
- [x] REQ-003：README 已包含 MyBatis 学习路径。

## 非功能要求

- SQL 参数必须使用 `#{...}` 绑定，不通过字符串拼接注入用户输入。
- `Book` 保持不可变对象。

## 不在范围内

- 更换 MySQL 或调整 `books` 表字段。
- 引入 JPA、分页插件、二级缓存或数据库迁移框架。
- 修改既有请求校验功能。

## 待确认问题

- 无。

## 变更记录

| 版本 | 日期 | 内容 | 原因 | 影响 Task |
|---|---|---|---|---|
| 1.0 | 2026-09-19 | 初始版本 | 用户确认迁移到 MyBatis | [T001](./tasks/T001-迁移书籍持久层/task.md) |

## Task 与 Review

- [Task Index](./tasks/index.md)
- [Code Review](./review.md)
