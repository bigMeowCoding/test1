# Java Web 书城：前后端分离入门项目

这个仓库包含两个独立项目：Java 后端只提供 JSON API；Vite 前端只负责界面与用户交互。它们通过 HTTP 联调，适合学习现代 Java Web 的职责边界。

```text
frontend（浏览器） ── /api 代理 ──→ backend（Spring MVC） ──→ MySQL
                                        │
 web DTO → service（业务 / 领域模型）→ BookStore port → persistence adapter → MyBatis
```

## 项目结构

| 位置 | 作用 |
| --- | --- |
| `web` | JSON API 和 Request / Response DTO：解析 HTTP、固定对外 JSON 契约 |
| `service` | 校验价格/库存、分页、业务错误；只依赖 `BookStore` 端口 |
| `domain/book` | `Book` 领域模型：只表达业务状态 |
| `application/port` | `BookStore`：业务层定义的存储能力 |
| `infrastructure/persistence` | `BookRecord` 持久化对象与 `BookPersistenceAdapter` 转换边界 |
| `repository` | MyBatis Mapper 接口与 XML SQL；只处理 `BookRecord` |
| `frontend` | 独立 Vite + React 前端，包含书目与订单工作台 |

## `src/main/resources` 资源目录

`src/main/resources` 存放运行时需要、但不作为 Java 源码编译的资源。Maven 构建时会将其中内容打包到应用的 classpath，Spring Boot 与 MyBatis 会从这里加载配置、SQL 映射和初始化脚本。

| 位置 | 作用 |
| --- | --- |
| `application.yml` | 默认应用配置：服务端口、数据源、MyBatis Mapper 的加载位置与本地 Profile。 |
| `application-local.yml` | 仅供本地开发覆盖数据库账号等配置；已被 Git 忽略，不应提交凭据。 |
| `application.properties` | 兼容保留的 Spring Boot 属性配置，目前包含服务端口。若与 YAML 配置同一项重复，应避免值不一致。 |
| `mapper/BookMapper.xml` | MyBatis SQL 映射文件；由 `mybatis.mapper-locations: classpath:mapper/*.xml` 加载，并与 `BookMapper` 接口的方法绑定。 |
| `db/init.sql` | `DatabaseInitializer` 执行的建表和演示数据初始化脚本。 |
| `db.properties` | 本地数据库连接示例/旧配置，已被 Git 忽略；不要将真实账号或密码写入受版本控制的文件。 |

## API 契约

| 方法与路径 | 用途 |
| --- | --- |
| `GET /api/books?keyword=&page=1` | 搜索、分页列表 |
| `GET /api/books/{id}` | 查询单本书 |
| `POST /api/books` | 新增书籍（JSON 请求体） |
| `PUT /api/books/{id}` | 更新书籍（JSON 请求体） |
| `DELETE /api/books/{id}` | 删除书籍 |
| `POST /api/orders` | 创建待支付订单并预占库存 |
| `GET /api/orders/{id}` | 查询订单及其价格快照 |
| `POST /api/orders/{id}/pay` | 模拟支付，确认已预占库存为已售 |
| `POST /api/orders/{id}/cancel` | 取消待支付订单并释放库存 |

新增/更新的请求体：

```json
{"title":"Java 入门","author":"张三","price":"59.90","stock":"10"}
```

## Bookstore 2.0：订单与库存预占

订单创建时传入商品与数量，系统以当前书名、单价建立不可变快照，并在同一事务中预占库存。例如：

```json
{"items":[{"bookId":1,"quantity":2},{"bookId":3,"quantity":1}]}
```

库存分为可售、预占和已售三态。创建订单通过 MySQL 条件更新一次完成“库存足够”的判断和预占，因此并发请求不能将可售库存扣成负数。支付或取消只允许从 `PENDING_PAYMENT` 状态发起，分别将预占库存转为已售或释放回可售。

```text
POST /api/orders → PENDING_PAYMENT（可售 → 预占）
                         ├─ POST /pay    → PAID（预占 → 已售）
                         └─ POST /cancel → CANCELLED（预占 → 可售）
```

库存不足和非法状态迁移返回 `409`；图书或订单不存在返回 `404`；请求字段无效返回 `400`，错误 JSON 保持 `{ "message": "..." }` 结构。

## 启动联调

需要 JDK 17、Maven、Node.js 与正在运行的 MySQL。

1. 本地开发配置写在被 Git 忽略的 `src/main/resources/application-local.yml`。该项目默认启用 `local` Profile；首次克隆时，创建该文件并填入你的数据库账号：

```yaml
spring:
  datasource:
    username: root
    password: 你的 MySQL 密码
```

如地址不同，可在该文件覆盖 `url`；部署环境可使用 `BOOKSTORE_DB_*` 环境变量或 `SPRING_PROFILES_ACTIVE` 覆盖配置。
2. 终端 A 启动后端：

```bash
mvn test
mvn spring-boot:run
```

后端 API 地址：<http://localhost:8081/api/books>。启动时会自动创建 `bookstore` 数据库和 `books` 表。

3. 终端 B 启动前端：

```bash
cd frontend
npm install
npm run dev
```

打开 <http://localhost:5173>。`frontend/vite.config.js` 会将 `/api` 自动转发到 8081，因此开发期不需要 CORS 配置。

## 验证与学习路线

```bash
mvn test          # BookService 的业务单元测试
cd frontend && npm run build  # React 前端生产构建
```

1. 浏览器点“登记新书”，从 `frontend/src/main.js` 的 `fetch` 看请求如何进入 `BookController`。
2. 跟入 `BookService`，理解为什么校验、分页不能散落在 Controller。
3. 阅读 `BookMapper` 与 `mapper/BookMapper.xml`，掌握 Mapper 接口如何与 XML 的同名语句绑定。
4. 改一个前端字段，再依次更新 API Request / Response DTO、业务层、领域模型、持久化对象和 SQL，体验各层契约如何串起全栈。

## MyBatis 学习路径

书籍数据访问已由 MyBatis 实现，关键文件及职责如下：

- `BookMapper.java`：`@Mapper` 让 Spring Boot 注册 MyBatis 生成的接口实现；`@Param` 对应 XML 中的 `#{...}` 具名参数。
- `mapper/BookMapper.xml`：接口方法名与语句 `id` 一一对应。`#{...}` 由 PreparedStatement 绑定，不能用 `${...}` 代替用户输入。
- `BookRecordResultMap`：XML 通过 `<constructor>` 创建 `BookRecord`；`BookPersistenceAdapter` 再把它转换为领域 `Book`。数据库字段的变化不会直接泄漏到业务或 HTTP 层。
- `application.yml`：`mybatis.mapper-locations` 指定 XML 文件位置；MyBatis 与启动初始化共同复用 Spring Boot 创建的 DataSource/连接池。
- `DatabaseInitializer`：它仍使用 JDBC，仅负责在 MyBatis 获取业务连接之前创建数据库、表和演示数据；业务 CRUD 不再手写 JDBC。

> 后端使用 Tomcat 10 与 `jakarta.servlet.*`，而不是旧版的 `javax.servlet.*`。
> 现在由 Spring Boot 自动配置并启动内嵌 Tomcat，HTTP 入口已经是 Spring MVC Controller。

## 模型分层学习路径

同一份“书籍”数据在三个边界使用不同对象：

```text
HTTP JSON ── BookRequest / BookResponse ──→ BookService ── Book ──→ BookStore
                                                                          │
MyBatis XML ───────────────────────────────────────────────────── BookRecord
```

- DTO 只承担 API 输入校验和输出契约；例如未来隐藏成本价时，只需调整 `BookResponse`。
- `Book` 是业务模型，不能出现 MyBatis 注解、SQL 字段别名或 HTTP 校验注解。
- `BookRecord` 只服务于 `books` 表和 MyBatis 参数/结果映射；适配器是唯一允许同时认识 `Book` 与 `BookRecord` 的位置。

### application、domain 与 infrastructure 的职责

这三个名字不是为了增加目录数量，而是为了回答三个不同的问题：

| 分层 | 要回答的问题 | 当前项目对应内容 |
| --- | --- | --- |
| `domain` | 业务是什么？有哪些不应受技术影响的业务状态和规则？ | `domain/book/Book`：书籍的业务状态。 |
| `application` | 系统如何完成一个用例？需要哪些外部能力？ | `service/BookService` 负责编排创建、查询和分页；`application/port/BookStore` 声明它需要的存储能力。 |
| `infrastructure` | 外部能力具体由什么技术实现？ | `infrastructure/persistence/BookPersistenceAdapter` 用 MyBatis 实现 `BookStore`；`BookRecord` 对应数据库字段。 |

`BookService` 当前为保持渐进改造而留在 `service/` 目录；从职责上说，它属于 application 层。以后业务模块增多时，可将它移动到 `application/book/`，但不需要为了目录名称而立即重构。

依赖方向应始终由具体技术指向核心业务：

```text
web ───────────────→ application ───────────────→ domain
                         ↑
infrastructure ──────────┘
```

也就是说，Web 和基础设施可以依赖 application / domain；`domain` 不应知道 Spring、HTTP、MyBatis 或 MySQL，application 也不应直接依赖具体 Mapper。这样替换数据库实现或调整接口时，核心业务受影响最小。

## 提交规范

- 一个 commit 只实现一个独立、可描述的功能或修复；不要将多个功能、重构、依赖升级或格式化混在同一次提交中。
- 提交前只暂存与该功能直接相关的文件，并确认暂存区内容可独立构建和验证。
- 如果工作区同时存在多个功能，请分别完成验证后再逐个提交；无法独立验证的共享改动应在提交说明中明确其关联功能。
