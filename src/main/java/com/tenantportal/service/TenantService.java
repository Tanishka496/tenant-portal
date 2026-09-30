package com.tenantportal.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenantportal.dto.TenantResponse;
import com.tenantportal.repository.TenantRepository;

@Service
@Transactional(readOnly = true)
public class TenantService {
    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public TenantResponse getCurrentTenant(String authenticatedUsername) {
        return tenantRepository.findByUserUsername(authenticatedUsername)
                .map(TenantResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant profile was not found"));
    }

    public List<TenantResponse> getAllTenants() {
        return tenantRepository.findAll().stream()
                .map(TenantResponse::from)
                .toList();
    }
}
