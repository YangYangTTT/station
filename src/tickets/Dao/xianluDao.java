package tickets.Dao;

import tickets.Common.Dbutils;
import tickets.VO.Route;
import tickets.VO.Station;
import tickets.VO.ThroughStation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//查询 线路是否存在
public class xianluDao extends BaseDao {
    public Route findByNameWithLock(String routeName) throws SQLException {
        Connection connection = Dbutils.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Route route = null;
        try {
            statement = connection.prepareStatement("SELECT r.`route_id`,r.`route_name`,r.`start_station`,r.`terminal_station`,r.`price`,r.`baggage_fee` " +
                    "FROM routes r " +
                    "WHERE (r.`del_flag` =0  OR r.`del_flag` IS NULL) AND r.`route_name`=? for update");
            statement.setString(1,routeName);
            resultSet=statement.executeQuery();
             if(resultSet.next()){
                 route=this.convert2Route(resultSet,false);
              }
        } finally {
            Dbutils.close(resultSet, statement);

        }
        return route;

    }

    private Route convert2Route(ResultSet resultSet, boolean b) throws SQLException {
        Route route=new Route();
        route.setRouteId(resultSet.getInt("route_id"));
        route.setRouteName(resultSet.getString("route_name"));
        //始发站
        Station station=new Station();
        station.setStationId(resultSet.getInt("start_station"));
       route.setStartStation(station);
       //终点站
        station=new Station();
        station.setStationId(resultSet.getInt("terminal_station"));
        route.setTerminalStation(station);
        //全程票价
        route.setPrice(resultSet.getFloat("price"));
        route.setBaggageFee(resultSet.getFloat("baggage_fee"));
        if(b){
            super.setCommonVOProperty(route, resultSet, super.parseColumnNames(resultSet));
        }
        return route;
    }


    //查询线路不存在后  增加线路
    public int addxianlu(Route route) throws SQLException {

        java.sql.Timestamp createDate, updateDate;
        createDate = new java.sql.Timestamp(route.createDate.getTime());
        updateDate = new java.sql.Timestamp(route.updateDate.getTime());
        String sql = "INSERT INTO routes(route_name,start_station,terminal_station,price,baggage_fee,del_flag,creator,create_date,updator,update_date)\n" +
                "VALUES(?,?,?,?,?,0,?,?,?,?)";
        Object[] params = {route.getRouteName(), route.getStartStation().getStationId(), route.getTerminalStation().getStationId(),
                route.getPrice(), route.getBaggageFee(), route.cerator, createDate, route.updator, updateDate};
        int count = super.insertWithAutoIncrement(sql, params);
        if (count > 0) {
            route.setRouteId(count);
        }
        return count;
    }


///录入途径站点信息   当路线信息录入成功后 在Service 在传来的路线信息中get出途径站的信息列表并遍历
    //给每一个途经站点设置route属性
    public int saveThroughStations(List<ThroughStation> list) throws SQLException {
        Connection connection=Dbutils.getConnection();
        PreparedStatement preparedStatement=null;
        int count=0;
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO through_stations(route_id,station_id,order_value,price,baggage_fee)" +
                    "VALUES(?,?,?,?,0)");
            for (ThroughStation throughStation : list) {
                preparedStatement.setInt(1, throughStation.getRoute().getRouteId());
                preparedStatement.setInt(2, throughStation.getStationId());
                preparedStatement.setInt(3, throughStation.getOrderValue());
                preparedStatement.setFloat(4, throughStation.getPrice());
                preparedStatement.addBatch();
            }
            int[] array = preparedStatement.executeBatch();
            for (int a : array) {
                count += a;
            }
        }finally {
                Dbutils.close(null,preparedStatement);
        }
        return count;
    }

    public List<Route> findAllRoutes() throws SQLException {
        Connection connection=Dbutils.getConnection();
             PreparedStatement preparedStatement=null;
                 ResultSet resultSet=null;
        List<Route> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement("SELECT r.`route_id`,r.`route_name`,r.`start_station`,r.`terminal_station`,r.`price`,r.`baggage_fee` " +
                    "FROM routes r " +
                    "WHERE (r.`del_flag` =0 OR r.`del_flag` IS  NULL)");
             resultSet=preparedStatement.executeQuery();
             while(resultSet.next()){
               list.add(this.convert2Route(resultSet,false));
             }
        }finally {
             Dbutils.close(resultSet,preparedStatement);
        }
        return list;
    }

    /**
     * 根据线路编号查询线路信息
     *
     * @param routeId
     * @return
     * @throws SQLException
     */
    public Route findById(Integer routeId) throws SQLException {
        Connection connection = Dbutils.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Route route = null;

        try {
            statement = connection.prepareStatement("SELECT r.`route_id`,r.`route_name`,r.`start_station`,r.`terminal_station`,r.`price`,r.`baggage_fee` " +
                    "FROM routes r " +
                    "WHERE (r.`del_flag` =0  OR r.`del_flag` IS NULL) AND r.`route_id`=? ");
            statement.setInt(1, routeId);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                route = this.convert2Route(resultSet, false);
            }
        } finally {
            Dbutils.close(resultSet, statement);
        }
        return route;
    }
}

