package moustapha.teach.gestionVoyage.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI yoonOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Yoon API")
                        .version("1.0.0")
                        .description("""
                                Réservation de voyages en autocar au Sénégal.
                                Connectez-vous avec POST /api/auth/connexion puis cliquez sur « Authorize »
                                et collez le jeton JWT."""))
                .components(new Components().addSecuritySchemes("bearer",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer"));
    }
}
