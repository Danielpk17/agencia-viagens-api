package com.senai.agenciaviagens.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA_BASIC = "basicAuth";

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Agencia de Viagens")
                        .description("API RESTful de destinos turisticos com persistencia em PostgreSQL e seguranca com Spring Security")
                        .version("2.0.0"))
                .components(new Components().addSecuritySchemes(ESQUEMA_BASIC,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_BASIC));
    }
}
