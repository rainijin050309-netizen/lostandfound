package com.lostandfound.lostandfound.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lostandfound.lostandfound.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    User findByStudentIdAndPassword(
            @Param("studentId") String studentId,
            @Param("password") String password
    );

    User findByStudentId(@Param("studentId") String studentId);

    long countByStudentId(@Param("studentId") String studentId);
}