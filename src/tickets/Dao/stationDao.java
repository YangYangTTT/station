package tickets.Dao;

import tickets.Common.Dbutils;
import tickets.VO.Station;

import java.sql.*;
import java.util.List;

public class stationDao  extends  BaseDao{
    //查找所有站点信息
    public List<Station> findAllStations() throws SQLException {
        String sql = "SELECT s.`station_id`,s.`station_code`,s.`station_name`,s.`distance` " +
                "FROM stations s " +
                "WHERE ( s.`del_flag` = 0 OR s.`del_flag` IS NULL )";
        return super.queryObjectList(sql, null, Station.class);
    }
////通过站点代码查找站点信息
    public Station findByCodeWithLock(String stationCode) throws SQLException {
        Connection connection=Dbutils.getConnection();
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        Station station=null;
        try {
            preparedStatement = connection.prepareStatement("SELECT s.`station_id`,s.`station_name`,s.`station_code`,s.`distance`" +
                    "FROM stations s\n" +
                    "WHERE(s.`del_flag`=0 OR s.`del_flag` IS NULL)AND s.`station_code`=?" +
                    "FOR UPDATE");
            preparedStatement.setString(1,stationCode);
           resultSet=preparedStatement.executeQuery();
           if(resultSet.next()){
               station=this.convert2Station(resultSet,false);
           }
        }finally {
          Dbutils.close(resultSet,preparedStatement);
        }
        return  station;
    }
       ///将信息写入station方法
    private Station convert2Station(ResultSet resultSet, boolean b) throws SQLException {
        Station station=new Station();
        station.setStationId(resultSet.getInt("station_id"));
        station.setStationCode(resultSet.getString("station_code"));
        station.setStationName(resultSet.getString("station_name"));
        station.setDistance(resultSet.getInt("distance"));
        if(b){
            station.cerator = resultSet.getInt("creator");
            station.createDate = resultSet.getTimestamp("create_date");
            station.updator = resultSet.getInt("updator");
            station.updateDate = resultSet.getTimestamp("update_date");
        }
        return  station;
    }

    public int addStation(Station station) throws SQLException {
        String sql = "INSERT INTO stations(station_code,station_name,distance,del_flag,creator,create_date,updator,update_date) " +
                "VALUES(?,?,?,0,?,?,?,?)";
        Timestamp createDate,updateDate;
        createDate=new Timestamp(station.createDate.getTime());
        updateDate=new Timestamp(station.updateDate.getTime());
        Object[] params = {station.getStationCode(), station.getStationName(), station.getDistance(), station.cerator, createDate, station.updator, updateDate};
        return super.update(sql, params);
    }

    public Station findById(Integer stationId) throws SQLException {
        Connection connection = Dbutils.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Station station = null;

        try {
            statement = connection.prepareStatement("SELECT s.`station_id`,s.`station_code`,s.`station_name`,s.`distance` " +
                    "FROM stations s " +
                    "WHERE ( s.`del_flag` = 0 OR s.`del_flag` IS NULL ) AND s.`station_id`=? " +
                    "FOR UPDATE");
            statement.setInt(1, stationId);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                station = this.convert2Station(resultSet, false);
            }
        } finally {
            Dbutils.close(resultSet, statement);
        }
        return station;
    }
}
