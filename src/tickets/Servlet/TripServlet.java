package tickets.Servlet;

import tickets.Service.TripService;
import tickets.VO.AjaxResp;
import tickets.VO.Employee;
import tickets.VO.Trip;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet({"/trip/searchByRouteAndLeaveTime", "/trip/add"})
public class TripServlet extends BaseServlet {
    private TripService tripService = new TripService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = super.parseRequestURI(req);
        if ("/trip/searchByRouteAndLeaveTime".equals(uri)) {
            searchByRouteAndLeaveDate(req, resp);
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = super.parseRequestURI(req);
        if ("/trip/add".equals(uri)) {
            addTrip(req,resp);
        }
    }

    private void addTrip(HttpServletRequest req, HttpServletResponse resp) {
        Trip trip = super.parseJSONString(req, Trip.class);
        AjaxResp ajaxResp = null;
        if (trip == null || trip.getRoute() == null || trip.getRoute().getRouteId() == null || trip.getVehicle() == null || trip.getVehicle().getVehicleId() == null || trip.getEmployee() == null
                || trip.getEmployee().getEmpId() == null || trip.getLeaveTime() == null){
            ajaxResp = AjaxResp.error(-2, "参数不足!");
        }else{
            Employee loginedUser=super.getLoginedUser(req);
            if(loginedUser==null){
                ajaxResp=AjaxResp.error(-9,"还未登录!");
            }else{
                trip.cerator=trip.updator=loginedUser.getEmpId();
                try {
                    boolean result = this.tripService.addTrip(trip);
                    ajaxResp = AjaxResp.ok(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    ajaxResp = AjaxResp.error(-3, e.getMessage());
                }
            }
        }
        super.sendJSONResponse(resp, ajaxResp);
    }



    //根据线路和发车日期查询已安排的班次
    private void searchByRouteAndLeaveDate(HttpServletRequest req, HttpServletResponse resp) {
        Integer routeId = super.parseRequestParameter(req, "routeId", Integer.class);
        Date leaveDate = super.parseRequestParameter(req, "leaveTime", Date.class);
        AjaxResp ajaxResp = null;
        if(routeId==null||leaveDate==null){
            ajaxResp = AjaxResp.error(-2, "参数不足!");
        }else{
            List<Trip> tripList = this.tripService.searchByRouteAndLeaveDate(routeId, leaveDate);
            ajaxResp = AjaxResp.ok(tripList);
        }
        super.sendJSONResponse(resp, ajaxResp);
    }
}