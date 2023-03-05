package tickets.Servlet;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import org.apache.log4j.Logger;
import tickets.Common.Constants;
import tickets.Common.Utils;
import tickets.VO.Employee;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BaseServlet extends HttpServlet {

        private static ObjectMapper objectMapper = new ObjectMapper();

    private static Logger logger = Logger.getLogger(BaseServlet.class);

    @Override
    public void init() throws ServletException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

   /**解析提交的字符串*/
    public <T> T parseJSONString(HttpServletRequest req, Class<T> clz) {
        T t = null;
        try {
            t = objectMapper.readValue(req.getReader(), clz);
        } catch (IOException e) {
            logger.error("解析提交的JSON字符串时异常:" + e.getMessage());
        }
        return t;
    }

    /***解析请求URI 解析出除去本地路径外的uri*/
    public String parseRequestURI(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        int index = uri.indexOf(contextPath);
        if (index >= 0) {
            uri = uri.substring(index + contextPath.length());
        }
        return uri;
    }

    /** 给客户端返回JSON应答 */
    protected void sendJSONResponse(HttpServletResponse response, Object object) {
        response.setContentType("application/json; charset=UTF-8");
        if (object != null) {
            try {
                objectMapper.writeValue(response.getWriter(), object);
            } catch (IOException e) {
                logger.error("给客户端返回JSON应答时异常:" + e.getMessage());
            }
        }
    }
    /**
     * 解析JSON字符串为Map<String,String>时异常
     *
     */
    protected Map<String, Object> parseJSONString2Map(HttpServletRequest request) {
        MapType mapType = objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
        Map<String, Object> map = null;
        try {
            map = objectMapper.readValue(request.getReader(), mapType);
        } catch (IOException e) {
            logger.error("解析JSON字符串为Map<String,String>时异常:" + e.getMessage());
        }
        return map;
    }
    /**
     * 解析提交的参数，并转换为指定的类型
     *
     * @param req
     * @param paraName
     * @param clz
     * @param <T>
     * @return
     */
    protected <T> T parseRequestParameter(HttpServletRequest req, String paraName, Class<T> clz) {
        T result = null;
        //获取参数值
        String paraValue = req.getParameter(paraName);
        if (paraValue != null && paraValue.length() > 0) {
            try {
                if (clz == String.class || Number.class.isAssignableFrom(clz)) {
                    result = clz.getDeclaredConstructor(String.class).newInstance(paraValue);
                }
                if (clz == Date.class) {
                    Date date = Utils.parseString(paraValue);
                    if (date != null)
                        result = clz.getDeclaredConstructor(long.class).newInstance(date.getTime());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    protected void save2Session(HttpServletRequest request, String key, Object object) {
        request.getSession().setAttribute(key, object);
    }

    public Object getFromSession(HttpServletRequest request, String key) {
        return request.getSession().getAttribute(key);
    }
    protected Employee getLoginedUser(HttpServletRequest request) {
        return (Employee) request.getSession().getAttribute(Constants.SessionKey.LOGINED_USER);
    }
}

