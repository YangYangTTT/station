package tickets.Servlet;


import com.alibaba.druid.util.StringUtils;

import tickets.Common.Constants;
import tickets.Common.Utils;
import tickets.Service.EmployeeService;
import tickets.VO.AjaxResp;
import tickets.VO.Employee;
import tickets.VO.PageInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet({"/login","/emp/list", "/emp/add","/emp/info","/emp/edit","/logout"})
public class EmployeeServlet extends BaseServlet {
    private EmployeeService employeeService = new EmployeeService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
             String uri=super.parseRequestURI(request);
             if("/emp/info".equals(uri)){
                 empInformation(request,response);
             }
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = super.parseRequestURI(request);
        if ("/login".equals(uri)) {
            login(request,response);
        }else if ( "/emp/add".equals(uri)){
            inputEmployee(request,response);
        }else if("/emp/list".equals(uri)){
            empList(request,response);
        }else if("/emp/edit".equals(uri)){
            editEmployee(request,response);
        }else if("/logout".equals(uri)){
            request.getSession().invalidate();
        }
    }


    ///查询当前页的所有员工
    private void empList(HttpServletRequest request, HttpServletResponse response) {
        //解析传入的字符串为map<String,String>格式 当前页和每页条数
        Map<String,Object> map=super.parseJSONString2Map(request);
        PageInfo<Employee> pageInfo=employeeService.listEmployeesByPage(map);
         super.sendJSONResponse(response,AjaxResp.ok(pageInfo));

    }


    //录入新员工
    private void   inputEmployee(HttpServletRequest request, HttpServletResponse response) {
        /**解析提交的字符串为Employee类型*/
        Employee employee = super.parseJSONString(request, Employee.class);
        AjaxResp ajaxResp = null;
        if (employee == null || StringUtils.isEmpty(employee.getAccount())) {
            ajaxResp = AjaxResp.error(-1, "参数格式不正确或缺失参数!");
        } else {
            Employee loginedUser = (Employee) super.getFromSession(request, Constants.SessionKey.LOGINED_USER);
            if (loginedUser == null) {
                ajaxResp = AjaxResp.error(-9, "未登录");
            } else {
                employee.cerator = loginedUser.getEmpId();
                employee.updator = loginedUser.getEmpId();
                try {
                    boolean result = this.employeeService.addEmployee(employee);
                    ajaxResp = AjaxResp.ok(result);
                } catch (Exception e) {
                    ajaxResp = AjaxResp.error(-3, e.getMessage());
                }
            }
        }
        super.sendJSONResponse(response, ajaxResp);
    }



    //登录
    private void login(HttpServletRequest request, HttpServletResponse response) {
        AjaxResp ajaxResp = null;
        Map<String, Object> hashMap = super.parseJSONString2Map(request);
        if (hashMap.containsKey("account") && hashMap.containsKey("password")) {
            Employee employee = this.employeeService.findByAccount((String)hashMap.get("account"));
            if (employee == null) {
                ajaxResp = AjaxResp.error(-2, "账号或密码不正确!");
            } else {
                if (Utils.matchPassword((String) hashMap.get("password"), employee.getPwd())) {
                    employee.setPwd("");
                    ajaxResp = AjaxResp.ok(employee);
                    //这一步为将此登陆的信息存入session  用于别的页面get此session 来设置updator的值
                    super.save2Session(request, Constants.SessionKey.LOGINED_USER, employee);
                } else {
                    ajaxResp = AjaxResp.error(-2, "账号或密码不正确!");
                }
            }
        } else {
            ajaxResp = AjaxResp.error(-1, "参数不足!");
        }
        super.sendJSONResponse(response, ajaxResp);
    }



    //////查询要编辑的人员的信息(编辑功能)
    private void empInformation(HttpServletRequest request, HttpServletResponse response) {
        //解析提交参数为任意类型
      Integer empId=super.parseRequestParameter(request,"empId",Integer.class);
      AjaxResp ajaxResp=null;
        if(empId==null){
          ajaxResp=AjaxResp.error(-1,"参数不走");
        }else{
            Employee employee=this.employeeService.findByEmpId(empId);
            ajaxResp=AjaxResp.ok(employee);
        }
        super.sendJSONResponse(response,ajaxResp);
    }




    //编辑员工信息(编辑功能)
    private void editEmployee(HttpServletRequest request, HttpServletResponse response) {
        Employee employee = super.parseJSONString(request, Employee.class);
        AjaxResp ajaxResp=null;
          if(employee==null|| employee.getEmpId() ==null||employee.getAccount()==null){
               ajaxResp=AjaxResp.error(-1,"参数不足");
          }else{
              //通过定义的getFromSession方法  得到此时登陆的管理员的信息用于设置updator的值
              //cisession在login servelt中查询到此用户信息时set
       Employee loginedUser=(Employee) super.getFromSession(request,Constants.SessionKey.LOGINED_USER);
             if(loginedUser==null){
                 ajaxResp=AjaxResp.error(-9,"还未登录");
             }else{
                employee.updator=loginedUser.getEmpId();
                boolean result=this.employeeService.editEmployee(employee);
                ajaxResp=AjaxResp.ok(result);
             }
          }
          super.sendJSONResponse(response,ajaxResp);
    }


}
