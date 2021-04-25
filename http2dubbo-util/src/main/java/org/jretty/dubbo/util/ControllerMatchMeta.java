package org.jretty.dubbo.util;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.jretty.util.match.ZolltyPathMatcher;

/**
 * 
 * @author zollty
 * @since 2021年4月22日
 */
public class ControllerMatchMeta {
    
    Object instance;
    
    String classUrl;
    
    List<MethodMatchMeta> methodMeta = new LinkedList<>();
    
    public class MethodMatchMeta {
        Method method;
        String methodUrl;
        String methodType;
        ZolltyPathMatcher mather;
    }
    
}
