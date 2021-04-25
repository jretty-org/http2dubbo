package org.jretty.dubbo.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jretty.dubbo.dto.HttpRequestDto;
import org.jretty.util.NestedRuntimeException;
import org.jretty.util.PathUtils;
import org.jretty.util.ReflectionUtils;
import org.jretty.util.StringUtils;
import org.jretty.util.match.ZolltyPathMatcher;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ValueConstants;

/**
 * 根据Controller注解信息查找匹配，并调用Controller方法
 * 
 * @author zollty
 * @since 2021年4月13日
 */
public class SpringControllerInvoke {
    protected List<ControllerMatchMeta> list;
    
    public SpringControllerInvoke(ApplicationContext applicationContext) {
        parseControllerInfo(applicationContext);
    }
    
    protected void parseControllerInfo(ApplicationContext applicationContext) {
        Map<String, Object> controllerBeans = applicationContext.getBeansWithAnnotation(RestController.class);
        controllerBeans.putAll(applicationContext.getBeansWithAnnotation(Controller.class));
        
        list = new LinkedList<ControllerMatchMeta>();
        
        for(final Map.Entry<String, Object> en: controllerBeans.entrySet()) {
            ControllerMatchMeta ctrl = new ControllerMatchMeta();
            list.add(ctrl);
            // get parent uri
            RequestMapping rm = en.getValue().getClass().getAnnotation(RequestMapping.class);
            String tpath = null;
            if (rm != null) {
                tpath = rm.value()[0];
            }
            if (tpath == null || tpath.length() == 0) {
                tpath = "/";
            } else if (!tpath.startsWith("/")) {
                tpath = "/" + tpath;
            }
            ctrl.classUrl = tpath;
            ctrl.instance = en.getValue();
            
            ReflectionUtils.doWithMethods(en.getValue().getClass(), new ReflectionUtils.MethodCallback() {
                
                @Override
                public void doWith(Method method) {
                    String mtype = getMethodType(method);
                    if (mtype == null) {
                        return;
                    }
                    String[] tp = getMethodUrl(method);
                    if (tp == null) {
                        return;
                    }
                    ControllerMatchMeta.MethodMatchMeta cm = ctrl.new MethodMatchMeta();
                    ctrl.methodMeta.add(cm);
                    cm.method = method;
                    cm.methodType = mtype;
                    if (tp.length > 0) {
                        cm.methodUrl = connectPaths(ctrl.classUrl, tp[0]);
                        if (cm.methodUrl.contains("{}")) {
                            cm.mather = new ZolltyPathMatcher(StringUtils.replaceParams(cm.methodUrl, "*"));
                        }
                    } // else methodUrl = null
                }
            });
        } // end for
        
    }
    
    public Object doInvoke(HttpRequestDto dto) throws Throwable {
        final HttpRequest request = new SimpleHttpRequest(dto);
        final String url = request.getRequestURI();
        final String methodType = request.getMethod();
        
        for (ControllerMatchMeta ctrl : list) {
            if (!url.startsWith(ctrl.classUrl)) {
                continue;
            }
            for (ControllerMatchMeta.MethodMatchMeta cm : ctrl.methodMeta) {
                if (!cm.methodType.equals("ALL") && !cm.methodType.equals(methodType)) {
                    continue;
                }
                if (cm.methodUrl == null || url.equals(cm.methodUrl)) { // url完全匹配
                    Object[] args = getInvokeParams(cm.method, request, null);
                    return invokeMethod(cm.method, ctrl.instance, args);
                } else if (cm.mather != null) { // url有参数：get/{} --- get/101
                    List<String> pathVals = cm.mather.match(url);
                    if (pathVals != null) { // 匹配
                        Object[] args = getInvokeParams(cm.method, request, pathVals);
                        return invokeMethod(cm.method, ctrl.instance, args);
                    }
                }
            }
        }
        throw new NestedRuntimeException("404 url not found"); // not find the matched method
    }
    
    // ~ utils -------------------------------------------
    
    private String[] getMethodUrl(Method method) {
        RequestMapping rm = method.getAnnotation(RequestMapping.class);
        if (rm != null) {
            return rm.value();
        }
        if (method.getAnnotation(PostMapping.class) != null) {
            return method.getAnnotation(PostMapping.class).value();
        }
        if (method.getAnnotation(GetMapping.class) != null) {
            return method.getAnnotation(GetMapping.class).value();
        }
        if (method.getAnnotation(PutMapping.class) != null) {
            return method.getAnnotation(PutMapping.class).value();
        }
        if (method.getAnnotation(DeleteMapping.class) != null) {
            return method.getAnnotation(DeleteMapping.class).value();
        }
        return null;
    }

    private String getMethodType(Method method) {
        RequestMapping rm = method.getAnnotation(RequestMapping.class);
        if (rm != null) {
            if (rm.method().length > 0) {
                return rm.method()[0].name();
            }
            return "ALL";
        }
        if (method.getAnnotation(PostMapping.class) != null) {
            return "POST";
        }
        if (method.getAnnotation(GetMapping.class) != null) {
            return "GET";
        }
        if (method.getAnnotation(PutMapping.class) != null) {
            return "PUT";
        }
        if (method.getAnnotation(DeleteMapping.class) != null) {
            return "DELETE";
        }
        return null;
    }
    
    private static Object invokeMethod(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            return e.getCause(); //new NestedRuntimeException(e.getCause());
        } catch (Exception e) {
            return e; //new NestedRuntimeException(e);
        } catch (ExceptionInInitializerError e) {
            return e.getCause(); //new NestedRuntimeException(e.getCause());
        }
    }
    
    private ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
    /**
     * 标准HTTP数据：
     * 1、UrlParams（Queries）： map[string]string / map[string][]string
     * 2、Body:
     *  FormData（同Queries）: （application/x-www-form-urlencoded）
     *  String: （application/json， application/xml）
     *  file/mix （multipart/form-data）
     *  
     *  暂不支持数组值，只支持map[string]string
     *  不支持file/mix
     *  暂不支持controller泛型request参数，例如：@RequestBody Request<CouponOpenQueryFrom> request
     */
    private Object[] getInvokeParams(Method method, HttpRequest request, List<String> pathVals) {
        Class<?>[] paraTypes = method.getParameterTypes();
        Parameter[] parameters = method.getParameters();
        Annotation[][] annotations = method.getParameterAnnotations();
        Object[] args = new Object[parameters.length];
        for (int i = 0, j = 0; i < parameters.length; i++) {
            Annotation anno = getMethodParamAnnotation(annotations[i]);
            Class<?> paraType = paraTypes[i];
            // 5种值类型(type)：
            // 0 Param： RequestParam 或 省略
            // 1 Header： 从header中拿
            // 2 Attribute：（附加信息）从服务器端拿
            // 3 PathVariable: 从url中拿
            // 4 RequestBody：从body中拿
            if (anno == null || anno.annotationType().equals(RequestParam.class)) {
                String key;
                String defaultVal = null;
                if (anno == null) {
                    key = discoverer.getParameterNames(method)[i];
                } else {
                    RequestParam hparam = (RequestParam) anno;
                    key = hparam.value();
                    if (key.length() == 0) {
                        key = hparam.name();
                    }
                    if (key.length() == 0) {
                        key = discoverer.getParameterNames(method)[i];
                    }
                    if (!hparam.defaultValue().equals(ValueConstants.DEFAULT_NONE)) {
                        defaultVal = hparam.defaultValue();
                    }
                }
                args[i] = getParamValue(key, defaultVal, request, paraType);

            } else if (anno.annotationType().equals(RequestBody.class)) {
                args[i] = getBodyValue(request, paraType);
            } else if (anno.annotationType().equals(PathVariable.class)) {
                args[i] = getPathValue(pathVals, paraType, j++);
            } else if (anno.annotationType().equals(RequestHeader.class)) {
                String key;
                String defaultVal = null;
                RequestHeader hparam = (RequestHeader) anno;
                key = hparam.value();
                if (key.length() == 0) {
                    key = hparam.name();
                }
                if (key.length() == 0) {
                    key = discoverer.getParameterNames(method)[i];
                }
                if (!hparam.defaultValue().equals(ValueConstants.DEFAULT_NONE)) {
                    defaultVal = hparam.defaultValue();
                }
                args[i] = getHeaderValue(key, defaultVal, request, paraType);
            } else if (anno.annotationType().equals(RequestAttribute.class)) {
                String key;
                RequestAttribute hparam = (RequestAttribute) anno;
                key = hparam.value();
                if (key.length() == 0) {
                    key = hparam.name();
                }
                if (key.length() == 0) {
                    key = discoverer.getParameterNames(method)[i];
                }
                args[i] = getAttributeValue(key, request, paraType);
            }
        }
        return args;
    }
    
    private Object getParamValue(String key, String defaultVal, HttpRequest request, Class<?> paraType) {
        if (MvcConvertUtils.canConvert(paraType) != null) {
            // 数据类型为主要类型，可以直接转换，比如int、Long等
            String sval = request.getParameter(key);
            if (sval != null) {
                return MvcConvertUtils.convert(sval, paraType);
            } else if (defaultVal != null) {
                return MvcConvertUtils.convert(defaultVal, paraType);
            }
            return null;
        } else {
            // 数据类型为复合类型，需要调用标准setter赋值
            
            // 请求参数封装到javabean
            Enumeration<String> enumeration = request.getParameterNames();
            
            BeanParamMeta paramMetaInfo = new BeanParamMeta(paraType,
                    MvcConvertUtils.Ref.getSetterMethods(paraType),
                    key);
            Object p = paramMetaInfo.newParamInstance();
            // 把http参数赋值给参数对象
            while (enumeration.hasMoreElements()) {
                String httpParamName = enumeration.nextElement();
                String[] paramValue = request.getParameterValues(httpParamName);
                if (paramValue.length == 1 && paramValue[0].length() < 1) {
                    // 前端的空值("")和null值，传到后端都是""，且无法区分，没有意义，因此直接丢弃掉
                    continue;
                }
                if (!httpParamName.endsWith("[]")) {
                    paramMetaInfo.setParam(p, httpParamName, paramValue[0]);
                } else {
                    paramMetaInfo.setParam(p, httpParamName.substring(0, httpParamName.length() - 2),
                            paramValue);
                }
            }
            
            return p;
        }
    }
    
    private Object getHeaderValue(String key, String defaultVal, HttpRequest request, Class<?> paraType) {
        if (MvcConvertUtils.canConvert(paraType) != null) {
            // 数据类型为主要类型，可以直接转换，比如int、Long等
            String sval = request.getHeader(key);
            if (sval != null) {
                return MvcConvertUtils.convert(sval, paraType);
            } else if (defaultVal != null) {
                return MvcConvertUtils.convert(defaultVal, paraType);
            }
            return null;
        }
        return null;
    }
    
    private Object getAttributeValue(String key, HttpRequest request, Class<?> paraType) {
        Object obj = request.getAttribute(key);
        if (obj == null) {
            return null;
        }
        if (paraType.isAssignableFrom(obj.getClass())) {
            return obj;
        }
        throw new IllegalArgumentException("can'not get the param '" + key + "' of type=" + paraType.getName());
    }
    
    private Object getPathValue(List<String> pathVals, Class<?> paraType, int index) {
        if (MvcConvertUtils.canConvert(paraType) != null) {
            // 数据类型为主要类型，可以直接转换，比如int、Long等
            String sval = pathVals.get(index);
            return MvcConvertUtils.convert(sval, paraType);
        }
        return null;
    }
    
    // 默认为json类型
    private Object getBodyValue(HttpRequest request, Class<?> paraType) {
            // 数据类型为复合类型，需要调用标准setter赋值
        return PrivateJson.parseObject(request.getBodyString(), paraType);
    }
    
    private Annotation getMethodParamAnnotation(Annotation[] annotations) {
        for (Annotation a : annotations) {
            if (a.annotationType().equals(RequestParam.class) 
                    || a.annotationType().equals(RequestBody.class)
                    || a.annotationType().equals(PathVariable.class)
                    || a.annotationType().equals(RequestHeader.class)
                    || a.annotationType().equals(RequestAttribute.class))
                return a;
        }
        return null;
    }
    
    private static String connectPaths(String p1, String p2) {
        if (StringUtils.isNullOrEmpty(p2)) {
            return p1;
        }
        return PathUtils.connectPaths(p1, p2);
    }

}
