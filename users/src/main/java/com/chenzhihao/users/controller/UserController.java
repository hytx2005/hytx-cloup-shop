package com.chenzhihao.users.controller;


import com.chenzhihao.shopcommon.exception.BaseException;
import com.chenzhihao.shopcommon.result.Result;
import com.chenzhihao.shopcommon.util.JwtUtils;
import com.chenzhihao.users.domain.dto.UserLoginDto;
import com.chenzhihao.users.domain.dto.UserRegisterDto;
import com.chenzhihao.users.domain.po.User;
import com.chenzhihao.users.domain.vo.UserLoginVo;
import com.chenzhihao.users.properties.JwtProperties;
import com.chenzhihao.users.service.IUserService;
import org.apache.dubbo.common.logger.FluentLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.chenzhihao.users.domain.po.User;
import com.chenzhihao.users.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户模块 前端控制器
 * </p>
 *
 * @author hqh
 * @since 2025-06-27
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private JwtProperties jwtProperties;


    @PostMapping("/register")
    public Result<?> register(@RequestBody UserRegisterDto userRegisterDto) {
        userService.registerUser(userRegisterDto);
        return Result.success("注册成功");
    }

    @GetMapping("/login")
    public Result<UserLoginVo> login(@RequestBody UserLoginDto userLoginDto){
        return Result.success(userService.loginUser(userLoginDto));
    }


    /**
     * 测试令牌有效接口
     * @param string 令牌
     * @return {@link Result }<{@link Object }>
     */
    @GetMapping("/testToken")
    public Result<Object> test(@RequestParam("token") String string){
        Long l = JwtUtils.parseToken(jwtProperties.getSecretKey(), string);
        return Result.success(l);
    }

    @GetMapping("/test")
    public Result<Integer> test1(HttpServletRequest request){
        String header = request.getHeader(jwtProperties.getHeaderName());
        int userId = Integer.parseInt(header);
        userId += 100;
        System.out.println("测试");
        return Result.success(userId);
    }
}
