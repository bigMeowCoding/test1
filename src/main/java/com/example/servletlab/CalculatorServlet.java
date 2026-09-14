package com.example.servletlab;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** 将控制台计算器改成 HTTP 接口，示范请求参数、状态码与错误响应。 */
public final class CalculatorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            double a = Double.parseDouble(requireParameter(request, "a"));
            double b = Double.parseDouble(requireParameter(request, "b"));
            String operator = requireParameter(request, "operator");
            double result = calculate(a, b, operator);
            HtmlResponse.write(response, "Servlet 计算器", "<p><strong>结果：%s %s %s = %s</strong></p>"
                    .formatted(a, HtmlResponse.escape(operator), b, result));
        } catch (IllegalArgumentException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            HtmlResponse.write(response, "请求参数有误", "<p>%s</p><p>示例：<a href=\"/calculate?a=10&b=4&operator=%%2B\">10 + 4</a></p>"
                    .formatted(HtmlResponse.escape(exception.getMessage())));
        }
    }

    private String requireParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("缺少请求参数：" + name);
        }
        return value;
    }

    private double calculate(double a, double b, String operator) {
        return switch (operator) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> {
                if (b == 0) {
                    throw new IllegalArgumentException("除数不能为 0");
                }
                yield a / b;
            }
            default -> throw new IllegalArgumentException("不支持的 operator：" + operator);
        };
    }
}
