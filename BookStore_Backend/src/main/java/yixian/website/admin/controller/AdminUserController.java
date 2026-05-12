package yixian.website.admin.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import yixian.website.common.SuccessMessages;
import yixian.website.dto.PageRequestDto;
import yixian.website.dto.SearchUserRequestDto;
import yixian.website.model.Users;
import yixian.website.service.UserManagementService;
import yixian.website.service.query.UserQueryService;
import yixian.website.common.Result;

@RestController
@RequestMapping("/admin")
public class AdminUserController {

    private final UserManagementService userManagementService;
    private final UserQueryService userQueryService;


    public AdminUserController(UserManagementService userManagementService, UserQueryService userQueryService) {

        this.userManagementService = userManagementService;
        this.userQueryService = userQueryService;
    }


    @PostMapping("/user")
    public Result saveUser(@RequestBody Users user) {
        return userManagementService.saveOrUpdateUser(user);
    }


    @GetMapping("/user")
    public Result findUsersByPage(PageRequestDto pageRequestDto) {
        Page<Users> users = userQueryService.findUsersByPage(pageRequestDto);
        return Result.getSuccess(users, SuccessMessages.GET_OK);

    }

    @GetMapping("/user/search")
    public Result searchUsers(@ModelAttribute SearchUserRequestDto searchUserRequestDto,
                              @ModelAttribute PageRequestDto pageRequestDto) {
        Page<Users>users=userQueryService.getUserByParams(searchUserRequestDto, pageRequestDto);
        return Result.getSuccess(users, SuccessMessages.GET_OK);
    }

    @GetMapping("/user/search/id/{id}")
    public Result findUserById(@PathVariable Integer id) {
        Users user = userQueryService.findUserById(id);

        return Result.getSuccess(user, SuccessMessages.GET_OK);
    }


}
