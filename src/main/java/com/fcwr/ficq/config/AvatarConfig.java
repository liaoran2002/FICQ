package com.fcwr.ficq.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * 设置虚拟路径，访问绝对路径下资源
 */
@Configuration
public class AvatarConfig implements WebMvcConfigurer{
    @Value("${file.staticAvatarPath}")
    private String staticAvatarPath;
    @Value("${file.upAvatarFolder}")
    private String upAvatarFolder;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(staticAvatarPath).addResourceLocations("file:///" + upAvatarFolder.replace("\\", "/"));
    }
}
