package tickets.Service;

import org.apache.log4j.Logger;
import tickets.Common.Dbutils;
import tickets.Common.Utils;
import tickets.Dao.TripDao;
import tickets.Dao.stationDao;
import tickets.Dao.xianluDao;
import tickets.VO.Route;
import tickets.VO.Station;
import tickets.VO.Trip;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class TripService {
    TripDao tripDao=new TripDao();
    xianluDao xianludao=new xianluDao();
    stationDao stationdao=new stationDao();
    public static Logger logger=Logger.getLogger(TripService.class);
    /**
     * 根据线路和发车日期查询班次
     *
     *
     */
    public List<Trip> searchByRouteAndLeaveDate(Integer routeId, Date leaveDate) {
        List<Trip> list = null;
        try {
            list = this.tripDao.searchByRouteAndLeaveDate(routeId, leaveDate);
            Dbutils.commit();
        } catch (SQLException e) {
            Dbutils.rollback();
          logger.error("根据线路和发车日期查询班次列表时异常：" +e.getMessage());
        }
        return  list;
    }

    public boolean addTrip(Trip trip) {
        int count = 0;
        try {
            //生成班次编号：线路终点代码-8位日期-3位线路编号+3位顺序值
            Route route = this.xianludao.findById(trip.getRoute().getRouteId());
            if (route == null) {
                throw new RuntimeException("该线路已被删除!");
            }
            Station station = this.stationdao.findById(route.getTerminalStation().getStationId());
            if (station == null) {
                throw new RuntimeException("该站点已被删除!");
            }
            //统计当日该线路已安排的班次数量
            int tripCount = this.tripDao.countByRouteAndLeaveTime(trip.getRoute().getRouteId(), trip.getLeaveTime());
            String tripId = station.getStationCode() + "-" + Utils.formatDate3(trip.getLeaveTime()) + "-" + String.format("%03d", route.getRouteId()) + "-" + String.format("%03d", tripCount + 1);
            trip.setTripId(tripId);
            logger.debug("trip id="+tripId);
            trip.createDate=trip.updateDate=new Date();
            count = this.tripDao.addTrip(trip);
            Dbutils.commit();
        } catch (SQLException e) {
            Dbutils.rollback();
            logger.error("添加新班次时异常：" + e.getMessage());
        }
        return count > 0;
    }
}
