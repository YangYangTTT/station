package tickets.Dao;

import tickets.Common.Dbutils;
import tickets.Common.Utils;
import tickets.VO.Employee;
import tickets.VO.Route;
import tickets.VO.Trip;
import tickets.VO.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TripDao extends BaseDao{
    public List<Trip> searchByRouteAndLeaveDate (Integer routeId, Date leaveDate) throws SQLException {
        Connection connection = Dbutils.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Trip> list = new ArrayList<>();
        Trip trip = null;
        try {
            statement = connection.prepareStatement("SELECT t.`trip_id`,t.`route_id`,r.`route_name`,t.`vehicle_id`,v.`vehicle_num`, " +
                    " t.`emp_id`,e.`real_name`, " +
                    " t.`leave_time`,t.`return_time` " +
                    "FROM trips t INNER JOIN routes r ON t.`route_id`=r.`route_id` " +
                    "     INNER JOIN vehicles v ON t.`vehicle_id`=v.`vehicle_id` " +
                    "     INNER JOIN employees e ON t.`emp_id`=e.`emp_id` " +
                    "WHERE ( t.`del_flag`=0 OR t.`del_flag` IS NULL ) " +
                    "     AND t.`route_id`=? " +
                    "     AND t.`leave_time`>=? " +
                    "     AND t.`leave_time`<?");
            statement.setInt(1, routeId);
            Date date = Utils.clearHMS(leaveDate);
            statement.setTimestamp(2, new Timestamp(date.getTime()));
            date = Utils.addDays(date, 1);
            statement.setTimestamp(3, new Timestamp(date.getTime()));
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                trip = new Trip();
                trip.setTripId(resultSet.getString("trip_id"));
                Route route = new Route();
                route.setRouteId(resultSet.getInt("route_id"));
                route.setRouteName(resultSet.getString("route_name"));
                trip.setRoute(route);
                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleId(resultSet.getInt("vehicle_id"));
                vehicle.setVehicleNum(resultSet.getString("vehicle_num"));
                trip.setVehicle(vehicle);
                Employee employee = new Employee();
                employee.setEmpId(resultSet.getInt("emp_id"));
                employee.setRealName(resultSet.getString("real_name"));
                trip.setEmployee(employee);
                trip.setLeaveTime(resultSet.getTimestamp("leave_time"));
                trip.setReturnTime(resultSet.getTimestamp("return_time"));

                list.add(trip);
            }
        } finally {
            Dbutils.close(resultSet, statement);
        }
        return list;
    }

    /**
     * 根据线路和发车日期，统计当日已安排的班次数量
     *
     * @param routeId
     * @param leaveTime
     * @return
     * @throws SQLException
     */
    public int countByRouteAndLeaveTime(Integer routeId, Date leaveTime) throws SQLException {
        Connection connection = Dbutils.getConnection();
        PreparedStatement statement = null;
        int count = 0;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement("SELECT COUNT(t.`trip_id`) " +
                    "FROM trips t  " +
                    "WHERE ( t.`del_flag`=0 OR t.`del_flag` IS NULL ) " +
                    "     AND t.`route_id`=? " +
                    "     AND t.`leave_time`>=? " +
                    "     AND t.`leave_time`<?");
            statement.setInt(1, routeId);
            Date date = Utils.clearHMS(leaveTime);
            statement.setTimestamp(2, new Timestamp(date.getTime()));
            Date date1 = Utils.addDays(date, 1);
            statement.setTimestamp(3, new Timestamp(date1.getTime()));
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } finally {
            Dbutils.close(resultSet, statement);
        }
        return count;
    }

    /**
     * 录入新班次
     *
     * @param trip
     * @return
     */
    public int addTrip(Trip trip) throws SQLException {
        String sql = "INSERT INTO trips(trip_id,route_id,vehicle_id,emp_id,leave_time,return_time,del_flag,creator,create_date,updator,update_date)\n" +
                "VALUES(?,?,?,?,?,?,0,?,?,?,?)";
        Timestamp leaveTime, returnTime = null, createDate, updateDate;
        leaveTime = new Timestamp(trip.getLeaveTime().getTime());
        if (trip.getReturnTime() != null) {
            returnTime = new Timestamp(trip.getReturnTime().getTime());
        }
        createDate = new Timestamp(trip.createDate.getTime());
        updateDate = new Timestamp(trip.updateDate.getTime());
        Object[] params = {trip.getTripId(), trip.getRoute().getRouteId(), trip.getVehicle().getVehicleId(), trip.getEmployee().getEmpId(), leaveTime, returnTime, trip.cerator, createDate, trip.updator, updateDate};
        return super.update(sql, params);
    }
}
