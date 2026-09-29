# Bookstore 2.0：订单与库存预占

## 1. 概述

### 1.1 背景

当前项目已具备书籍目录的 CRUD、分页查询以及 Web DTO、领域模型、持久化对象的边界。书籍本身的业务规则较少，难以展示领域驱动设计在状态变化、跨对象一致性和失败处理上的价值。

2.0 将项目扩展为一个最小可用的购书流程：用户选择书籍下单，系统预占库存；用户可模拟支付或取消订单。订单、库存和商品目录各自承担不同规则。

### 1.2 目标

- 演示聚合、聚合根、值对象、限界上下文、领域事件和应用层编排的实际用途。
- 保持现有书籍 API 的兼容性，并在其基础上增加订单能力。
- 防止超卖、重复支付、非法状态迁移和取消后库存未释放。
- 将业务规则放在领域/应用层，而不是 Controller、Mapper 或 SQL 拼接中。

### 1.3 非目标

- 不实现真实用户账户、登录、权限、第三方支付、优惠券、物流或退款。
- 不引入消息队列、分布式事务、缓存、搜索引擎或 JPA。
- 不实现购物车长期保存；创建订单时由请求直接提交订单行。
- 不修改既有 `GET/POST/PUT/DELETE /api/books` 的契约。

## 2. 用户与核心场景

本项目的产品用户是需要完成购书的终端用户；教学用户是希望观察 DDD 业务边界的开发者。

| 场景 | 用户目标 | 系统结果 |
|---|---|---|
| 创建订单 | 锁定希望购买的书和数量 | 创建待支付订单，并预占对应库存 |
| 模拟支付 | 确认购买 | 订单变为已支付，预占库存转为已售出 |
| 取消订单 | 放弃购买 | 订单变为已取消，预占库存回到可售库存 |
| 查询订单 | 知道订单状态和金额 | 返回订单、订单行和价格快照 |
| 库存不足 | 避免无法履约的订单 | 拒绝创建订单，不产生部分预占 |

## 3. 领域划分

```text
Catalog（商品目录）
  Book：名称、作者、当前标价
         │ 提供下单时的商品与价格
         ▼
Order（订单）
  Order 聚合：订单状态、订单行、总金额
         │ 申请预占 / 支付确认 / 取消释放
         ▼
Inventory（库存）
  Inventory：可售数量、已预占数量、已售数量
```

### 3.1 Catalog 上下文

沿用现有 `Book`。它负责“卖什么”和当前标价，不负责订单状态或库存扣减。

### 3.2 Order 上下文

`Order` 是聚合根。它拥有订单行，保证订单状态和总金额一致。

建议模型：

- `Order`：`id`、`status`、`lines`、`totalAmount`、`createdAt`、`paidAt`、`cancelledAt`。
- `OrderLine`：`bookId`、`bookTitle`、`unitPrice`、`quantity`、`lineAmount`。
- `OrderStatus`：`PENDING_PAYMENT`、`PAID`、`CANCELLED`。
- `Money`：金额值对象，统一金额非负、两位小数和货币计算规则。

订单行必须保存书名与单价快照。后续图书改名或调价，不能篡改已创建订单的历史事实。

### 3.3 Inventory 上下文

`Inventory` 以书籍 ID 为边界，维护库存状态。

- `availableQuantity`：当前可被新订单预占的数量。
- `reservedQuantity`：待支付订单已锁定的数量。
- `soldQuantity`：已支付订单确认卖出的累计数量。

库存对象必须只通过自身行为改变数量，禁止在 Service 中直接加减字段。

## 4. 领域规则与不变量

| ID | 规则 |
|---|---|
| BR-01 | 一个订单至少包含一条订单行。 |
| BR-02 | 每条订单行的 `bookId` 必须存在，数量必须大于 0。 |
| BR-03 | 创建订单时，按当前图书名称和标价写入订单行快照。 |
| BR-04 | 所有订单行都成功预占库存后，才能创建订单；任意一项失败时不得留下部分预占。 |
| BR-05 | 可售库存不足时，拒绝订单创建并说明库存不足的书籍。 |
| BR-06 | 只有 `PENDING_PAYMENT` 订单可以支付。支付后状态为 `PAID`，预占库存转入已售库存。 |
| BR-07 | 只有 `PENDING_PAYMENT` 订单可以取消。取消后状态为 `CANCELLED`，预占库存必须释放。 |
| BR-08 | `PAID` 与 `CANCELLED` 都是终态；重复支付、重复取消或终态间转换必须被拒绝。 |
| BR-09 | 金额不可为负；订单总额必须等于各订单行金额之和。 |
| BR-10 | 所有创建订单、支付和取消操作必须在同一数据库事务中完成。 |

## 5. 用例与状态流转

```text
创建订单：校验订单行 → 读取 Book 快照 → 预占所有库存 → 保存 PENDING_PAYMENT 订单

PENDING_PAYMENT --支付--> PAID
      │                       │
      └------取消------------> CANCELLED
```

### 5.1 创建订单

输入为一个或多个 `bookId + quantity`。

1. 校验订单行不为空、书籍 ID 有效、数量大于零，合并重复书籍行。
2. 批量读取书籍；任何书籍不存在即失败。
3. 从每本书的当前价格创建 `OrderLine` 快照，生成 `Order`。
4. 对每个书籍库存执行预占。
5. 所有预占成功后保存订单；任一步失败回滚事务。

### 5.2 支付订单

1. 读取订单并调用 `order.pay()`。
2. 对每一订单行调用库存确认出售行为。
3. 保存订单与库存变化。

支付目前是本地模拟动作，不代表真实资金已收取。

### 5.3 取消订单

1. 读取订单并调用 `order.cancel()`。
2. 对每一订单行释放对应预占库存。
3. 保存订单与库存变化。

## 6. 接口设计

### 6.1 创建订单

`POST /api/orders`

请求：

```json
{
  "items": [
    {"bookId": 1, "quantity": 2},
    {"bookId": 3, "quantity": 1}
  ]
}
```

成功响应：`201 Created`

```json
{
  "id": 101,
  "status": "PENDING_PAYMENT",
  "items": [
    {"bookId": 1, "bookTitle": "Java 核心技术 卷 I", "unitPrice": 118.00, "quantity": 2, "lineAmount": 236.00},
    {"bookId": 3, "bookTitle": "代码整洁之道", "unitPrice": 69.00, "quantity": 1, "lineAmount": 69.00}
  ],
  "totalAmount": 305.00,
  "createdAt": "2026-09-29T12:00:00Z"
}
```

### 6.2 查询订单

`GET /api/orders/{id}`，返回与创建订单相同的订单视图，附带 `paidAt` 或 `cancelledAt`（若存在）。

### 6.3 支付订单

`POST /api/orders/{id}/pay`

成功时返回 `200 OK` 和状态为 `PAID` 的订单视图。

### 6.4 取消订单

`POST /api/orders/{id}/cancel`

成功时返回 `200 OK` 和状态为 `CANCELLED` 的订单视图。

### 6.5 错误语义

| 情况 | HTTP 状态 | 示例错误 |
|---|---|---|
| 请求体格式/字段无效 | 400 | `订单至少需要一项商品` |
| 图书或订单不存在 | 404 | `订单不存在` |
| 库存不足或状态不允许 | 409 | `《Java 入门》库存不足`、`订单已支付，不能取消` |
| 意外持久化失败 | 500 | 保留原因供日志诊断，不暴露底层数据库信息 |

现有 API 的错误结构应保持一致；若当前错误处理尚不能区分 404/409，在实现前需要先确认统一 API 错误契约。

## 7. 代码结构与依赖

在现有分层基础上，按业务模块聚合新增代码：

```text
com.example.bookstore
├── domain
│   ├── book
│   ├── inventory
│   └── order
├── application
│   ├── port
│   ├── inventory
│   └── order
├── infrastructure
│   └── persistence
│       ├── inventory
│       └── order
└── web
    ├── inventory
    └── order
```

依赖必须保持：

```text
web ───────────────→ application ───────────────→ domain
                         ↑
infrastructure ──────────┘
```

- `domain` 不依赖 Spring、MyBatis、HTTP、数据库注解或 Mapper。
- application Service 通过 `OrderStore`、`InventoryStore`、`BookStore` 等端口工作，不直接依赖 MyBatis Mapper。
- infrastructure Adapter 实现端口，并在该层完成领域对象与持久化对象转换。
- Web DTO 仅在 Controller 边界转换，不能传入领域模型作为 HTTP 请求体。

## 8. 持久化设计

### 8.1 新增表

```text
inventories
  book_id (PK, FK books.id)
  available_quantity
  reserved_quantity
  sold_quantity
  version

orders
  id (PK)
  status
  total_amount
  created_at
  paid_at (nullable)
  cancelled_at (nullable)

order_lines
  id (PK)
  order_id (FK orders.id)
  book_id
  book_title
  unit_price
  quantity
  line_amount
```

### 8.2 并发策略

库存预占必须避免并发超卖。2.0 采用 MySQL 条件更新作为最小方案：

```sql
UPDATE inventories
SET available_quantity = available_quantity - #{quantity},
    reserved_quantity = reserved_quantity + #{quantity},
    version = version + 1
WHERE book_id = #{bookId}
  AND available_quantity >= #{quantity};
```

受影响行数为 0 即代表库存不足或书籍无库存记录；应用层必须回滚整个创建订单事务。`version` 为下一阶段采用乐观锁或诊断并发冲突预留。

## 9. 领域事件

初版用进程内、同步的领域事件表达业务事实，不引入消息队列：

| 事件 | 发生时机 | 初期处理 |
|---|---|---|
| `OrderCreated` | 订单及库存预占成功后 | 审计日志或教学观察点；可先不持久化。 |
| `OrderPaid` | 订单支付成功后 | 确认预占库存转为已售。 |
| `OrderCancelled` | 待支付订单取消后 | 释放预占库存。 |

事件不能替代事务。订单与库存的强一致状态变化仍须在同一事务内完成；未来若接入通知、积分或发货，可再将事件演进为可靠消息。

## 10. 交付范围与阶段

### Phase A：订单聚合

- `Order`、`OrderLine`、`Money`、`OrderStatus`。
- 创建、查询订单；生成不可变价格快照。
- 单元测试覆盖订单金额和非法状态迁移。

### Phase B：库存预占

- `Inventory` 领域行为和持久化实现。
- 创建订单时预占库存；取消时释放；支付时确认出售。
- 覆盖库存不足、重复操作、事务回滚和并发条件更新。

### Phase C：事件与查询优化（可选）

- 以同步领域事件组织支付/取消后的库存操作。
- 增加订单列表、库存查询和审计日志。
- 仅在有明确需求时引入异步消息或独立读模型。

## 11. 验收标准

- [ ] 现有书籍 CRUD 和分页测试保持通过。
- [ ] 用户能用一个或多个有效订单行创建 `PENDING_PAYMENT` 订单，响应包含正确书籍和价格快照。
- [ ] 创建订单后，对应库存的可售数量减少、预占数量增加。
- [ ] 库存不足、图书不存在或非法数量时，整个订单创建失败且所有库存保持原值。
- [ ] 支付待支付订单后，订单变为 `PAID`，预占数量减少、已售数量增加。
- [ ] 取消待支付订单后，订单变为 `CANCELLED`，预占数量减少、可售数量恢复。
- [ ] 支付/取消终态订单得到明确冲突错误，库存不再变化。
- [ ] 并发库存预占测试证明可售库存不会小于 0。
- [ ] Order、Inventory 的业务规则不依赖 Spring、MyBatis 或 HTTP；Service 不直接调用 Mapper。
- [ ] README 补充 2.0 的上下文关系、状态流转和运行方式。

## 12. 风险与待确认决策

| 决策 | 建议 | 原因 |
|---|---|---|
| 订单归属 | 2.0 不引入用户，订单匿名创建 | 避免认证/授权扩大范围，先聚焦订单和库存规则。 |
| 支付超时 | 2.0 不自动超时取消 | 需要定时任务与幂等设计，适合后续专题。 |
| 价格精度 | 固定人民币、`BigDecimal` 两位小数 | 足以学习金额快照，避免多币种复杂度。 |
| 重复书籍行 | 创建时合并为同一订单行 | 避免同一书籍被多次预占、展示和金额计算不一致。 |
| 初始库存 | 建库时为每本演示书建立库存 | 保持开发环境可立即验证。 |
| 订单错误状态码 | 引入 404 与 409 | 更准确表达“资源不存在”和“状态/库存冲突”；会影响现有统一异常处理，实施前需确认。 |

## 13. 成功判定

2.0 成功不以类或目录数量衡量，而以开发者能从代码和测试回答下列问题为准：

1. 为什么订单行要保存价格快照，而不能在查询时回读 `Book.price`？
2. 为什么 Controller 不能直接扣减库存？
3. 为什么支付和取消只能从 `PENDING_PAYMENT` 发起？
4. 为什么创建订单与库存预占需要同一事务？
5. 如果将 MyBatis 替换成其他存储方案，哪些业务代码不应改变？
