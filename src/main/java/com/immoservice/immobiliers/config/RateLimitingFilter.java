package com.immoservice.immobiliers.config;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    private static final int SC_TOO_MANY_REQUESTS = 429;
    private final int MAX_REQUESTS_PER_MINUTE = 100;
    private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public RateLimitingFilter() {
        // Planifier la réinitialisation des compteurs toutes les minutes
        scheduler.scheduleAtFixedRate(this::resetCounts, 1, 1, TimeUnit.MINUTES);
    }

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();
        requestCounts.putIfAbsent(clientIp, new AtomicInteger(0));
        int requests = requestCounts.get(clientIp).incrementAndGet();

        if (requests > MAX_REQUESTS_PER_MINUTE) {
            response.setStatus(SC_TOO_MANY_REQUESTS);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void resetCounts() {
        // Réinitialiser les compteurs des adresses IP
        requestCounts.clear();
    }
}
