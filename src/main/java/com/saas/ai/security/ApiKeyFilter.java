package com.saas.ai.security;

import com.saas.ai.service.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiKeyFilter extends OncePerRequestFilter {

    private final ApiKeyService apiKeyService;

    @Value("${security.api-key.header:X-API-Key}")
    private String apiKeyHeader;

    @Value("${security.api-key.enabled:true}")
    private boolean apiKeyEnabled;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Bypass pour les endpoints publics
        String path = request.getRequestURI();
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!apiKeyEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader(apiKeyHeader);

        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Requête sans API Key: {}", path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"API Key manquante. Ajoutez le header X-API-Key.\"}");
            return;
        }

        if (!apiKeyService.validateApiKey(apiKey)) {
            log.warn("API Key invalide ou inactive: {}", apiKey);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"API Key invalide ou désactivée.\"}");
            return;
        }

        // Authentification OK
        var auth = new UsernamePasswordAuthenticationToken(
                apiKey, null, List.of(new SimpleGrantedAuthority("ROLE_API_CLIENT"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/h2-console")
                || path.equals("/api/health")
                || path.startsWith("/swagger")
                || path.startsWith("/v3/api-docs")
                || path.equals("/api/admin/clients"); // endpoint de création de clé
    }
}
