package com.lostandfound.lostandfound.user.service.impl;

import com.lostandfound.lostandfound.common.Result;
import com.lostandfound.lostandfound.user.dto.UserLoginDTO;
import com.lostandfound.lostandfound.user.dto.UserRegisterDTO;
import com.lostandfound.lostandfound.user.entity.User;
import com.lostandfound.lostandfound.user.mapper.UserMapper;
import com.lostandfound.lostandfound.user.service.UserService;
import com.lostandfound.lostandfound.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public Result<UserVO> login(UserLoginDTO dto) {
        User user = userMapper.findByStudentIdAndPassword(
                dto.getStudentId(), dto.getPassword()
        );
        if (user == null) {
            return Result.fail("学号或密码错误");
        }
        return Result.success(toVO(user));
    }

    @Override
    public Result<UserVO> register(UserRegisterDTO dto) {
        long count = userMapper.countByStudentId(dto.getStudentId());
        if (count > 0) {
            return Result.fail("该学号已注册");
        }
        User user = new User();
        user.setStudentId(dto.getStudentId());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole("student");
        userMapper.insert(user);
        return Result.success(toVO(user));
    }

    @Override
    public Result<UserVO> getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        return Result.success(toVO(user));
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setStudentId(user.getStudentId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setRole(user.getRole());
        vo.setCreatedAt(user.getCreatedAt());
        return vo;
    }
}