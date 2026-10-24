package yixian.website.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yixian.website.common.Result;
import yixian.website.common.SuccessMessages;
import yixian.website.dto.request.AdminUserSaveRequest;
import yixian.website.dto.request.PageParams;
import yixian.website.dto.request.UserSearchRequest;
import yixian.website.dto.response.PageResponse;
import yixian.website.dto.response.UserResponse;
import yixian.website.mapper.UserMapper;
import yixian.website.service.UserService;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @PostMapping("/user")
    public Result<Void> saveUser(@RequestBody AdminUserSaveRequest request) {
        userService.saveByAdmin(request);
        return Result.ok(SuccessMessages.SAVE_OK);
    }

    @GetMapping("/user")
    public Result<PageResponse<UserResponse>> findUsers(@ModelAttribute PageParams page) {
        return Result.ok(PageResponse.from(userService.findAll(page.toPageable()), UserMapper::toResponse), SuccessMessages.GET_OK);
    }

    @GetMapping("/user/search")
    public Result<PageResponse<UserResponse>> searchUsers(@ModelAttribute UserSearchRequest search,
                                                          @ModelAttribute PageParams page) {
        return Result.ok(PageResponse.from(userService.search(search, page.toPageable()), UserMapper::toResponse), SuccessMessages.GET_OK);
    }

    @GetMapping("/user/search/id/{id}")
    public Result<UserResponse> findUserById(@PathVariable Integer id) {
        return Result.ok(UserMapper.toResponse(userService.findById(id)), SuccessMessages.GET_OK);
    }
}
