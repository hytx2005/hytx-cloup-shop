package com.chenzhihao.users.domain.vo;


import lombok.Data;

/**
 * 用户登录返回实体类
 * @author dhx
 */
@Data
public class UserLoginVo {
    /**
     * 用户名
     */
    private String username;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * token令牌
     */
    private String token;
}
