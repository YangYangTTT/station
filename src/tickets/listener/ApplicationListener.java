package tickets.listener;
import tickets.Common.Dbutils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionBindingEvent;

@WebListener()
public class ApplicationListener implements ServletContextListener {


    public void contextInitialized(ServletContextEvent sce) {
        //初始化数据源
        Dbutils.initDataSource();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        //关闭数据源
        Dbutils.closeDataSource();
    }

}
