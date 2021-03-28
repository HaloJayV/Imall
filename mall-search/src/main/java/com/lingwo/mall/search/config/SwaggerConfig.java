package com.lingwo.mall.search.config;

import com.lingwo.mall.common.config.BaseSwaggerConfig;
import com.lingwo.mall.common.domain.SwaggerProperties;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger API文档相关配置
 * Created by lingwo on 2018/4/26.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig extends BaseSwaggerConfig {

    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder()
                .apiBasePackage("com.lingwo.mall.search.controller")
                .title("mall搜索系统")
                .description("mall搜索相关接口文档")
                .contactName("lingwo")
                .version("1.0")
                .enableSecurity(false)
                .build();
    }
}
