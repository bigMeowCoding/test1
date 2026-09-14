package com.example.servletlab;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/** 用日志与页面计数直观展示 Servlet 生命周期。 */
public final class LifecycleServlet extends HttpServlet {
    private final AtomicInteger requestCount = new AtomicInteger();

    @Override
    public void init() throws ServletException {
        System.out.println("[LifecycleServlet] init()：Tomcat 创建 Servlet 实例，只调用一次。");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int count = requestCount.incrementAndGet();
        System.out.printf("[LifecycleServlet] doGet()：第 %d 次请求%n", count);
        HtmlResponse.write(response, "Servlet 生命周期", """
                <p>当前请求次数：<strong>%d</strong></p>
                <p>本次处理时间：%s</p>
                <ol><li><code>init()</code>：Tomcat 创建该 Servlet 时调用一次。</li>
                <li><code>service()</code>：每次请求都会进入；<code>HttpServlet</code> 再按方法分派到 <code>doGet()</code>。</li>
                <li><code>destroy()</code>：Tomcat 关闭或卸载应用时调用一次。</li></ol>
                <p>刷新本页并观察控制台：只有 <code>doGet()</code> 重复输出。</p>
                """.formatted(count, Instant.now()));
    }

    @Override
    public void destroy() {
        System.out.println("[LifecycleServlet] destroy()：Tomcat 正在关闭，可在这里释放资源。");
    }
}
