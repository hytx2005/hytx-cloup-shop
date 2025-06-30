package com.chenzhihao.shopcommon.exception;

/**
 * @author 异常类，继承RuntimeException，用于抛出模块的异常
 */
public class BaseException extends RuntimeException{

    public BaseException(String message) {
        super(message);
    }

    public BaseException(){

    }
}
