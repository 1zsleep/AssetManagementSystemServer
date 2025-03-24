// WebMvcConfig.java
package com.example.assetManagementSystemServer.config;

import com.example.assetManagementSystemServer.base.query.ListParamResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

/**
 * MVC配置 - 注册自定义参数解析器
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ListParamResolver());
    }
}