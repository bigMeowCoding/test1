package com.example.servletlab;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** 展示 Servlet 如何从 HttpServletRequest 中读取客户端发送的数据。 */
public final class RequestInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = HtmlResponse.escape(request.getParameter("name"));
        String greeting = name.isBlank() ? "未提供 name 参数" : "你好，" + name;
        String body = """
                <p>%s</p>
                <table><tr><th>字段</th><th>Servlet 读取结果</th></tr>
                <tr><td>HTTP 方法</td><td>%s</td></tr>
                <tr><td>请求 URI</td><td>%s</td></tr>
                <tr><td>查询字符串</td><td>%s</td></tr>
                <tr><td>User-Agent</td><td>%s</td></tr></table>
                <p>试试：<a href="/request?name=Tom">/request?name=Tom</a></p>
                """.formatted(greeting, request.getMethod(), request.getRequestURI(),
                HtmlResponse.escape(request.getQueryString()), HtmlResponse.escape(request.getHeader("User-Agent")));
        HtmlResponse.write(response, "读取 HTTP 请求", body);
    }
}
