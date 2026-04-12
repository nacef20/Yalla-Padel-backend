package tn.esprit.userservice.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Configuration
public class KeycloakConfig {

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8085")
                .realm("jungleinenglish-realm")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId("jungleinenglish-backend")
                .clientSecret("pQJW27kylPU2e1yo0CW4voEBy6bQqh8Z")
                .build();
    }
}
