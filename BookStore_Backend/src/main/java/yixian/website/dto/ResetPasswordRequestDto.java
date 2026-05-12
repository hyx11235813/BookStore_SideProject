package yixian.website.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDto {
    @NotBlank(message = "Email不得為空")
    @Email(message = "Email格式不正確")
    private String email;
    @NotBlank(message = "新密碼不得為空")
    private String password;
}
