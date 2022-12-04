package com.kqinfo.universal.common.config;

import com.kqinfo.universal.common.properties.SwaggerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * swagger2 配置
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Configuration
@EnableSwagger2
@ConditionalOnClass(Docket.class)
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerConfiguration {

    @Autowired
    private SwaggerProperties swaggerProperties;

    private static final String HEAD_KEY = "Authorization";

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).enable(true).apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage())).paths(PathSelectors.any()).build()
                .securitySchemes(securitySchemes()).securityContexts(securityContexts());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(swaggerProperties.getTitle()).description(swaggerProperties.getDescription())
                .contact(new Contact("dev", null, "?@cqkqinfo.com")).version("1.0.0").build();
    }

    private List<ApiKey> securitySchemes() {
        // 设置请求头信息
        List<ApiKey> result = new ArrayList<>();
        ApiKey apiKey = new ApiKey(HEAD_KEY, HEAD_KEY, "header");
        result.add(apiKey);
        return result;
    }

    private List<SecurityContext> securityContexts() {
        // 设置需要登录认证的路径
        List<SecurityContext> result = new ArrayList<>();
        result.add(getContextByPath("/*.*"));
        return result;
    }

    private SecurityContext getContextByPath(String pathRegex) {
        return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.regex(pathRegex))
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        List<SecurityReference> result = new ArrayList<>();
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        result.add(new SecurityReference(HEAD_KEY, authorizationScopes));
        return result;
    }

}
