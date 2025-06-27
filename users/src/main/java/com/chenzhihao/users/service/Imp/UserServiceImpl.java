package com.chenzhihao.users.service.imp;

import com.chenzhihao.users.domain.po.User;
import com.chenzhihao.users.mapper.UserMapper;
import com.chenzhihao.users.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
