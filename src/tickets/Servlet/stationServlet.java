package tickets.Servlet;

import com.alibaba.druid.util.StringUtils;
import tickets.Service.stationService;
import tickets.VO.AjaxResp;
import tickets.VO.Employee;
import tickets.VO.Station;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet({"/sta/list","/sta/add"})
public class stationServlet extends BaseServlet {
    stationService stationService=new stationService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

       String uri=super.parseRequestURI(req);
       if("/sta/list".equals(uri)){
           List<Station> list=this.stationService.findAllStations();
             AjaxResp ajaxResp=AjaxResp.ok(list);
             super.sendJSONResponse(resp,ajaxResp);
       }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri=super.parseRequestURI(req);
        if("/sta/add".equals(uri)) {
            //将客户端信息解析为Station对象
            Station station = super.parseJSONString(req, Station.class);
            AjaxResp ajaxResp = null;
            if (station == null || StringUtils.isEmpty(station.getStationCode()) || StringUtils.isEmpty(station.getStationName())
                    || station.getDistance() == null) {
                ajaxResp = AjaxResp.error(-1, "参数不足");
            } else {
                //参数都够则设置cerator和updator
                Employee loginuser = super.getLoginedUser(req);
                if (loginuser == null) {
                    ajaxResp = AjaxResp.error(-9, "还未登陆");
                } else {
                    station.cerator = station.updator = loginuser.getEmpId();
                    try {
                        boolean result = this.stationService.addStation(station);
                        ajaxResp = AjaxResp.ok(result);
                    }catch(Exception e){
                          ajaxResp=AjaxResp.error(-3,e.getMessage());
                    }
                }
            }
                      super.sendJSONResponse(resp,ajaxResp);
        }
    }
}
