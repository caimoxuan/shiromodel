package com.cmx.shiroservice.util;

import com.cmx.shiroservice.domain.InvokeLog;
import org.slf4j.Logger;

public class LogUtils {


    public static void debug(Logger log, String message, Object... params){
        if(log.isDebugEnabled()){
            InvokeLog invokeLog = new InvokeLog();
            invokeLog.setContent(message);
            log.debug(invokeLog.toString(), params);
        }
    }

}
