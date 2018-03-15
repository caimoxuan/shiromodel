package com.cmx.shiroservice.util;


import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * 网页请求工具
 */
public class WebUtils {

    /**
     * 判断是否是ajax请求
     * @param request
     * @return
     */
    public static boolean  isAjax(ServletRequest request){
//        String header = ((HttpServletRequest)request).getHeader("x-requested-with");
//        String header1 = ((HttpServletRequest)request).getHeader("Access-Control-Request-Headers");
//        if("XMLHttpRequest".equalsIgnoreCase(header) || "x-requested-with".equalsIgnoreCase(header1)){
//            return true;
//        }
        return false;
    }

}
