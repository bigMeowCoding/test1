package com.example.servletlab;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.nio.file.Path;

/** 应用入口：由这里创建 Tomcat（Servlet 容器）并注册 URL 与 Servlet 的映射。 */
public final class EmbeddedTomcatApp {
    private static final int PORT = 8081;

    private EmbeddedTomcatApp() {
    }

    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(PORT);
        tomcat.getConnector(); // 触发默认 HTTP Connector 的创建。

        String webRoot = Path.of("src/main/webapp").toAbsolutePath().toString();
        Context context = tomcat.addContext("", webRoot);
        Tomcat.addServlet(context, "lifecycleServlet", new LifecycleServlet());
        context.addServletMappingDecoded("/lifecycle", "lifecycleServlet");
        Tomcat.addServlet(context, "requestInfoServlet", new RequestInfoServlet());
        context.addServletMappingDecoded("/request", "requestInfoServlet");
        Tomcat.addServlet(context, "calculatorServlet", new CalculatorServlet());
        context.addServletMappingDecoded("/calculate", "calculatorServlet");

        tomcat.start();
        System.out.printf("Tomcat 已启动：http://localhost:%d/%n", PORT);
        tomcat.getServer().await();
    }
}
