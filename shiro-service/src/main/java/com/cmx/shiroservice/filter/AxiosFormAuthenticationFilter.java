package com.cmx.shiroservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 如果要实现验证码登陆的话， 可以再onAccessDenied方法中添加逻辑
 */
@Slf4j
public class AxiosFormAuthenticationFilter extends FormAuthenticationFilter {


    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        log.info("formLogin onAccessDenied");
        Subject subject = SecurityUtils.getSubject();
        if(subject.hasRole("admin")){
            log.info("subject has role admin");
        }else{
            log.info("subject is anon");
        }
        return super.onAccessDenied(request, response);
    }
}
