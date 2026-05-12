package yixian.website.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerifyRequestDto {
    private String email;
    private String token;
    private String code;

}
