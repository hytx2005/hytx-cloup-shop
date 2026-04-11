package com.chenzhihao.shopcommon.advice;

import com.chenzhihao.shopcommon.annotation.DubboException;
import com.chenzhihao.shopcommon.result.Result;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class DubboAop {

    @Pointcut("@annotation(com.chenzhihao.shopcommon.annotation.DubboException)")
    public void dubboService() {

    }

    @Around("dubboService()")
    public Object around(ProceedingJoinPoint pjp){
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        try {
            return pjp.proceed();
        }
        catch (RuntimeException e){
            DubboException dubboServiceAop = method.getAnnotation(DubboException.class);
            if (dubboServiceAop != null) {
                // 获取 message 属性值
                String message = dubboServiceAop.message();
                if (!message.isEmpty()){
                    return Result.error(message);
                }
                return Result.error(e.getMessage());
            }
            else {
                return Result.error(e.getMessage());
            }
        }
        catch (Throwable e){
            return Result.error(e.getMessage());
        }
    }
}
