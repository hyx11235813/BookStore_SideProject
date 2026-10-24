package yixian.website.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record UserResponse(
        Integer id,
        String account,
        String email,
        String name,
        String role,
        Boolean isActive,
        String avatar,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Taipei")
        Date creationDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Taipei")
        Date editDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Taipei")
        Date loginDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Taipei")
        Date lastLoginDate) {
}
