package com.chenzhihao.users.domain.dto;

import lombok.Data;

/**
 * 用户登录表单实体类
 * @author dhx
 */
@Data
public class UserLoginDto {

    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
}
