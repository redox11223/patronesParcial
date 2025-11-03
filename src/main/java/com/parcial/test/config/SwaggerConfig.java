package com.parcial.test.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI serfOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema SERF - API REST")
                        .description("Sistema Empresarial de Gestión de Reportes Financieros para FinanCorp S.A.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("FinanCorp S.A.")
                                .email("soporte@financorp.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}

