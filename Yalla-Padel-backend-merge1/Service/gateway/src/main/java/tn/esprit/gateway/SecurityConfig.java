package tn.esprit.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Flux;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/public/**").permitAll()
                        .pathMatchers("/uploads/**").permitAll()
                        .pathMatchers("/img/**").permitAll()
                        .pathMatchers("/api/users/add").permitAll()
                        .pathMatchers("/ws/**").permitAll()
                        .pathMatchers("/ws/info/**").permitAll()
                        .pathMatchers("/chatPrivee/**").hasRole("USER")
                        .pathMatchers("/messageGroup/**").hasRole("USER")
                        .pathMatchers("/joinGroup/**").hasRole("USER")
                        .pathMatchers("/membreGroupChat/**").hasRole("USER")
                        .pathMatchers("/reservations/**").hasRole("USER")
                        .pathMatchers("/clubs/**").hasAnyRole("ADMIN","USER")

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakConverter()))
                )
                .build();
    }



    @Bean
    public ReactiveJwtAuthenticationConverter keycloakConverter() {

        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            Map<String, Object> realmAccess = jwt.getClaim("realm_access");

            System.out.println("=== JWT DEBUG ===");
            System.out.println("realmAccess = " + realmAccess);

            if (realmAccess == null || realmAccess.get("roles") == null) {
                System.out.println("NO ROLES FOUND");
                return Flux.empty();
            }

            List<String> roles = (List<String>) realmAccess.get("roles");

            System.out.println("ROLES FROM KEYCLOAK = " + roles);

            System.out.println("=== END JWT DEBUG ===");

            return Flux.fromIterable(
                    roles.stream()
                            // 🔥 filtre les rôles système Keycloak
                            .filter(role -> role != null && !role.startsWith("default-roles"))
                            .map(role -> {
                                String authority = "ROLE_" + role;
                                System.out.println("ADDING AUTHORITY = " + authority);
                                return (GrantedAuthority) new SimpleGrantedAuthority(authority);
                            })
                            .toList()
            );
        });

        return converter;
    }
}