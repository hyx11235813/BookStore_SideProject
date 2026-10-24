package yixian.website.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import yixian.website.common.Roles;
import yixian.website.config.JwtProperties;
import yixian.website.config.WebConfig;
import yixian.website.controller.admin.AdminUserController;
import yixian.website.entity.User;
import yixian.website.exception.GlobalExceptionHandler;
import yixian.website.repository.UserRepository;
import yixian.website.security.AdminInterceptor;
import yixian.website.security.AuthInterceptor;
import yixian.website.security.CurrentUserArgumentResolver;
import yixian.website.security.JwtService;
import yixian.website.service.UserService;

import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
@Import({WebConfig.class, AuthInterceptor.class, AdminInterceptor.class, CurrentUserArgumentResolver.class,
        JwtService.class, GlobalExceptionHandler.class})
@EnableConfigurationProperties(JwtProperties.class)
@TestPropertySource(properties = "app.jwt.secret=unit-test-secret-with-at-least-32-chars")
class AdminAccessTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;

    @Test
    void normalUserCannotAccessAdminApi() throws Exception {
        when(userRepository.findById(1)).thenReturn(Optional.of(userWithRole(1, Roles.USER)));

        mockMvc.perform(get("/admin/user/search/id/2").header("token", jwtService.createToken(1)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminGetsUserWithoutSensitiveFields() throws Exception {
        User admin = userWithRole(1, Roles.ADMIN);
        when(userRepository.findById(1)).thenReturn(Optional.of(admin));
        User target = userWithRole(2, Roles.USER);
        target.setPassword("hashed-secret");
        target.setActivationCode("activation-secret");
        when(userService.findById(2)).thenReturn(target);

        mockMvc.perform(get("/admin/user/search/id/2").header("token", jwtService.createToken(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.isActive").value(true))
                .andExpect(jsonPath("$.data.creationDate").value("2024-01-01 08:00:00"))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.activationCode").doesNotExist());
    }

    private User userWithRole(int id, String role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        user.setIsActive(true);
        user.setCreationDate(Date.from(java.time.Instant.parse("2024-01-01T00:00:00Z")));
        return user;
    }
}
