package com.lostandfound.lostandfound.user.controller;

import com.lostandfound.lostandfound.common.Result;
import com.lostandfound.lostandfound.user.dto.UserLoginDTO;
import com.lostandfound.lostandfound.user.dto.UserRegisterDTO;
import com.lostandfound.lostandfound.user.service.UserService;
import com.lostandfound.lostandfound.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody UserLoginDTO dto) {
        return userService.login(dto);
    }

    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody UserRegisterDTO dto) {
        return userService.register(dto);
    }

    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
