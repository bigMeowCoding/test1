package com.example.servletlab;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** 统一输出学习示例页面，并避免直接回显请求参数造成 HTML 注入。 */
final class HtmlResponse {
    private HtmlResponse() {
    }

    static void write(HttpServletResponse response, String title, String body) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().printf("""
                <!doctype html><html lang="zh-CN"><head><meta charset="UTF-8"><title>%s</title>
                <style>body{font-family:system-ui,sans-serif;max-width:760px;margin:40px auto;line-height:1.65}nav a{margin-right:16px}table{border-collapse:collapse}th,td{border:1px solid #bbb;padding:6px;text-align:left}code{background:#f2f2f2;padding:2px 4px}</style>
                </head><body><nav><a href="/">首页</a><a href="/lifecycle">生命周期</a><a href="/request">请求</a><a href="/calculate?a=10&b=4&operator=%%2B">计算器</a></nav><hr><h1>%s</h1>%s</body></html>
                """, escape(title), escape(title), body);
    }

    static String escape(String value) {
        if (value == null) {
            return "（无）";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}
