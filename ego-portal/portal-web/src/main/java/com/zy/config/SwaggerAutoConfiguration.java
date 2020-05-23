package com.zy.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger 配置
 */
@Configuration
@EnableSwagger2 // 开启swagger的功能
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfiguration {

  private SwaggerProperties swaggerProperties ;

  public SwaggerAutoConfiguration(SwaggerProperties swaggerProperties){
    this.swaggerProperties = swaggerProperties ;
  }
  @Bean
  public Docket docket(){

    return new Docket(DocumentationType.SWAGGER_2).
      apiInfo(getApiInfo()).
      select().apis(RequestHandlerSelectors.basePackage("")).build();
  }

  private ApiInfo getApiInfo() {
    Contact contact = new Contact("", "", "");
    return new ApiInfoBuilder().
      contact(contact).
      title("").
      description("").
      termsOfServiceUrl("").
      license("").
      licenseUrl("").
      build();
  }
}

