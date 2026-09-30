package com.tenantportal.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tenantportal.dto.TenantResponse;
import com.tenantportal.service.TenantService;

@RestController
@RequestMapping("/api/tenant")
public class TenantController {
    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping("/me")
    public TenantResponse getCurrentTenant(Authentication authentication) {
        return tenantService.getCurrentTenant(authentication.getName());
    }
}
