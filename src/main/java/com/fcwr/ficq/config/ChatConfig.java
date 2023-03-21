package com.fcwr.ficq.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 设置虚拟路径，访问绝对路径下资源
 */
@Configuration
public class ChatConfig implements WebMvcConfigurer{
    @Value("${file.staticChatPath}")
    private String staticChatPath;
    @Value("${file.upChatFolder}")
    private String upChatFolder;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(staticChatPath).addResourceLocations("file:///" + upChatFolder.replace("\\", "/"));
    }
}
