package org.jretty.dubbo.util;

import org.jretty.dubbo.dto.HttpRequestDto;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 
 * @author zollty
 * @since 2021年4月25日
 */
public class SpringControllerInvokeTest implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContext = this.applicationContext;
    }
    
    public void test() {
        SpringControllerInvoke invoker = new SpringControllerInvoke(applicationContext);

        System.out.println("----------------");
        final String url = "/app/role/list";
        final String methodType = "POST";
        SimpleHttpRequest shr = new SimpleHttpRequest();
        shr.setRequestURI(url);
        shr.setMethod(methodType);
        
        shr.setParameter("roleName", "admin");
        shr.setParameter("order", "DESC");
        shr.setParameter("page", "10");
        shr.setParameter("limit", "20");
        
//        TestUser u = new TestUser();
//        u.setAge(18);
//        u.setName("zollty");
//        shr.setAttribute("user", u);
        final HttpRequestDto request = shr.toDto();
        
        Object result;
        try {
            result = invoker.doInvoke(request);
            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx: ");
            System.out.println(result);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


}
