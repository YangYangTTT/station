package tickets.Dao;

import com.alibaba.druid.util.StringUtils;
import org.apache.log4j.Logger;
import tickets.Common.Dbutils;
import tickets.VO.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class EmployeeDao extends BaseDao {
    private static Logger logger = Logger.getLogger(EmployeeDao.class);


    private Employee convert2Employee(ResultSet resultSet, boolean detail) throws SQLException {
        Employee employee = new Employee();
        employee.setEmpId(resultSet.getInt("emp_id"));
        employee.setAccount(resultSet.getString("account"));
        employee.setRealName(resultSet.getString("real_name"));
        employee.setIdCardNum(resultSet.getString("id_card_num"));
        employee.setMobile(resultSet.getString("telephone"));
        employee.setDuty(resultSet.getString("duty"));
        employee.setRole(resultSet.getInt("role"));
        employee.setSex(resultSet.getString("sex"));

        if (detail) {
            employee.setPwd(resultSet.getString("pwd"));
            employee.setNationality(resultSet.getString("nationality"));
            employee.setDob(resultSet.getDate("dob"));
            employee.setAddress(resultSet.getString("address"));
            employee.setHireDate(resultSet.getDate("hire_Date"));
            super.setCommonVOProperty(employee, resultSet, super.parseColumnNames(resultSet));
        }
        return employee;
    }


    /**
     * 录入新员工
     *
     * @param employee
     * @return
     * @throws SQLException
     */
    public int insertEmployee(Employee employee) throws SQLException {
        java.sql.Date dob = null, createDate, updateDate, hireDate = null;
        if (employee.getDob() != null) {
            dob = new java.sql.Date(employee.getDob().getTime());
        }
        createDate = new java.sql.Date(employee.createDate.getTime());
        updateDate = new java.sql.Date(employee.updateDate.getTime());
        if (employee.getHireDate() != null) {
            hireDate = new java.sql.Date(employee.getHireDate().getTime());
        }

        return super.update("INSERT INTO employees(account,pwd,real_name,sex,nationality,id_card_num,dob,address, " +
                "hire_date,telephone,duty,role,del_flag,creator,create_date,updator,update_date) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{employee.getAccount(), employee.getPwd(), employee.getRealName(),
                employee.getSex(), employee.getNationality(), employee.getIdCardNum(), dob, employee.getAddress(), hireDate, employee.getMobile(), employee.getDuty(),
                employee.getRole(), 0, employee.cerator, createDate, employee.updator, updateDate});
    }


    ////查询人员总数 total  （分页功能）
    public int countEmployees() throws SQLException {
        Connection connection = Dbutils.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int count = 0;
        try {
            statement = connection.prepareStatement("SELECT COUNT(e.`emp_id`) " +
                    "FROM employees e " +
                    "WHERE del_flag=0 OR del_flag IS NULL");
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } finally {
            Dbutils.close(resultSet, statement);
        }
        return count;
    }

    //查询每页的员工    (分页功能)
    public List<Employee> listEmployeeByPage(Map<String, Object> map) throws SQLException {
        //需要从map中取出当前页 和每页多少人两个信息
        Integer currentPage = (Integer) map.get("currentPage");
        if (currentPage == null) {
            currentPage = 1;
        }
        Integer pageSize = (Integer) map.get("pageSize");
        if (pageSize == null) {
            pageSize = 5;
        }
        return super.queryObjectList("SELECT e.`emp_id`,e.`account`,e.`real_name`,e.`sex`,e.`telephone`,e.`duty`,e.`role` " +
                "FROM employees e " +
                "WHERE (e.`del_flag`=0 OR e.`del_flag` IS NULL)  " +
                "LIMIT ?,?", new Object[]{(currentPage - 1) * pageSize, pageSize}, Employee.class);
    }

    /////查询此员工是否存在(录入前需查询)
    public Employee findByAccountWithLock(String account)throws SQLException  {
        Connection connection = Dbutils.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Employee employee = null;
        try {
            statement = connection.prepareStatement("select * from employees e where e.account=? and ( del_flag=0 OR del_flag IS NULL ) for update");
            statement.setString(1, account);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                employee = this.convert2Employee(resultSet, true);
            }
        } finally {
            Dbutils.close(resultSet, statement);
        }
        return employee;
    }




    //////查询要编辑的人员的信息(编辑功能)
    //调用通用查询方法
    public Employee findByEmpId(Integer empId) throws SQLException {
       return super.querySingleObject("SELECT e.* " +
               "FROM employees e " +
               "WHERE e.`emp_id`=? and ( del_flag=0 OR del_flag IS NULL ) ", new Object[]{empId}, Employee.class);
    }




    //编辑员工信息(编辑功能)
    public int editEmployee(Employee employee) throws SQLException {
          //判断dob  hireDate 是否为空 不为空设置他们用于编辑里面的信息  以及将service层设置好的updateDate转换为
        //javasql的getTime格式
        java.sql.Date dob=null,hireDate=null,updateDate;

        if(employee.getDob()!=null){
          dob=new java.sql.Date(employee.getDob().getTime());
        }
        if(employee.getHireDate()!=null){
            hireDate=new java.sql.Date(employee.getHireDate().getTime());
        }
        updateDate=new java.sql.Date(employee.updateDate.getTime());
        //此时object数组中的内容为用户修改的传过来的employee中的内容，以及dob,heirdata,updatadate等属性得自己
        //定义   步骤为  先判断属性是否为空 不为空则new java.sql.date(在employee中得到的数据.gettime)
        Object[] params = new Object[]{employee.getRealName(), employee.getSex(), employee.getNationality(), employee.getIdCardNum(), dob, employee.getAddress(),
                hireDate, employee.getMobile(), employee.getDuty(), employee.getRole(), employee.updator, updateDate, employee.getEmpId()};
        return super.update("UPDATE employees SET real_name=?,sex=?,nationality=?,id_card_num=?,dob=?,address=?," +
                "hire_date=?,telephone=?, " +
                "duty=?,role=?,updator=?,update_date=? " +
                "WHERE emp_id=? AND (del_flag=0 OR del_flag IS NULL)", params);
        //updatadata直接在service层中设置，在Dao层中才能用employee类点出来
    }

    /**
     * 根据账号查询员工信息
     *
     * @param account
     * @return
     * @throws SQLException
     */
    public Employee findByAccount(String account) throws SQLException {
        return super.querySingleObject("SELECT e.* " +
                "FROM employees e " +
                "WHERE e.`account`=? and ( del_flag=0 OR del_flag IS NULL ) ", new Object[]{account}, Employee.class);
    }


}
