package com.tenantportal.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenantportal.dto.RegisterRequest;
import com.tenantportal.dto.TenantResponse;
import com.tenantportal.entity.AppUser;
import com.tenantportal.entity.Role;
import com.tenantportal.entity.Tenant;
import com.tenantportal.repository.AppUserRepository;
import com.tenantportal.repository.TenantRepository;

@Service
public class AuthService {
    private final AppUserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AppUserRepository userRepository,
                       TenantRepository tenantRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public TenantResponse registerTenant(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username is already registered");
        }
        if (tenantRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email is already registered");
        }

        AppUser user = userRepository.save(new AppUser(
                request.username(),
                passwordEncoder.encode(request.password()),
                Role.TENANT));
        Tenant tenant = tenantRepository.save(new Tenant(
                user,
                request.fullName(),
                request.email(),
                request.phone(),
                request.flatNumber()));
        return TenantResponse.from(tenant);
    }
}
