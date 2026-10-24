package yixian.website.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchRequest {
    private String name;
    private String account;
    private String email;
    private String role;
    private Boolean isActive;
    private String creationDateStart;
    private String creationDateEnd;
    private String editDateStart;
    private String editDateEnd;
}
