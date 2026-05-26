package com.example.demo.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class KeycloakPrincipalExtractor {

    public String getUsername(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt.getClaimAsString("preferred_username");
        }
        return null;
    }

    public String getEmail(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwtA = (Jwt) authentication.getPrincipal();
            return jwtA.getClaimAsString("email");
        }
        return null;
    }

    public java.util.List<String> getRoles(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null) {
                return (java.util.List<String>) realmAccess.get("roles");
            }
        }
        return java.util.Collections.emptyList();
    }
}
