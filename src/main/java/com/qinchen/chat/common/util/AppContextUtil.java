package com.qinchen.chat.common.util;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * UserBean: bxl
 * Date:2015/11/19
 * Time: 16:20
 */
@Component
public class AppContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) {
        AppContextUtil.applicationContext = applicationContext;
    }
    public static ApplicationContext getApplicationContext() {
        Assert.notNull(applicationContext, "AppContextUtil ApplicationContext  must be not null!");
        return applicationContext;
    }
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }
}
