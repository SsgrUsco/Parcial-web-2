package co.edu.usco.gestion_asignaturas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Parcial2 API - Consultas Medicas")
                        .description("Servicios web para administrar consultas medicas, medicos, pacientes y horarios segun el rol del usuario.")
                        .version("1.0.0")
                        .contact(new Contact().name("Proyecto Parcial Programacion Web")));
    }
}
