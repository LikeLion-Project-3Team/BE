package likelion.devbreak.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info()
                .title("User Management API")
                .version("1.0")
                .description("API for managing users and their profile images.")
                .contact(new Contact()
                        .name("sadew12")
                        .email("gusals9497@gmail.com"));

        Server server = new Server();
        server.setUrl("https://sadew12.store");

        Server server1 = new Server();
        server1.setUrl("http://localhost:8081");

        List<Server> serverList = new ArrayList<>();
        serverList.add(server);
        serverList.add(server1);

        return new OpenAPI()
                .info(info)
                .servers(serverList)
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}