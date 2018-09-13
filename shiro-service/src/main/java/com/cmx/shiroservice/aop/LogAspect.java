package com.cmx.shiroservice.aop;


import com.alibaba.dubbo.rpc.RpcContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

//@Aspect
//@Component
public class LogAspect {

    /**
     * 拦截Logger下面所有的方法
     */
    @Pointcut("execution (* com.cmx.shiroservice.controller..*.*(..))" +
            "|| execution(* com.cmx.shiroservice.service..*.*(..))")
    private void pointcut() {
    }

    @Before(value = "pointcut()")
    public void before(JoinPoint joinPoint){
        Class clazz = joinPoint.getTarget().getClass();
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        System.out.println("log AspectJ class ->  " + clazz.getName() + "   Aspectj Method -> " +  method.getName()) ;
        System.out.println(RpcContext.getContext().getRemoteHostName());
        System.out.println(RpcContext.getContext().getMethodName());
    }


    @AfterReturning(value = "pointcut()", returning = "returnVal")
    public void afterR(JoinPoint joinPoint, Object returnVal){
        Class clazz = joinPoint.getTarget().getClass();
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        System.out.println("do this method : " + method.getName() + "returnVal : " + returnVal);
    }

    @AfterThrowing(value = "pointcut()", throwing = "error")
    public void afterT(JoinPoint joinPoint, Throwable error){
        System.out.println("Aspectj throws Exception" + error);
    }

}
