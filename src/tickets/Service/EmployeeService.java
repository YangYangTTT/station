package tickets.Service;

import com.alibaba.druid.util.StringUtils;
import org.apache.log4j.Logger;
import tickets.Common.Dbutils;
import tickets.Common.Utils;
import tickets.Dao.EmployeeDao;
import tickets.VO.Employee;
import tickets.VO.PageInfo;

import java.sql.SQLException;
import java.util.Date;

import java.util.List;
import java.util.Map;

public class EmployeeService {
    private static Logger logger = Logger.getLogger(EmployeeService.class);

    private EmployeeDao employeeDao = new EmployeeDao();

    /**
     * 根据账号查找员工信息
     *
     * @param account
     * @return
     */
    public Employee findByAccount(String account) {
        Employee employee = null;
        try {
            employee = this.employeeDao.findByAccount(account);
            Dbutils.commit();
        } catch (SQLException e) {
            Dbutils.rollback();
            logger.error("根据账号查询员工信息时异常:" + e.getMessage());
        }
        return employee;
    }


    /**
     * 录入员工
     */
    public boolean addEmployee(Employee employee) {
        int count = 0;
        try {
            //先调用Dao中方法 通过账号查询此用户是否存在，不存在时才能录入
            Employee emp = this.employeeDao.findByAccountWithLock(employee.getAccount());

            if (emp != null) {
                throw new RuntimeException("账号重复，不能录入!");
            }
            employee.setPwd(Utils.digestPassword(employee.getPwd()));
            if (StringUtils.isEmpty(employee.getNationality())) {
                employee.setNationality(null);
            }
            if (StringUtils.isEmpty(employee.getDuty())) {
                employee.setDuty(null);
            }
            Date date = new Date();
            employee.createDate = date;
            employee.updateDate = date;
            count = this.employeeDao.insertEmployee(employee);
            Dbutils.commit();
        } catch (Exception e) {
            logger.error("录入新员工时异常:" + e.getMessage());
            Dbutils.rollback();
        }
        return count > 0;
    }


    ///查询当前页的所有员工
    public PageInfo<Employee> listEmployeesByPage(Map<String, Object> map) {
        PageInfo<Employee> pageInfo = new PageInfo<>();
        //1  设置pageinfo类里面的total属性
        try {
            pageInfo.setTotal(this.employeeDao.countEmployees());
            Integer currentPage = (Integer) map.get("currentPage");
            if (currentPage == null) {
                currentPage = 1;
            }
            //1  设置pageinfo类里面的currentPage属性
            pageInfo.setCurrentPage(currentPage);
            //1  设置pageinfo类里面的list属性
            pageInfo.setList(this.employeeDao.listEmployeeByPage(map));
            Dbutils.commit();
        } catch (SQLException e) {
            logger.error("查询数据时异常" + e.getMessage());
            Dbutils.rollback();
        }
        return pageInfo;
    }


    //////查询要编辑的人员的信息(编辑功能)
    public Employee findByEmpId(Integer empId) {
        Employee employee = null;
        try {
            employee = this.employeeDao.findByEmpId(empId);
            Dbutils.commit();
        } catch (Exception e) {
            Dbutils.rollback();
            logger.error("根据员工编号查询员工信息时异常:" + e.getMessage());
        }
        return employee;
    }


    ///////编辑人员(编辑功能)
    public boolean editEmployee(Employee employee) {
        int count = 0;
        if (StringUtils.isEmpty(employee.getNationality())) {
            employee.setNationality(null);
        }
        if (StringUtils.isEmpty(employee.getDuty())) {
            employee.setDuty(null);
        }
        if (StringUtils.isEmpty(employee.getAddress())) {
            employee.setAddress(null);
        }
        /////设置employee里面的updataDate属性
        employee.updateDate = new Date();
        try {
            count = this.employeeDao.editEmployee(employee);
            Dbutils.commit();
        } catch (Exception e) {
            Dbutils.rollback();
            logger.error("编辑员工信息时异常" + e.getMessage());

        }
        return count > 0;
    }

}

