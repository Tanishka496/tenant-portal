package com.tenantportal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tenantportal.dto.TenantResponse;
import com.tenantportal.service.TenantService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final TenantService tenantService;

    public AdminController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping("/tenants")
    public List<TenantResponse> getAllTenants() {
        return tenantService.getAllTenants();
    }
}
