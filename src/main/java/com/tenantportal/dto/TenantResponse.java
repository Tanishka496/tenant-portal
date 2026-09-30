package com.tenantportal.dto;

import com.tenantportal.entity.Tenant;

public record TenantResponse(
        Long id,
        String username,
        String fullName,
        String email,
        String phone,
        String flatNumber) {

    public static TenantResponse from(Tenant tenant) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getUser().getUsername(),
                tenant.getFullName(),
                tenant.getEmail(),
                tenant.getPhone(),
                tenant.getFlatNumber());
    }
}
