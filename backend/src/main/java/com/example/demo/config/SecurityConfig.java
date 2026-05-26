package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Rutas públicas
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/packages/available").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/packages/{id}").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Rutas de admin (requieren rol ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/packages").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/packages").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/packages/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/packages/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/packages/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reservations").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reports/**").hasRole("ADMIN")

                        // Rutas autenticadas
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/reservations/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/reservations/user/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/reservations").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/reservations/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/payments/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/payments").authenticated()

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        // Extrae correctamente los roles desde realm_access.roles del token de Keycloak
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess == null) {
                return Collections.emptyList();
            }

            Object rolesObj = realmAccess.get("roles");
            if (!(rolesObj instanceof List<?>)) {
                return Collections.emptyList();
            }

            List<?> roles = (List<?>) rolesObj;
            return roles.stream()
                    .filter(role -> role instanceof String)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        });

        return converter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://localhost:80",
                "http://localhost"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
