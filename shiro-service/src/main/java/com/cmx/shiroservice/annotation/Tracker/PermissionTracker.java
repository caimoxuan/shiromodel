package com.cmx.shiroservice.annotation.Tracker;

import com.cmx.shiroservice.annotation.AdminPermission;

import java.lang.reflect.Method;

public class PermissionTracker {


    public static void track(Class<?> clazz){
        Method[] methods = clazz.getMethods();
        for(Method m : methods){
            AdminPermission adminPermission = m.getAnnotation(AdminPermission.class);
            if(adminPermission != null){
                System.out.println("user should be admin!");
            }
        }
    }
}
