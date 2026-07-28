package com.example.ecommerce_backend.core.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "ratelimit")
public class RateLimitConfig {

    private final Map<String, LimitProperties> auth = new HashMap<>();
    private final Map<String, LimitProperties> api = new HashMap<>();

    public Map<String, LimitProperties> getAuth() {
        return auth;
    }

    public Map<String, LimitProperties> getApi() {
        return api;
    }

    public Bandwidth resolveBandwidth(String group) {
        LimitProperties props = resolveProps(group);
        if (props == null) {
            return Bandwidth.simple(100, Duration.ofMinutes(1));
        }
        return Bandwidth.classic(props.getLimit(), Refill.greedy(props.getLimit(), props.windowDuration()));
    }

    private LimitProperties resolveProps(String group) {
        switch (group) {
            case "auth_login": return auth.get("login");
            case "auth_register": return auth.get("register");
            case "auth_send-otp": return auth.get("send-otp");
            case "api_authenticated": return api.get("authenticated");
            case "api_public": return api.get("public");
            default: return null;
        }
    }

    public static class LimitProperties {
        private int limit;
        private String window;

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public String getWindow() {
            return window;
        }

        public void setWindow(String window) {
            this.window = window;
        }

        public Duration windowDuration() {
            if (window.endsWith("m")) {
                return Duration.ofMinutes(Long.parseLong(window.replace("m", "")));
            } else if (window.endsWith("s")) {
                return Duration.ofSeconds(Long.parseLong(window.replace("s", "")));
            } else if (window.endsWith("h")) {
                return Duration.ofHours(Long.parseLong(window.replace("h", "")));
            }
            return Duration.ofMinutes(1);
        }
    }
}
