package yixian.website.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yixian.website.common.Result;
import yixian.website.common.SuccessMessages;
import yixian.website.dto.request.ChangeNameRequest;
import yixian.website.dto.request.ChangePasswordRequest;
import yixian.website.dto.request.UpdateAvatarRequest;
import yixian.website.dto.response.UserResponse;
import yixian.website.mapper.UserMapper;
import yixian.website.security.AuthenticatedUser;
import yixian.website.security.CurrentUser;
import yixian.website.service.UserService;

/**
 * 登入使用者管理自己的資料
 */
@RestController
@RequestMapping("/user-info")
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;

    @GetMapping("/user")
    public Result<UserResponse> getCurrentUser(@CurrentUser AuthenticatedUser currentUser) {
        return Result.ok(UserMapper.toResponse(userService.findById(currentUser.id())), SuccessMessages.GET_OK);
    }

    @PutMapping("/change-name")
    public Result<Void> changeName(@CurrentUser AuthenticatedUser currentUser,
                                   @RequestBody @Valid ChangeNameRequest request) {
        userService.rename(currentUser.id(), request.name());
        return Result.ok(SuccessMessages.SAVE_OK);
    }

    @PutMapping("/upload-avatar")
    public Result<Void> changeAvatar(@CurrentUser AuthenticatedUser currentUser,
                                     @RequestBody @Valid UpdateAvatarRequest request) {
        userService.changeAvatar(currentUser.id(), request.avatar());
        return Result.ok("更新頭像成功");
    }

    @PutMapping("/remove-avatar/{id}")
    public Result<Void> removeAvatar(@CurrentUser AuthenticatedUser currentUser, @PathVariable Integer id) {
        currentUser.requireSameUser(id);
        userService.removeAvatar(id);
        return Result.ok("移除用戶頭像成功");
    }

    @PutMapping("/change-password")
    public Result<Void> changePassword(@CurrentUser AuthenticatedUser currentUser,
                                       @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(currentUser.id(), request.password());
        return Result.ok("密碼修改成功");
    }
}
