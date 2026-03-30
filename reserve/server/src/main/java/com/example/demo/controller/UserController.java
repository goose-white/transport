package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.common.Result;
import com.example.demo.common.Constants;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户登录/注册控制器（取消密码加密）
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", maxAge = 3600) // 解决跨域
public class UserController {
    @Resource
    private UserMapper userMapper;

    // 登录（取消密码加密，新增返回用户ID）
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        // 1. 参数校验
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return Result.error(Constants.CODE_400, "账号不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return Result.error(Constants.CODE_400, "密码不能为空");
        }

        try {
            // 2. 查询数据库用户
            User dbUser = userMapper.selectByUsername(user.getUsername().trim());
            if (dbUser == null) {
                return Result.error(Constants.CODE_400, "账号不存在");
            }

            // 3. 直接比对明文密码（取消加密）
            if (!dbUser.getPassword().equals(user.getPassword().trim())) {
                return Result.error(Constants.CODE_400, "密码错误");
            }

            // 4. 登录成功，返回token + username + id（工号）
            Map<String, Object> data = new HashMap<>();
            data.put("token", "demo_token_" + dbUser.getId() + "_" + System.currentTimeMillis());
            data.put("username", dbUser.getUsername()); // 登录人员姓名（注册账号）
            data.put("id", dbUser.getId()); // 工号（对应用户ID）
            return Result.success("登录成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(Constants.CODE_500, "登录失败：" + e.getMessage());
        }
    }

    // 注册（取消密码加密）
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        // 1. 参数校验
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return Result.error(Constants.CODE_400, "账号不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return Result.error(Constants.CODE_400, "密码不能为空");
        }
        if (user.getPassword().length() < 6) {
            return Result.error(Constants.CODE_400, "密码长度不能少于6位");
        }

        try {
            // 2. 检查账号是否已存在
            User dbUser = userMapper.selectByUsername(user.getUsername().trim());
            if (dbUser != null) {
                return Result.error(Constants.CODE_400, "账号已存在");
            }

            // 3. 直接存储明文密码（取消加密）
            user.setPassword(user.getPassword().trim()); // 仅去空格，不加密
            int count = userMapper.insert(user);

            if (count > 0) {
                return Result.success("注册成功，请登录");
            } else {
                return Result.error(Constants.CODE_500, "注册失败：数据库插入失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(Constants.CODE_500, "注册失败：" + e.getMessage());
        }
    }
}