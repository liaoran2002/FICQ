package com.fcwr.ficq.config;

import com.fcwr.ficq.listenner.SessionListener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SessionConfiguration implements WebMvcConfigurer {
    @Bean
    public ServletListenerRegistrationBean<SessionListener> servletListenerRegistrationBean(){
        ServletListenerRegistrationBean<SessionListener> slrBean=new ServletListenerRegistrationBean<SessionListener>();
        slrBean.setListener(new SessionListener());
        return slrBean;
    }
}
/*
这段代码是一个 Spring MVC 的配置类，用于注册自定义的 SessionListenner 监听器，具体说明如下：
@Configuration 注解：指示该类为 Spring 配置类。
public class SessionConfiguration extends WebMvcConfigurerAdapter：该类继承了 WebMvcConfigurerAdapter 类，表示它是一个 Spring MVC 的配置类，并且可以重写 WebMvcConfigurerAdapter 类中的方法以完成 MVC 的一些自定义配置。
@Bean 注解：将方法返回值声明为一个 bean，并将其注入到 Spring 容器中进行管理。
ServletListenerRegistrationBean<SessionListenner>：使用 ServletListenerRegistrationBean 来注册 SessionListenner 监听器。
setListener(new SessionListenner())：使用 setListener() 方法设置要注册的监听器为 SessionListenner。
return slrBean：返回配置好的 ServletListenerRegistrationBean 对象。
总之，该代码实现了在 Spring 应用程序中注册自定义的 SessionListenner 监听器，用于监视和管理会话（session），控制用户访问权限等。
*/