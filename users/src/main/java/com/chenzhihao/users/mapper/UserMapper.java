package com.chenzhihao.users.mapper;

import com.chenzhihao.users.domain.po.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
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
     * @return {@link User }
     */
    @Select("select * from user where user_name = #{username}")
    User login(String username);

    /**
     * 得到用户的密码
     * @param username
     * @return {@link String }
     */
    @Select("select password from user where user_name = #{username}")
    String getPassword(@Param("username") String username);

}
