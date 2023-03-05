package tickets.Dao;

import tickets.Common.Dbutils;
import tickets.VO.Vehicle;

import java.sql.*;
import java.util.List;

public class CarDao extends BaseDao {
    /////通过车辆编号查询车辆是否存在   存在的话则不能再添加此名字的车辆
    public Vehicle findByVehicleNumWithLock(String vehicleNum) throws SQLException {
        Connection connection = Dbutils.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Vehicle vehicle = null;
            try{
                statement=connection.prepareStatement("SELECT v.`vehicle_id`,v.`vehicle_num`,v.`buy_date`,v.`brand`,v.model,v.`max_carry`,v.`displacement`,v.`engine_number`" +
                      "FROM vehicles v " +
                      "WHERE (v.`del_flag`=0 OR v.`del_flag` IS NULL) AND v.`vehicle_num`=?" +
                      "FOR UPDATE");
                 statement.setString(1,vehicleNum);
                 resultSet=statement.executeQuery();
                   if(resultSet.next()) {
                       vehicle = this.convert2Vehicle(resultSet, false);
                   }
                   }finally {
             Dbutils.close(resultSet,statement);

            }
                return vehicle;
    }

    private Vehicle convert2Vehicle(ResultSet resultSet, boolean b) throws SQLException {
        Vehicle vehicle=new Vehicle();
        vehicle.setVehicleId(resultSet.getInt("vehicle_id"));
        vehicle.setVehicleNum(resultSet.getString("vehicle_num"));
        vehicle.setBuyDate(resultSet.getDate("buy_date"));
        vehicle.setBrand(resultSet.getString("brand"));
        vehicle.setModel(resultSet.getString("model"));
        vehicle.setMaxCarry(resultSet.getInt("max_carry"));
         if(resultSet.getObject("displacement")!=null){
                    vehicle.setDisplacement(resultSet.getFloat("displacement"));
                    vehicle.setEngineNumber(resultSet.getString("engine_number"));
         }
         if(b){
             vehicle.delFlag = resultSet.getShort("del_flag");
             vehicle.cerator = resultSet.getInt("creator");
             vehicle.createDate = resultSet.getTimestamp("create_date");
             vehicle.updator = resultSet.getInt("updator");
             vehicle.updateDate = resultSet.getTimestamp("update_date");
         }
         return  vehicle;
    }


    //添加车辆
    public int addCar(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles(vehicle_num,buy_date,brand,model,max_carry, " +
                "displacement,engine_number,del_flag,creator,create_date,updator,update_date) " +
                "VALUES(?,?,?,?,?,?,?,0,?,?,?,?)";
java.sql.Date buyDate=null;
                   if(vehicle.getBuyDate()!=null){
                       //判断购买日期属性是否为空  不为空 则将他转化为时分秒格式的java sal date格式
                       //并赋给各变量   此变量在sql语句？中 可成为购买日期的值
                       buyDate=new java.sql.Date(vehicle.getBuyDate().getTime());
                   }
        Timestamp createDate, updateDate;
          createDate=new Timestamp(vehicle.createDate.getTime());
          updateDate=new Timestamp(vehicle.updateDate.getTime());
        Object[] params = {vehicle.getVehicleNum(), buyDate, vehicle.getBrand(), vehicle.getModel(), vehicle.getMaxCarry(),
                vehicle.getDisplacement(), vehicle.getEngineNumber(), vehicle.cerator, createDate, vehicle.updator, updateDate};
                return super.update(sql,params);
    }
    /**查询所有车辆信息  用于班次的车辆下拉框信息**/

    public List<Vehicle> findAllVehicles() throws SQLException {
        String sql = "SELECT v.`vehicle_id`,v.`vehicle_num`,v.`buy_date`,v.`brand`,v.`model`,v.`max_carry`,v.`displacement`,v.`engine_number` " +
                "FROM vehicles v " +
                "WHERE (v.`del_flag` = 0 OR v.`del_flag` IS NULL)";
        return super.queryObjectList(sql, null, Vehicle.class);
    }
}
