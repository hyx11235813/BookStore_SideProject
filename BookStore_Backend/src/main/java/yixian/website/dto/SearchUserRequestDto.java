package yixian.website.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchUserRequestDto {
    private String name;
    private String account;
    private String email;
    private String role;
    private Boolean isActive ;
    private String creationDateStart;
    private String creationDateEnd;
    private String editDateStart;
    private String editDateEnd;
    PageRequestDto page;

}
