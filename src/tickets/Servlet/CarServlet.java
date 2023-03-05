package tickets.Servlet;

import com.alibaba.druid.util.StringUtils;
import tickets.Service.CarService;
import tickets.VO.AjaxResp;
import tickets.VO.Employee;
import tickets.VO.Vehicle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/car/add"})
public class CarServlet extends BaseServlet {
    CarService carService=new CarService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       String uri=super.parseRequestURI(req);
            if("/car/add".equals(uri)){
                addCar(req,resp);
            }
    }

    private void addCar(HttpServletRequest req, HttpServletResponse resp) {
        Vehicle vehicle=super.parseJSONString(req,Vehicle.class);
        AjaxResp ajaxResp=null;
        if(vehicle==null|| StringUtils.isEmpty(vehicle.getVehicleNum())||StringUtils.isEmpty(vehicle.getBrand())||
        vehicle.getBuyDate()==null||vehicle.getMaxCarry()==null){
           ajaxResp=AjaxResp.error(-1,"参数不足");
        }else{
             try {
                 Employee loginedUser=super.getLoginedUser(req);
             if(loginedUser==null){
                 ajaxResp=AjaxResp.error(-9,"还未登陆");
             }else{
                  //登陆者不为空则设置cerator
                        vehicle.cerator=vehicle.updator=loginedUser.getEmpId();
                         boolean result=this.carService.addCar(vehicle);
                         ajaxResp=AjaxResp.ok(result);
             }
             }catch (Exception e){
                  ajaxResp=AjaxResp.error(-3,e.getMessage());

             }
        }
        super.sendJSONResponse(resp,ajaxResp);
    }
}
