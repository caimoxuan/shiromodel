package com.cmx.shiroservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于再访问controller之前判断用户是否登陆
 * 否 ajax? 返回 json
 * 是 不处理
 */
@Slf4j
public class ShiroAxiosFilter extends AccessControlFilter {

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) {
        log.info("start user filter from axios");
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated()){
            if(subject.hasRole("admin")){
                log.info("has role");
            }else{
                log.info("has no role");
            }
            return true;
        }else{
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        log.info("isAccessAllowd");
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        log.info("onAccessDenied");
        return false;
    }
}
