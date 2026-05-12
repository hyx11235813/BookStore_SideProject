package yixian.website.user.controller;

import org.springframework.web.bind.annotation.*;
import yixian.website.common.Result;
import yixian.website.common.SuccessMessages;
import yixian.website.dto.UserDto;
import yixian.website.model.Users;
import yixian.website.service.UserManagementService;
import yixian.website.util.TokenUtils;
import yixian.website.verification.RegexValidator;

@RestController
@RequestMapping("/user-info")
public class UserInfoController {
    private final UserManagementService userManagementService;
    private final TokenUtils tokenUtils;

    public UserInfoController(UserManagementService userManagementService, TokenUtils tokenUtils) {
        this.userManagementService = userManagementService;
        this.tokenUtils = tokenUtils;
    }

    @GetMapping("/user")
    public Users getCurrentUser() {
        return tokenUtils.getCurrentUser();
    }


    @PutMapping("/change-name")
    public Result changeUserName(@RequestBody UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().trim().isEmpty()) {
            return Result.updateError("用戶名稱不能為空");
        }
        userManagementService.setUserName(userDto);
        return Result.updateSuccess(null, SuccessMessages.SAVE_OK);
    }


    @PutMapping("/upload-avatar")
    public Result changeUserAvatar(@RequestBody UserDto userDto) {
        userManagementService.updateUserAvatar(userDto);
        return Result.updateSuccess(null, "更新頭像成功");
    }

    @PutMapping("/remove-avatar/{id}")
    public Result removeUserAvatar(@PathVariable Integer id) {
        userManagementService.removeUserAvatar(id);
        return Result.updateSuccess(null, "移除用戶頭像成功");
    }

    @PutMapping("/change-password")
    public Result changePassword(@RequestBody UserDto userDto) {
        boolean isValidPassword = RegexValidator.isValidPassword(userDto.getPassword());
        if (!isValidPassword) {
            return Result.updateError("密碼格式錯誤");
        }
        userManagementService.changePassword(userDto);
        return Result.updateSuccess(null, "密碼修改成功");
    }
}

