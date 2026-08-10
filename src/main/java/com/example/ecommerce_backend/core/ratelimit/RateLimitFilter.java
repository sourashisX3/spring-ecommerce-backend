package com.example.ecommerce_backend.core.ratelimit;

import com.example.ecommerce_backend.modules.user.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);
    private static final String ACTUATOR_PREFIX = "/actuator";
    private static final String SWAGGER_PREFIX = "/swagger-ui";
    private static final String API_DOCS_PREFIX = "/v3/api-docs";

    private final RateLimitService rateLimitService;

    public RateLimitFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String group = resolveGroup(path);
        if (group == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = resolveKey(request);
        if (!rateLimitService.tryConsume(key, group)) {
            log.warn("Rate limit exceeded for key={} group={} path={}", key, group, path);
            response.setStatus(429);
            response.setHeader("Retry-After", "60");
            response.setContentType("application/json");
            if (request.getHeader("Origin") != null) {
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Vary", "Origin");
            }
            response.getWriter().write("{\"statusCode\":429,\"message\":\"Too many requests. Please try again later.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveGroup(String path) {
        if (path.contains(ACTUATOR_PREFIX) || path.contains(SWAGGER_PREFIX) || path.contains(API_DOCS_PREFIX)) {
            return null;
        }

        if (path.contains("/auth/login")) return "auth_login";
        if (path.contains("/auth/refresh")) return "auth_refresh";
        if (path.contains("/auth/register")) return "auth_register";
        if (path.contains("/auth/send-otp")) return "auth_send-otp";
        if (path.contains("/auth/")) return "auth_login";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return "api_authenticated";
        }
        return "api_public";
    }

    private String resolveKey(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Object principal = auth.getPrincipal();
            if (principal instanceof User user) {
                return "user:" + user.getId();
            }
            return "user:" + auth.getName();
        }
        String ip = request.getRemoteAddr();
        if (ip == null || ip.isBlank()) {
            ip = request.getHeader("X-Forwarded-For");
        }
        return "ip:" + (ip != null ? ip : "unknown");
    }
}
