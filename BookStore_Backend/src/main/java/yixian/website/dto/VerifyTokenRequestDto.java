package yixian.website.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class VerifyTokenRequestDto {

    @NotBlank(message = "Email不得為空")
    private String email;
    @NotBlank(message = "Token不得為空")
    private String token;
}
