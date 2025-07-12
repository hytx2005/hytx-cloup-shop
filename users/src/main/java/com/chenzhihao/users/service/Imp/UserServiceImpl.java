package com.chenzhihao.users.service.imp;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chenzhihao.shopcommon.exception.ParamEmptyException;
import com.chenzhihao.shopcommon.util.JwtUtils;
import com.chenzhihao.shopcommon.util.PasswordUtil;
import com.chenzhihao.users.domain.dto.UserLoginDto;
import com.chenzhihao.users.domain.dto.UserRegisterDto;
import com.chenzhihao.users.domain.po.User;
import com.chenzhihao.users.domain.vo.UserLoginVo;
import com.chenzhihao.users.mapper.UserMapper;
import com.chenzhihao.users.properties.JwtProperties;
import com.chenzhihao.users.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户模块 服务实现类
 * </p>
 *
 * @author hqh
 * @since 2025-06-27
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录用户
     *
     * @param userLoginDto 登录信息
     * @return {@link UserLoginVo }
     */
    @Override
    public UserLoginVo loginUser(UserLoginDto userLoginDto) {
        String username = userLoginDto.getUsername();
        String password = userLoginDto.getPassword();
        // 参数校验
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            throw new ParamEmptyException("用户名或密码为空");
        }
        String password1 = userMapper.getPassword(username);
        if (!PasswordUtil.checkPassword(password, password1)) {
            throw new ParamEmptyException("用户名或密码错误");
        }
        User login = userMapper.login(username);

        if (login == null) {
            throw new ParamEmptyException("用户名或密码错误");
        }

        // 生成 token令牌
        String token = JwtUtils.createToken(jwtProperties.getSecretKey(), jwtProperties.getUserTtl(),login.getId());

        // 设置返回结果
        UserLoginVo userLoginVo = new UserLoginVo();
        userLoginVo.setUsername(username);
        userLoginVo.setPhone(login.getPhone());
        userLoginVo.setToken(token);
        return userLoginVo;
    }

    /**
     * 注册用户
     *
     * @param userRegisterDto 注册信息
     */
    @Override
    public void registerUser(UserRegisterDto userRegisterDto) {
        String userName = userRegisterDto.getUsername();
        String password = userRegisterDto.getPassword();
        String phone = userRegisterDto.getPhone();

        if (StrUtil.isBlank(userName) || StrUtil.isBlank(password) || StrUtil.isBlank(phone)) {
            throw new ParamEmptyException("用户名、密码或手机号不能为空");
        }

        // 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName);
        User existingUser = userMapper.selectOne(queryWrapper);
        if (existingUser != null) {
            throw new ParamEmptyException("用户名已存在");
        }

        // 加密密码
        String encodedPassword = PasswordUtil.hashPassword(password);

        User user = User.builder()
                .userName(userName)
                .password(encodedPassword)
                .phone(phone)
                .userStatus("1")
                .build();

        save(user);
    }

}
