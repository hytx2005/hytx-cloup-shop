package com.chenzhihao.shopcommon.advice;

import com.chenzhihao.shopcommon.exception.BaseException;
import com.chenzhihao.shopcommon.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常捕获处理器
 * @author dhx
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandle {
    @ExceptionHandler
    public Result<String> exceptionHandle(BaseException e) {
        log.error("异常信息：{}",e.getMessage());
        return Result.error(e.getMessage());
    }
}
