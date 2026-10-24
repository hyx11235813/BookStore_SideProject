package yixian.website.mapper;

import yixian.website.dto.response.UserResponse;
import yixian.website.entity.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getAccount(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getIsActive(),
                user.getAvatar(),
                user.getCreationDate(),
                user.getEditDate(),
                user.getLoginDate(),
                user.getLastLoginDate());
    }
}
