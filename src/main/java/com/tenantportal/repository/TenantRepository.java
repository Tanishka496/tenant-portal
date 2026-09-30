package com.tenantportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tenantportal.entity.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByUserUsername(String username);
    boolean existsByEmail(String email);
}
