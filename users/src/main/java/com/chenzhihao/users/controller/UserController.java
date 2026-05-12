package com.chenzhihao.users.controller;

import com.chenzhihao.shopcommon.result.Result;
import com.chenzhihao.shopcommon.util.JwtUtils;
import com.chenzhihao.users.domain.dto.UserLoginDto;
import com.chenzhihao.users.domain.dto.UserRegisterDto;
import com.chenzhihao.users.domain.vo.UserLoginVo;
import com.chenzhihao.users.properties.JwtProperties;
import com.chenzhihao.users.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 用户模块前端控制器
 *
 * 提供用户相关的API接口
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


    /**
     * 注册接口
     * @param userRegisterDto 用户注册信息
     * @return {@link Result }<{@link ? }>
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody UserRegisterDto userRegisterDto) {
        userService.registerUser(userRegisterDto);
        return Result.success("注册成功");
    }

    /**
     * 用户登录接口
     * 登录成功后返回token令牌 ，token令牌名称就叫token
     * @param userLoginDto 用户登录信息
     * @return {@link Result }<{@link UserLoginVo }>
     */
    @GetMapping("/login")
    public Result<UserLoginVo> login(@RequestBody UserLoginDto userLoginDto){
        return Result.success(userService.loginUser(userLoginDto));
    }


    /**
     * 测试token令牌有效接口
     * @param string 令牌,就叫token
     * @return {@link Result }<{@link Object }>
     */
    @GetMapping("/testToken")
    public Result<Object> test(@RequestParam("token") String string){
        Long l = JwtUtils.parseToken(jwtProperties.getSecretKey(), string);
        return Result.success(l);
    }

    /**
     * 测试是否记录了userId
     * @param request 请求
     * @return {@link Result }<{@link Integer }>
     */
    @GetMapping("/test")
    public Result<Integer> test1(HttpServletRequest request){
        String header = request.getHeader(jwtProperties.getHeaderName());
        int userId = Integer.parseInt(header);
        userId += 100;
        System.out.println("测试");
        return Result.success(userId);
    }
}
