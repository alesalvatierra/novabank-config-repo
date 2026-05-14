package com.novabank.cuenta.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cuentaServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cuenta Service API")
                        .description("API reactiva para la gestión de clientes de NovaBank")
                        .version("1.0")
                        .contact(new Contact()
                                .name("NovaBank")
                                .email("soporte@novabank.com"))
                        .license(new License()
                                .name("Uso académico")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentación del proyecto"));
    }
}
