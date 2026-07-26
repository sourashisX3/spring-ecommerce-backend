package com.example.ecommerce_backend.core.config;

import com.example.ecommerce_backend.modules.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException {
        if (emailOrPhone.contains("@")) {
            return userRepository.findByEmailWithPermissions(emailOrPhone)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + emailOrPhone));
        }
        return userRepository.findByPhoneNumberWithPermissions(emailOrPhone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + emailOrPhone));
    }
}
