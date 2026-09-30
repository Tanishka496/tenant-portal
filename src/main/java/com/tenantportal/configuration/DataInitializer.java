package com.tenantportal.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tenantportal.entity.AppUser;
import com.tenantportal.entity.Role;
import com.tenantportal.repository.AppUserRepository;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner createBootstrapAdmin(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap-admin.username}") String username,
            @Value("${app.bootstrap-admin.password}") String password) {
        return args -> {
            if (!userRepository.existsByUsername(username)) {
                userRepository.save(new AppUser(username, passwordEncoder.encode(password), Role.ADMIN));
            }
        };
    }
}
