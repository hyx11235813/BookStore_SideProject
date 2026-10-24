package yixian.website.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import yixian.website.common.Roles;
import yixian.website.config.JwtProperties;
import yixian.website.config.WebConfig;
import yixian.website.entity.User;
import yixian.website.exception.GlobalExceptionHandler;
import yixian.website.repository.UserRepository;
import yixian.website.security.AdminInterceptor;
import yixian.website.security.AuthInterceptor;
import yixian.website.security.CurrentUserArgumentResolver;
import yixian.website.security.JwtService;
import yixian.website.service.CartService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@Import({WebConfig.class, AuthInterceptor.class, AdminInterceptor.class, CurrentUserArgumentResolver.class,
        JwtService.class, GlobalExceptionHandler.class})
@EnableConfigurationProperties(JwtProperties.class)
@TestPropertySource(properties = "app.jwt.secret=unit-test-secret-with-at-least-32-chars")
class CartControllerTest {
    private static final int USER_ID = 3;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    @MockBean
    private CartService cartService;
    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void loginAsUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setRole(Roles.USER);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    }

    @Test
    void requestWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(get("/user/cart/" + USER_ID))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void userCanReadOwnCart() throws Exception {
        when(cartService.getCartItems(USER_ID)).thenReturn(List.of());

        mockMvc.perform(get("/user/cart/" + USER_ID).header("token", jwtService.createToken(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void userCannotReadAnotherUsersCart() throws Exception {
        mockMvc.perform(get("/user/cart/999").header("token", jwtService.createToken(USER_ID)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void addItemsUsesTheUserFromTheToken() throws Exception {
        when(cartService.addItems(eq(USER_ID), any())).thenReturn(List.of());

        mockMvc.perform(post("/user/cart")
                        .header("token", jwtService.createToken(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"userId\": 999, \"bookId\": 7, \"quantity\": 2}]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("購物車更新成功"));
    }
}
