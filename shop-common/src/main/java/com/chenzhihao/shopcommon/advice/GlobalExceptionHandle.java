package com.chenzhihao.shopcommon.advice;

import com.chenzhihao.shopcommon.exception.BaseException;
import com.chenzhihao.shopcommon.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * 统一处理应用中的业务异常
 *
 * 功能：
 * - 捕获BaseException业务异常
 * - 记录异常日志
 * - 返回统一的错误响应格式
 *
 * @author dhx
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandle {
    @ExceptionHandler(BaseException.class)
    public Result<String> exceptionHandle(BaseException e) {
        log.error("异常信息：{}",e.getMessage());
        return Result.error(e.getMessage());
    }
}
