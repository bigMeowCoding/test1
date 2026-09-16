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
| `repository` | JDBC SQL 与 `BookRepository` 接口 |
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

1. 复制 `src/main/resources/db.properties.example` 为 `db.properties`，填写本机数据库账号。该文件被 Git 忽略。
2. 终端 A 启动后端：

```bash
mvn test
mvn compile exec:java
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
3. 阅读 `JdbcBookRepository`，掌握 `PreparedStatement`、`ResultSet`、try-with-resources。
4. 改一个前端字段，再依次更新 API 请求体、业务层、实体和 SQL，体验接口契约如何串起全栈。

> 后端使用 Tomcat 10 与 `jakarta.servlet.*`，而不是旧版的 `javax.servlet.*`。
