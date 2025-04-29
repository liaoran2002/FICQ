package org.lc.ficq.config;

import lombok.AllArgsConstructor;
import org.lc.ficq.interceptor.AuthInterceptor;
import org.lc.ficq.interceptor.XssInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class MvcConfig implements WebMvcConfigurer {
    @Bean
    public FilterRegistrationBean<CachingRequestBodyFilter> cachingRequestBodyFilter() {
        FilterRegistrationBean<CachingRequestBodyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CachingRequestBodyFilter());
        registrationBean.addUrlPatterns("/*"); // 对所有请求生效
        return registrationBean;
    }

    private final AuthInterceptor authInterceptor;
    private final XssInterceptor xssInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(xssInterceptor).addPathPatterns("/**").excludePathPatterns("/error");
        registry.addInterceptor(authInterceptor).addPathPatterns("/**")
            .excludePathPatterns("/login", "/logout", "/register", "/refreshToken", "/swagger/**", "/v3/api-docs/**",
                "/swagger-resources/**", "/swagger-ui.html", "/swagger-ui/**", "/doc.html");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用BCrypt加密密码
        return new BCryptPasswordEncoder();
    }

}
