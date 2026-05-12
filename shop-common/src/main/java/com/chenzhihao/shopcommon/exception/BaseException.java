package com.chenzhihao.shopcommon.exception;

/**
 * 基础异常类
 *
 * 应用自定义异常的基类
 * 继承RuntimeException，无需强制捕获
 *
 * 使用场景：
 * - 业务逻辑异常（订单不存在、库存不足等）
 * - 参数校验异常
 * - 权限异常
 *
 * @author dhx
 */
public class BaseException extends RuntimeException{

    public BaseException(String message) {
        super(message);
    }

    public BaseException(){

    }
}
