package top.pxczxn.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI starhavenOpenApi() {
        return new OpenAPI().info(new Info()
                .title("StarHaven 星栖 API")
                .description("民宿预订平台接口文档")
                .version("1.0.0"));
    }

    @Bean
    public GroupedOpenApi appApi() {
        return GroupedOpenApi.builder().group("用户端").pathsToMatch("/api/v1/**").build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder().group("管理后台").pathsToMatch("/admin/**").build();
    }
}
