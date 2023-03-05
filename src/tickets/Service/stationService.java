package tickets.Service;

import org.apache.log4j.Logger;
import tickets.Common.Dbutils;
import tickets.Dao.stationDao;
import tickets.VO.AjaxResp;
import tickets.VO.Station;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class stationService   {
    Logger logger=Logger.getLogger(stationService.class);
    stationDao stationdao=new stationDao();
    public List<Station> findAllStations() {
      List<Station> list=null;
      try {
          list = this.stationdao.findAllStations();
          Dbutils.commit();
      }catch(SQLException e){
         Dbutils.rollback();
        logger.error("查询站点列表异常"+e.getMessage());
      }

return list;
    }

    public boolean addStation(Station station) {
        AjaxResp ajaxResp=null;
             int count=0;
              //先检查车站是否存在 不存在再添加

        try {
        Station temp=stationdao.findByCodeWithLock(station.getStationCode());
        if(temp!=null){
             Dbutils .rollback();
              throw  new RuntimeException("站点名称已经存在，无法录入");
        }
               //不存在则设置createDATE和updatedate的值
        station.createDate=station.updateDate=new Date();
       count=this.stationdao.addStation(station);
       Dbutils.commit();
        } catch (SQLException e){
            logger.error("录入新站点时异常"+e.getMessage());
             Dbutils.rollback();
        }
        return  count>0;
    }
}
