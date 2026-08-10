package com.firstproject.journalApp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myconfig(){
        return new OpenAPI().info(
          new Info().title("Journal App API's").description("By Pankaj")
        )
                .servers(Arrays.asList(new Server().url("http://localhost:8080/journal").description("localhost"),
                        new Server().url("http://localhost:8081/journal").description("localhost"),
                        new Server().url("http://localhost:8081/journalapp").description("localhost"),
                        new Server().url("https://srv1845286.hstgr.cloud/journal").description("live"),
                        new Server().url("https://srv1845286.hstgr.cloud/journalapp").description("live")))
                .tags(Arrays.asList(
                        new Tag().name("Public APIs")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes(
                        "bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                ));

    }
}
