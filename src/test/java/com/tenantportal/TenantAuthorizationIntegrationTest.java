package com.tenantportal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.tenantportal.entity.AppUser;
import com.tenantportal.entity.Role;
import com.tenantportal.entity.Tenant;
import com.tenantportal.repository.AppUserRepository;
import com.tenantportal.repository.TenantRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TenantAuthorizationIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Tenant tenantA;
    private Tenant tenantB;

    @BeforeEach
    void setUp() {
        tenantRepository.deleteAll();
        userRepository.deleteAll();

        AppUser admin = userRepository.save(new AppUser("admin-test", passwordEncoder.encode("admin-password"), Role.ADMIN));
        AppUser userA = userRepository.save(new AppUser("tenant-a", passwordEncoder.encode("tenant-password"), Role.TENANT));
        AppUser userB = userRepository.save(new AppUser("tenant-b", passwordEncoder.encode("tenant-password"), Role.TENANT));

        tenantA = tenantRepository.save(new Tenant(userA, "Tenant A", "a@example.com", "9000000001", "A-101"));
        tenantB = tenantRepository.save(new Tenant(userB, "Tenant B", "b@example.com", "9000000002", "B-202"));
        userRepository.flush();
        tenantRepository.flush();
    }

    @Test
    void tenantCanAccessOwnProfile() throws Exception {
        mockMvc.perform(get("/api/tenant/me").session(login("tenant-a", "tenant-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("tenant-a"))
                .andExpect(jsonPath("$.fullName").value("Tenant A"));
    }

    @Test
    void tenantCannotAccessAdminTenantList() throws Exception {
        mockMvc.perform(get("/api/admin/tenants").session(login("tenant-a", "tenant-password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanAccessAllTenants() throws Exception {
        mockMvc.perform(get("/api/admin/tenants").session(login("admin-test", "admin-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username").exists())
                .andExpect(jsonPath("$[1].username").exists());
    }

    @Test
    void tenantCannotUseAnotherTenantId() throws Exception {
        mockMvc.perform(get("/api/tenant/{tenantId}", tenantB.getId())
                        .session(login("tenant-a", "tenant-password")))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/tenant/me")
                        .param("tenantId", tenantB.getId().toString())
                        .session(login("tenant-a", "tenant-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("tenant-a"))
                .andExpect(jsonPath("$.username").value(org.hamcrest.Matchers.not("tenant-b")));
    }

    @Test
    void loginReturnsAuthenticatedTenantRole() throws Exception {
        mockMvc.perform(get("/api/auth/me").session(login("tenant-a", "tenant-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("tenant-a"))
                .andExpect(jsonPath("$.role").value("TENANT"));
    }

    @Test
    void adminLoginReturnsAuthenticatedAdminRole() throws Exception {
        mockMvc.perform(get("/api/auth/me").session(login("admin-test", "admin-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin-test"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void invalidCredentialsAreRejected() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"tenant-a\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unauthenticatedProtectedRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/tenant/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void frontendStaticResourcesArePublic() throws Exception {
        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void csrfTokenEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void registrationCreatesTenantAndNeverAdmin() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "new-tenant",
                                  "password": "tenant-password",
                                  "fullName": "New Tenant",
                                  "email": "new@example.com",
                                  "phone": "9000000003",
                                  "flatNumber": "C-303"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("new-tenant"));

        mockMvc.perform(get("/api/auth/me").session(login("new-tenant", "tenant-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("TENANT"));
    }

    @Test
    void duplicateUsernameAndEmailAreRejected() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "tenant-a",
                                  "password": "tenant-password",
                                  "fullName": "Duplicate User",
                                  "email": "new-user@example.com",
                                  "phone": "9000000004"
                                }
                                """))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "new-user",
                                  "password": "tenant-password",
                                  "fullName": "Duplicate Email",
                                  "email": "a@example.com",
                                  "phone": "9000000005"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void logoutInvalidatesSessionAndRequiresCsrf() throws Exception {
        MockHttpSession session = login("tenant-a", "tenant-password");

        mockMvc.perform(post("/api/auth/logout").session(session))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/auth/logout").with(csrf()).session(session))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tenant/me").session(session))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminLogoutInvalidatesSession() throws Exception {
        MockHttpSession session = login("admin-test", "admin-password");

        mockMvc.perform(post("/api/auth/logout").with(csrf()).session(session))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/admin/tenants").session(session))
                .andExpect(status().isUnauthorized());
    }

    private MockHttpSession login(String username, String password) throws Exception {
        String body = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent())
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }
}
