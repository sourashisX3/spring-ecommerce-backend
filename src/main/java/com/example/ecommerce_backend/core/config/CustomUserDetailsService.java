package com.example.ecommerce_backend.core.config;

import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException {
        User user;
        if (emailOrPhone.contains("@")) {
            user = userRepository.findByEmail(emailOrPhone)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + emailOrPhone));
        } else {
            user = userRepository.findByPhoneNumber(emailOrPhone)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + emailOrPhone));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList()
        );
    }
}
