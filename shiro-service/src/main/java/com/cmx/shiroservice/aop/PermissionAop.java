package com.cmx.shiroservice.aop;


import com.cmx.shiroservice.annotation.AdminPermission;
import lombok.val;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

//@Aspect
//@Component
public class PermissionAop {

    @Pointcut(value = "@annotation(com.cmx.shiroservice.annotation.AdminPermission)")
    private void pointcut() {

    }


    @Before(value = "pointcut() && @annotation(adminPermission)")
    public void before(JoinPoint point, AdminPermission adminPermission){
        Class clazz = point.getTarget().getClass();
        Method method = ((MethodSignature)point.getSignature()).getMethod();
        Subject subject = SecurityUtils.getSubject();
        System.out.println("get principal from subject: " +  subject.getPrincipal());
        System.out.println(subject.hasRole("admin"));
    }

    @AfterReturning(value = "pointcut() && @annotation(adminPermission)", returning = "val")
    public void afterReturning(JoinPoint point, AdminPermission adminPermission,Object val){

    }

    @Around(value = "pointcut()")
    public void around(){}


}
