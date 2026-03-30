package com.example.demo.mapper;

import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    // 根据账号查询用户
    @Select("select * from user where username = #{username}")
    User selectByUsername(String username);

    // 注册用户
    @Insert("insert into user(username, password) values(#{username}, #{password})")
    int insert(User user);
}