package com.chenzhihao.users.service;

import com.chenzhihao.users.domain.dto.UserLoginDto;
import com.chenzhihao.users.domain.dto.UserRegisterDto;
import com.chenzhihao.users.domain.po.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chenzhihao.users.domain.vo.UserLoginVo;

/**
 * <p>
 * 用户模块 服务类
 * </p>
 *
 * @author hqh
 * @since 2025-06-27
 */
public interface IUserService extends IService<User> {

    /**
     * 用户登录
     * @param loginDto 登录信息
     * @return {@link User }
     */
    UserLoginVo loginUser(UserLoginDto loginDto);

    /**
     * 用户注册
     * @param userRegisterDto 注册信息
     */
    void registerUser(UserRegisterDto userRegisterDto);
}
