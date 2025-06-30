package com.chenzhihao.shopcommon.exception;

/**
 * 未查询到该id的商品错误
 * @author dhx
 */
public class ProductionNoIDException extends BaseException{
    public ProductionNoIDException() {
        super("未查询到指定的商品");
    }
    public ProductionNoIDException(String message) {
        super(message);
    }
}
