package ru.sberbank.pprb.quote.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация сваггер
 * @author SagdievIA
 * Created: 15.01.2025
 */
@Configuration
@RequiredArgsConstructor
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {

    private final BuildProperties buildProperties;

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info().title("pprb-quote-service")
                        .description("Сервис котировок")
                        .version(buildProperties.getVersion())
                        .license(new License().name("Do not reproduce without permission in writing. Copyright (c) 2024 КОТЭ Group. All rights reserved.")
                        )
                )
                .components((new Components().addSecuritySchemes("bearer-jwt",
                        new io.swagger.v3.oas.models.security.SecurityScheme().type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                                .in(io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER).name("Authorization"))));
    }
}
