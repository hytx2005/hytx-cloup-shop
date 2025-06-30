package com.chenzhihao.users.mapper;

import com.chenzhihao.users.domain.po.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 用户模块 Mapper 接口
 * </p>
 *
 * @author hqh
 * @since 2025-06-27
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 用户登录
     * @param username 用户名
     * @param password  密码
     * @return {@link User }
     */
    @Select("select * from user where user_name = #{username} and password = #{password}")
    User login(String username, String password);
}
