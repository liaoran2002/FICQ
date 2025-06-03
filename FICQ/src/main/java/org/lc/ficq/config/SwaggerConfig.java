package org.lc.ficq.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FICQ API")
                        .version("1.0.0")
                        .description("飞克尔聊天系统是一款仿QQ的即时通讯软件")
                        .license(new io.swagger.v3.oas.models.info.License()
                                .name("GPL-3.0")
                                .url("https://github.com/liaoran2002/FICQ")));
    }
}