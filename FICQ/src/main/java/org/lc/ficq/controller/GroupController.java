package org.lc.ficq.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.lc.ficq.annotation.RepeatSubmit;
import org.lc.ficq.result.Result;
import org.lc.ficq.result.ResultUtils;
import org.lc.ficq.service.GroupService;
import org.lc.ficq.vo.GroupInviteVO;
import org.lc.ficq.vo.GroupMemberVO;
import org.lc.ficq.vo.GroupVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "群聊")
@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @RepeatSubmit
    @Operation(summary = "创建群聊", description = "创建群聊")
    @PostMapping("/create")
    public Result<GroupVO> createGroup(@Valid @RequestBody GroupVO vo) {
        return ResultUtils.success(groupService.createGroup(vo));
    }

    @RepeatSubmit
    @Operation(summary = "修改群聊信息", description = "修改群聊信息")
    @PutMapping("/modify")
    public Result<GroupVO> modifyGroup(@Valid @RequestBody GroupVO vo) {
        return ResultUtils.success(groupService.modifyGroup(vo));
    }

    @RepeatSubmit
    @Operation(summary = "解散群聊", description = "解散群聊")
    @DeleteMapping("/delete/{groupId}")
    public Result deleteGroup(@NotNull(message = "群聊id不能为空") @PathVariable Long groupId) {
        groupService.deleteGroup(groupId);
        return ResultUtils.success();
    }


    @Operation(summary = "查询群聊", description = "查询单个群聊信息")
    @GetMapping("/find/{groupId}")
    public Result<GroupVO> findGroup(@NotNull(message = "群聊id不能为空") @PathVariable Long groupId) {
        return ResultUtils.success(groupService.findById(groupId));
    }

    @Operation(summary = "查询群聊列表", description = "查询群聊列表")
    @GetMapping("/list")
    public Result<List<GroupVO>> findGroups() {
        return ResultUtils.success(groupService.findGroups());
    }
    
    @RepeatSubmit
    @Operation(summary = "邀请进群", description = "邀请好友进群")
    @PostMapping("/invite")
    public Result invite(@Valid @RequestBody GroupInviteVO vo) {
        groupService.invite(vo);
        return ResultUtils.success();
    }

    @Operation(summary = "查询群聊成员", description = "查询群聊成员")
    @GetMapping("/members/{groupId}")
    public Result<List<GroupMemberVO>> findGroupMembers(
        @NotNull(message = "群聊id不能为空") @PathVariable Long groupId) {
        return ResultUtils.success(groupService.findGroupMembers(groupId));
    }

    @RepeatSubmit
    @Operation(summary = "退出群聊", description = "退出群聊")
    @DeleteMapping("/quit/{groupId}")
    public Result quitGroup(@NotNull(message = "群聊id不能为空") @PathVariable Long groupId) {
        groupService.quitGroup(groupId);
        return ResultUtils.success();
    }

    @RepeatSubmit
    @Operation(summary = "踢出群聊", description = "将用户踢出群聊")
    @DeleteMapping("/kick/{groupId}")
    public Result kickGroup(@NotNull(message = "群聊id不能为空") @PathVariable Long groupId,
        @NotNull(message = "用户id不能为空") @RequestParam Long userId) {
        groupService.kickGroup(groupId, userId);
        return ResultUtils.success();
    }

}

