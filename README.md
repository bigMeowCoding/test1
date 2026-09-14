# Tomcat 与 Servlet 学习实验室

这个仓库原有的 `src/*.java` 是 Java 基础练习；新增的 Maven 模块把它扩展为可运行的 Web 实验。它使用**内嵌 Tomcat 10**，不需要单独下载或配置 Tomcat。

## 两个核心概念

- **Servlet**：符合 Jakarta Servlet API 的 Java 类。它接收 HTTP 请求，通过 `HttpServletRequest` 读取数据，再用 `HttpServletResponse` 写出响应。
- **Tomcat**：Servlet 容器。它监听端口、把 URL 匹配到 Servlet、管理 Servlet 的创建/销毁，并在每个请求到来时调用 Servlet。

本项目中的调用链是：

`浏览器 → Tomcat(8081 端口与路由) → Servlet → HttpServletRequest/Response → 浏览器`

## 运行

要求：JDK 17 与 Maven。

```bash
mvn compile exec:java
```

打开 <http://localhost:8081/>。停止程序时按 `Ctrl+C`，观察控制台的 `destroy` 日志。

## 实验路线

1. 访问 `/lifecycle` 多次：`init()` 只在首次创建时调用一次，`doGet()` 每次请求都会调用。
2. 访问 `/request?name=小明`：观察 URL、方法、请求参数和请求头如何被 Servlet 读取。
3. 访问 `/calculate?a=10&b=4&operator=%2F`：用 HTTP 参数驱动原先计算器的四则运算逻辑。
4. 关闭程序：观察 `destroy()`，它是释放资源的入口。

## 关键文件

- `EmbeddedTomcatApp`：启动并配置 Tomcat，把 URL 映射到 Servlet。
- `LifecycleServlet`：演示 `init → service/doGet → destroy` 生命周期。
- `RequestInfoServlet`：演示请求对象。
- `CalculatorServlet`：演示参数校验、响应状态和 HTML 输出。

> Tomcat 10 使用 `jakarta.servlet.*` 包名；旧教程中的 `javax.servlet.*` 对应 Tomcat 9 及更早版本，二者不能直接混用。
