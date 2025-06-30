package com.chenzhihao.shopcommon.exception;

/**
 * 参数为空错误
 * @author dhx
 */
public class ParamEmptyException extends BaseException{
    public ParamEmptyException() {

    }
    public ParamEmptyException(String message) {
        super(message);
    }
}
