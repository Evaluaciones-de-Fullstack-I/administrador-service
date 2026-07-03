package cl.duoc.administrador.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigSwagger {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Microservicio - Administrador") // 👈 Cambia el nombre según el servicio
                        .version("1.0.0")
                        .description("Documentación oficial de los endpoints para la gestión del microservicio de Administrador ")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}




