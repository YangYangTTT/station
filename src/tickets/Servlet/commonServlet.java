package tickets.Servlet;

import tickets.Service.CarService;
import tickets.Service.EmployeeService;
import tickets.Service.xianluService;
import tickets.VO.AjaxResp;
import tickets.VO.Employee;
import tickets.VO.Route;
import tickets.VO.Vehicle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet({"/common/routes","/common/vehicles","/common/employees"})
public class commonServlet extends BaseServlet {
 private xianluService xianluservice=new xianluService();
 private CarService carService=new CarService();
 private EmployeeService employeeServlet=new EmployeeService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = super.parseRequestURI(req);
        AjaxResp ajaxResp = null;
        if ("/common/routes".equals(uri)) {
            List<Route> routeList = this.xianluservice.findAllRoutes();
            ajaxResp = AjaxResp.ok(routeList);
        } else if ("/common/vehicles".equals(uri)) {
            List<Vehicle> vehicleList = this.carService.findAllVehicles();
            ajaxResp = AjaxResp.ok(vehicleList);
           }/*else if("/common/employees".equals(uri)){
               Integer roleid=super.parseRequestParameter(req,"role",Integer.class);
              Employee employee=new Employee();
                  if(roleid!=null){
                   employee.setRole(roleid);
                  }
                  List<Employee>employeeList=this.employeeServlet.seachByConditions(employee);
                        ajaxResp=AjaxResp.ok(employeeList);
           }*/else{
                ajaxResp=AjaxResp.error(-6,"无法处理该请求");
           }
           super.sendJSONResponse(resp,ajaxResp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
