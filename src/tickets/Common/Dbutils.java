package tickets.Common;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Dbutils {
    private static DataSource dataSource = null;
    private static Logger logger = Logger.getLogger(Dbutils.class);

    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();
    /**获取连接*/
    public static Connection getConnection() throws SQLException {
        Connection conn = threadLocal.get();
        if (conn == null) {
            conn = dataSource.getConnection();
            threadLocal.set(conn);
        }
        return conn;
    }
    /***提交事务**/
    public static void commit() {
        Connection conn = threadLocal.get();
        if (conn != null) {
            //提交事务
            try {
                conn.commit();
            } catch (SQLException e) {
                logger.error("提交事务时异常:" + e.getMessage());
            }
            //归还连接
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("关闭连接时异常:" + e.getMessage());
            }
            //清理当前线程绑定的连接
            threadLocal.remove();
        }
    }
    /***回滚事务**/
    public static void rollback() {
        Connection conn = threadLocal.get();
        if (conn != null) {
            //回滚事务
            try {
                conn.rollback();
            } catch (SQLException e) {
                logger.error("回滚事务时异常:" + e.getMessage());
            }
            //归还连接
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("关闭连接时异常:" + e.getMessage());
            }
            //清理当前线程绑定的连接
            threadLocal.remove();
        }
    }
    /****初始化数据源*/
    public static void initDataSource() {
        logger.debug("初始化数据源");

        InputStream inputStream = null;

        try {
            inputStream = Dbutils.class.getClassLoader().getResourceAsStream("db.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setUrl(properties.getProperty("db.url", "jdbc:mysql://localhost:3306/tickets"));
            dataSource.setDriverClassName(properties.getProperty("db.driver.name"));
            dataSource.setUsername(properties.getProperty("db.user.name"));
            dataSource.setPassword(properties.getProperty("db.password"));
            dataSource.setMaxActive(Integer.parseInt(properties.getProperty("db.max", "20")));
            dataSource.setInitialSize(Integer.parseInt(properties.getProperty("db.init", "1")));
            dataSource.setDefaultAutoCommit(false);
            Dbutils.dataSource = dataSource;
            logger.debug("数据源初始化完成...");
        } catch (Exception e) {
            logger.error("初始化数据源时异常:" + e.getLocalizedMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }
    /****关闭数据源*/
    public static void closeDataSource() {
        if (dataSource != null) {
            DruidDataSource drudiDataSource = (DruidDataSource) Dbutils.dataSource;
            drudiDataSource.close();
            Dbutils.dataSource = null;
        }
    }
    /****关闭连接*/
    public static void close(ResultSet resultSet, Statement statement) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException var4) {
                var4.printStackTrace();
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException var3) {
                var3.printStackTrace();
            }
        }

    }
}




