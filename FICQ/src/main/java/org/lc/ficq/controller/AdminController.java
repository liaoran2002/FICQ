package org.lc.ficq.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lc.ficq.dto.PageQueryDTO;
import org.lc.ficq.result.Result;
import org.lc.ficq.result.ResultUtils;
import org.lc.ficq.service.*;
import org.lc.ficq.vo.*;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final GroupService groupService;
    private final SensitiveWordService sensitiveWordService;
    private final PrivateMessageService privateMessageService;
    private final GroupMessageService groupMessageService;

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

    @PostMapping("/sensitiveWordList")
    @Operation(summary = "敏感词列表", description = "获取敏感词列表")
    public Result<ListResultVO<SensitiveWordVO>> findSensitiveWordList(@Valid @RequestBody PageQueryDTO dto) {
        return ResultUtils.success(sensitiveWordService.findSensitiveWordList(dto));
    }

    @PostMapping("/privateSensitiveWordHit")
    @Operation(summary = "用户消息列表", description = "获取私聊消息敏感词命中")
    public Result<ListResultVO<PrivateMessageVO>> findPrivateSensitiveWordHit(@Valid @RequestBody PageQueryDTO dto) {
        return ResultUtils.success(privateMessageService.findSensitiveWordHit(dto));
    }

    @PostMapping("/groupSensitiveWordHit")
    @Operation(summary = "群聊消息列表", description = "获取群聊消息敏感词命中")
    public Result<ListResultVO<GroupMessageVO>> findGroupSensitiveWordHit(@Valid @RequestBody PageQueryDTO dto) {
        return ResultUtils.success(groupMessageService.findSensitiveWordHit(dto));
    }

    @PostMapping("/privateMessageList")
    @Operation(summary = "用户消息列表", description = "获取私聊消息列表")
    public Result<ListResultVO<PrivateMessageVO>> findUserMessageList(@Valid @RequestBody PageQueryDTO dto) {
        return ResultUtils.success(privateMessageService.findMessageList(dto));
    }

    @PostMapping("/groupMessageList")
    @Operation(summary = "群聊消息列表", description = "获取群聊消息列表")
    public Result<ListResultVO<GroupMessageVO>> findGroupMessageList(@Valid @RequestBody PageQueryDTO dto) {
        return ResultUtils.success(groupMessageService.findMessageList(dto));
    }

    @PostMapping("findUser")
    @Operation(summary = "搜索用户列表", description = "获取搜索用户列表")
    public Result<ListResultVO<UserVO>> findUser(@Valid @RequestBody PageQueryDTO dto) {
        return ResultUtils.success(userService.findUser(dto));
    }

}
