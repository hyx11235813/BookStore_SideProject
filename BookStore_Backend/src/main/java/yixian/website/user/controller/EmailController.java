package yixian.website.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yixian.website.exception.EntityNotFoundException;
import yixian.website.service.UserManagementService;

import java.io.IOException;

@RestController
@RequestMapping("/email")
public class EmailController {
    private final UserManagementService userManagementService;

    public EmailController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @Value("${app.url}")
    private String url;
    @GetMapping("/activate/{activationCode}")
    public void activateUserAccount(@PathVariable String activationCode, HttpServletResponse response) throws IOException {
        try {
            userManagementService.activateAccount(activationCode);
            response.sendRedirect(url + "/register/successInfo");
        } catch (EntityNotFoundException e) {
            response.sendRedirect(url + "/error");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(url + "/error");
        }
    }
}
