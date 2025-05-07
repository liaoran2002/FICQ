package org.lc.ficq.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lc.ficq.dto.PageQueryDTO;
import org.lc.ficq.result.Result;
import org.lc.ficq.result.ResultUtils;
import org.lc.ficq.service.GroupService;
import org.lc.ficq.service.UserService;
import org.lc.ficq.vo.GroupVO;
import org.lc.ficq.vo.ListResultVO;
import org.lc.ficq.vo.UserVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final GroupService groupService;

    @PostMapping("/userList")
    @Operation(summary = "用户列表", description = "获取用户列表")
    public Result<ListResultVO<UserVO>> findUserList(@Valid @RequestBody PageQueryDTO dto) {
        return ResultUtils.success(userService.findUserList(dto));
    }
    @PostMapping("/groupList")
    @Operation(summary = "群组列表", description = "获取群组列表")
    public Result<ListResultVO<GroupVO>> findGroupList(@Valid @RequestBody PageQueryDTO dto) {
        return ResultUtils.success(groupService.findGroupList(dto));
    }
}
