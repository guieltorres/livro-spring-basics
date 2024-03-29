package com.creditas.livro.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@Profile("!prod")
@EnableSwagger2
class SwaggerConfig {

    @Bean
    fun api(): Docket = Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.creditas.livro.controller"))
        .paths(PathSelectors.any())
        .build()

        .apiInfo(
            ApiInfoBuilder()
            .title("Mercado-Livro")
            .description("Api do Mercado Livros")
            .build()
        )
}