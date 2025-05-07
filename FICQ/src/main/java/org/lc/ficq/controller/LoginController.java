package org.lc.ficq.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lc.ficq.dto.LoginDTO;
import org.lc.ficq.dto.ModifyPwdDTO;
import org.lc.ficq.dto.RegisterDTO;
import org.lc.ficq.result.Result;
import org.lc.ficq.result.ResultUtils;
import org.lc.ficq.service.UserService;
import org.lc.ficq.vo.LoginVO;
import org.springframework.web.bind.annotation.*;

@Tag(name = "注册登录")
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return ResultUtils.success(userService.login(dto));
    }

    @PutMapping("/refreshToken")
    @Operation(summary = "刷新token", description = "用refreshtoken换取新的token")
    public Result<LoginVO> refreshToken(@RequestHeader("refreshToken") String refreshToken) {
        return ResultUtils.success(userService.refreshToken(refreshToken));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册")
    public Result register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return ResultUtils.success();
    }

    @PutMapping("/modifyPwd")
    @Operation(summary = "修改密码", description = "修改用户密码")
    public Result modifyPassword(@Valid @RequestBody ModifyPwdDTO dto) {
        userService.modifyPassword(dto);
        return ResultUtils.success();
    }

}
