package com.chenzhihao.shopcommon.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 后
 * @author 端统一返回结果
 */
@Data
public class Result<T> implements Serializable {
    /**
     *编码：1成功，0和其它数字为失败
     */
    private Integer code;
    /**
     *错误信息
     */
    private String msg;
    /**
     *数据
     */
    private T data;

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.msg = "success";
        result.code = 1;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.msg = "success";
        result.code = 1;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<T>();
        result.msg = msg;
        result.code = 0;
        return result;
    }

}