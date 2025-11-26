package com.immoservice.immobiliers.config;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class OAuth2CorsBypassFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();
        if (isSwaggerOrOpenApiPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // ⚠️ Ne pas ajouter de headers CORS pour les routes OAuth2
        if (path.startsWith("/oauth2/") || path.startsWith("/auth/oauth2/")) {
            chain.doFilter(request, response);
            return;
        }

        // sinon, comportement normal
        chain.doFilter(request, response);
    }

    /**
     * Vérifie si le chemin correspond à Swagger ou OpenAPI
     */
    private boolean isSwaggerOrOpenApiPath(String path) {
        return path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/configuration/ui") ||
                path.startsWith("/configuration/security") ||
                path.startsWith("/webjars/") ||
                path.equals("/swagger-ui.html");
    }
}
