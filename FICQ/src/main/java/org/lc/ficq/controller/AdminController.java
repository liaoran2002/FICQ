package org.lc.ficq.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.lc.ficq.result.Result;
import org.lc.ficq.result.ResultUtils;
import org.lc.ficq.service.GroupService;
import org.lc.ficq.service.UserService;
import org.lc.ficq.vo.FriendVO;
import org.lc.ficq.vo.UserVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "管理")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final GroupService groupService;

    @GetMapping("/userList")
    @Operation(summary = "用户列表", description = "获取用户列表")
    public Result<List<UserVO>> findUserList() {
        return ResultUtils.success(userService.findUserList());
    }
}
