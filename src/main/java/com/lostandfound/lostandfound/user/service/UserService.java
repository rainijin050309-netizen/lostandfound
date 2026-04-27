package com.lostandfound.lostandfound.user.service;

import com.lostandfound.lostandfound.common.Result;
import com.lostandfound.lostandfound.user.dto.UserLoginDTO;
import com.lostandfound.lostandfound.user.dto.UserRegisterDTO;
import com.lostandfound.lostandfound.user.vo.UserVO;

public interface UserService {
    Result<UserVO> login(UserLoginDTO dto);
    Result<UserVO> register(UserRegisterDTO dto);
    Result<UserVO> getUserById(Long id);
}