package tickets.Servlet;

import tickets.Service.xianluService;
import tickets.VO.AjaxResp;
import tickets.VO.Employee;
import tickets.VO.Route;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebServlet({"/rou/add"})
public class xianluServlet extends BaseServlet {
    xianluService xianluservice=new xianluService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       String uri=super.parseRequestURI(req);
       if("/rou/add".equals(uri)){
           Route route=super.parseJSONString(req,Route.class);
           AjaxResp ajaxResp=null;
              if(route==null||route.getRouteName()==null||route.getTerminalStation()==null||route.getStartStation()==null||
              route.getPrice()==null||route.getBaggageFee()==null){
                  ajaxResp=AjaxResp.error(-1,"参数不足");
              }else {
                  Employee loginuser = super.getLoginedUser(req);
                  if (loginuser == null) {
                      ajaxResp = AjaxResp.error(-9, "还未登陆");
                  }else{
                      route.cerator=route.updator=loginuser.getEmpId();
               try{
                   boolean result= this.xianluservice.addxianlu(route);
                   ajaxResp=AjaxResp.ok(result);
               }catch (Exception e){
                  ajaxResp=AjaxResp.error(-3,e.getMessage());
               }
                  }
              }
              super.sendJSONResponse(resp,ajaxResp);
       }
    }
}
