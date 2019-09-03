package cn.geobeans.encrypt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.security.Principal;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private Class[] ignoreClass = new Class[]{
            Principal.class,
            Pageable.class
    };

    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("jar包进行加密")
                        .description("提供所有与平台相关的所有接口")
                        .version("1.0.0")
                        .contact(new Contact("GeoBeans","http://www.geobeans.cn","market@digitalearth.cn"))
                        .build())
                .ignoredParameterTypes(ignoreClass)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
}
