package yixian.website.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import yixian.website.common.Roles;
import yixian.website.config.JwtProperties;
import yixian.website.config.WebConfig;
import yixian.website.controller.admin.AdminBookController;
import yixian.website.dto.request.BookSaveRequest;
import yixian.website.dto.request.StoreBookQuery;
import yixian.website.entity.Book;
import yixian.website.entity.User;
import yixian.website.exception.GlobalExceptionHandler;
import yixian.website.repository.UserRepository;
import yixian.website.security.AdminInterceptor;
import yixian.website.security.AuthInterceptor;
import yixian.website.security.CurrentUserArgumentResolver;
import yixian.website.security.JwtService;
import yixian.website.service.BookService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AdminBookController.class, StoreBookController.class})
@Import({WebConfig.class, AuthInterceptor.class, AdminInterceptor.class, CurrentUserArgumentResolver.class,
        JwtService.class, GlobalExceptionHandler.class})
@EnableConfigurationProperties(JwtProperties.class)
@TestPropertySource(properties = "app.jwt.secret=unit-test-secret-with-at-least-32-chars")
class RequestBindingTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    @MockBean
    private BookService bookService;
    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void loginAsAdmin() {
        User admin = new User();
        admin.setId(1);
        admin.setRole(Roles.ADMIN);
        when(userRepository.findById(1)).thenReturn(Optional.of(admin));
    }

    @Test
    void adminBookFormIsBoundFromMultipart() throws Exception {
        Book saved = new Book();
        saved.setId(9);
        saved.setBookName("Clean Code");
        when(bookService.save(any())).thenReturn(saved);

        mockMvc.perform(multipart("/admin/book")
                        .file(new MockMultipartFile("uploadBookCover", "cover.jpg", "image/jpeg", new byte[]{1, 2, 3}))
                        .param("isbn", "9789573270126")
                        .param("bookName", "Clean Code")
                        .param("price", "480")
                        .param("coverUrl", "https://example.com/cover.jpg")
                        .header("token", jwtService.createToken(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(9))
                .andExpect(jsonPath("$.data.bookName").value("Clean Code"));

        ArgumentCaptor<BookSaveRequest> captor = ArgumentCaptor.forClass(BookSaveRequest.class);
        verify(bookService).save(captor.capture());
        assertEquals("9789573270126", captor.getValue().getIsbn());
        assertEquals(480, captor.getValue().getPrice());
        assertEquals("https://example.com/cover.jpg", captor.getValue().getCoverUrl());
        assertArrayEquals(new byte[]{1, 2, 3}, captor.getValue().getUploadBookCover().getBytes());
    }

    @Test
    void negativePriceIsRejected() throws Exception {
        mockMvc.perform(multipart("/admin/book")
                        .param("price", "-1")
                        .header("token", jwtService.createToken(1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("價格不得為負數"));
    }

    @Test
    void storeSearchParametersAreBoundAndPublic() throws Exception {
        when(bookService.searchForStore(any())).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/user/books")
                        .param("cat", "文學小說").param("q", "java").param("sortBy", "price")
                        .param("currentPage", "2").param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(0));

        ArgumentCaptor<StoreBookQuery> captor = ArgumentCaptor.forClass(StoreBookQuery.class);
        verify(bookService).searchForStore(captor.capture());
        assertEquals("文學小說", captor.getValue().getCat());
        assertEquals("java", captor.getValue().getQ());
        assertEquals("price", captor.getValue().getSortBy());
        assertEquals(1, captor.getValue().toPageable().getPageNumber()); //currentPage 從 1 開始，Pageable 從 0 開始
        assertEquals(5, captor.getValue().toPageable().getPageSize());
    }
}
