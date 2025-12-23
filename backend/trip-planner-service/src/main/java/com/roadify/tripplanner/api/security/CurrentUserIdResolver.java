package com.roadify.tripplanner.api.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Extracts the current user id from the authenticated JWT.
 * userId = JWT "sub" claim.
 */
@Component
public class CurrentUserIdResolver {

    public String getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("Authentication is missing");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwt) {
            String sub = jwt.getSubject();
            if (sub == null || sub.isBlank()) {
                throw new IllegalStateException("JWT subject (sub) is missing");
            }
            return sub;
        }

        // Fallback: some setups expose the subject as name
        String name = authentication.getName();
        if (name == null || name.isBlank()) {
            throw new IllegalStateException("Authenticated principal has no name");
        }
        return name;
    }
}
