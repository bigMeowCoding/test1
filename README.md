# Java Web 书城：前后端分离入门项目

这个仓库包含两个独立项目：Java 后端只提供 JSON API；Vite 前端只负责界面与用户交互。它们通过 HTTP 联调，适合学习现代 Java Web 的职责边界。

```text
frontend（浏览器） ── /api 代理 ──→ backend（Servlet） ──→ MySQL
                                       │
                    web API → service（业务）→ repository（JDBC）
```

## 项目结构

| 位置 | 作用 |
| --- | --- |
| `src/main/java/com/example/bookstore/web` | JSON API：解析 HTTP、返回状态码和 JSON |
| `service` | 校验价格/库存、分页、业务错误 |
| `repository` | MyBatis Mapper 接口与 XML SQL |
| `model` | `Book` 实体 |
| `frontend` | 独立 Vite + 原生 JavaScript 前端 |

## API 契约

| 方法与路径 | 用途 |
| --- | --- |
| `GET /api/books?keyword=&page=1` | 搜索、分页列表 |
| `GET /api/books/{id}` | 查询单本书 |
| `POST /api/books` | 新增书籍（JSON 请求体） |
| `PUT /api/books/{id}` | 更新书籍（JSON 请求体） |
| `DELETE /api/books/{id}` | 删除书籍 |

新增/更新的请求体：

```json
{"title":"Java 入门","author":"张三","price":"59.90","stock":"10"}
```

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
cd frontend && npm run build  # 前端生产构建
```

1. 浏览器点“登记新书”，从 `frontend/src/main.js` 的 `fetch` 看请求如何进入 `BookApiServlet`。
2. 跟入 `BookService`，理解为什么校验、分页不能散落在 Servlet。
3. 阅读 `BookMapper` 与 `mapper/BookMapper.xml`，掌握 Mapper 接口如何与 XML 的同名语句绑定。
4. 改一个前端字段，再依次更新 API 请求体、业务层、实体和 SQL，体验接口契约如何串起全栈。

## MyBatis 学习路径

书籍数据访问已由 MyBatis 实现，关键文件及职责如下：

- `BookMapper.java`：`@Mapper` 让 Spring Boot 注册 MyBatis 生成的接口实现；`@Param` 对应 XML 中的 `#{...}` 具名参数。
- `mapper/BookMapper.xml`：接口方法名与语句 `id` 一一对应。`#{...}` 由 PreparedStatement 绑定，不能用 `${...}` 代替用户输入。
- `BookResultMap`：`Book` 是不可变对象，XML 通过 `<constructor>` 调用其构造器完成查询结果映射。
- `application.yml`：`mybatis.mapper-locations` 指定 XML 文件位置；MyBatis 与启动初始化共同复用 Spring Boot 创建的 DataSource/连接池。
- `DatabaseInitializer`：它仍使用 JDBC，仅负责在 MyBatis 获取业务连接之前创建数据库、表和演示数据；业务 CRUD 不再手写 JDBC。

> 后端使用 Tomcat 10 与 `jakarta.servlet.*`，而不是旧版的 `javax.servlet.*`。
> 现在由 Spring Boot 自动配置并启动内嵌 Tomcat；下一阶段再将现有 Servlet 迁移为 Spring MVC Controller。

## 提交规范

- 一个 commit 只实现一个独立、可描述的功能或修复；不要将多个功能、重构、依赖升级或格式化混在同一次提交中。
- 提交前只暂存与该功能直接相关的文件，并确认暂存区内容可独立构建和验证。
- 如果工作区同时存在多个功能，请分别完成验证后再逐个提交；无法独立验证的共享改动应在提交说明中明确其关联功能。
