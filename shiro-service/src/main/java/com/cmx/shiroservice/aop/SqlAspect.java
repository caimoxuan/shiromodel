package com.cmx.shiroservice.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Aspect
@Order(-1)
@Component
public class SqlAspect {

    /**
     * 拦截Logger下面所有的方法
     */
    @Pointcut("execution(* com.cmx.shiroservice.mapper.*.*(..))")
    private void pointcut() {
    }

    private long warnWhenOverTime = 2 * 60 * 1000L;

    @Around("pointcut()")
    public Object logSqlExecutionTime(ProceedingJoinPoint joinPoint)
            throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long costTime = System.currentTimeMillis() - startTime;

        StringBuilder sb = new StringBuilder();
        sb.append("execute method :").append(joinPoint.getSignature());
        sb.append("args: ").append(arrayToString(joinPoint.getArgs()));
        sb.append(" cost time[").append(costTime).append("]ms");
        System.out.println(sb);

        return result;
    }

    private static String arrayToString(Object[] a) {
        if (a == null) {
            return "null";
        }

        int iMax = a.length - 1;
        if (iMax == -1) {
            return "[]";
        }

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0;; i++) {
            if (a[i] instanceof Object[]) {
                b.append(arrayToString((Object[]) a[i]));
            } else {
                b.append(String.valueOf(a[i]));
            }
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append(", ");
        }
    }


}
