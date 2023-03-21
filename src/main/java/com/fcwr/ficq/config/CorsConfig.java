package com.fcwr.ficq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").
                        allowedOrigins("*"). //允许跨域的域名，可以用*表示允许任何域名使用
                        allowedMethods("*"). //允许任何方法（post、get等）
                        allowedHeaders("*"). //允许任何请求头
                        allowCredentials(true). //带上cookie信息
                        exposedHeaders(HttpHeaders.SET_COOKIE).maxAge(3600L); //maxAge(3600)表明在3600秒内，不需要再发送预检验请求，可以缓存该结果
            }
            /*
            这段代码是一个 Java Spring 框架中用于配置跨域资源共享（CORS）的方法。具体解释如下：
            public void addCorsMappings(CorsRegistry registry)：这个方法用于添加 CORS 映射，即确定哪些 URL 路径可以允许跨域请求。
            registry.addMapping("/**")：将所有的 URL 路径都设置为允许跨域请求。
            allowedOrigins("*")：允许任何来源的请求，也可以指定具体的域名或 IP。
            allowedMethods("*")：允许任何 HTTP 方法，例如 GET、POST、PUT 等。
            allowedHeaders("*")：允许请求中携带任何请求头。
            allowCredentials(true)：允许发送 Cookie 和其他凭证信息。
            exposedHeaders(HttpHeaders.SET_COOKIE)：在响应头中暴露 SET_COOKIE 字段，允许客户端读取和使用该字段的值。
            maxAge(3600L)：设置缓存时间为 3600 秒，表示在这段时间内对同一资源的请求不需要再次发送预检请求。
            */
        };
    }
}
