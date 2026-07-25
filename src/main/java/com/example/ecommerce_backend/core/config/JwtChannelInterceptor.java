package com.example.ecommerce_backend.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("!test")
public class JwtChannelInterceptor implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtChannelInterceptor.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtChannelInterceptor(JwtTokenProvider jwtTokenProvider,
                                 UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = extractToken(accessor);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.extractEmail(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                accessor.setUser(authentication);
                log.debug("WebSocket authenticated: {}", email);
            } else {
                log.warn("WebSocket connection rejected: invalid or missing token");
            }
        }

        return message;
    }

    private String extractToken(StompHeaderAccessor accessor) {
        List<String> tokenParam = accessor.getNativeHeader("token");
        if (tokenParam != null && !tokenParam.isEmpty()) {
            return tokenParam.get(0);
        }
        return null;
    }
}
