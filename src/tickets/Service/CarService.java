package tickets.Service;

import org.apache.log4j.Logger;
import tickets.Common.Dbutils;
import tickets.Dao.CarDao;
import tickets.VO.Vehicle;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class CarService {
    private  static Logger logger=Logger.getLogger(CarService.class);
    CarDao carDao=new CarDao();
    ////增加车辆
    public boolean addCar(Vehicle vehicle) {
        int count = 0;
        try {
            //检查车牌是否已经存在
            Vehicle temp = this.carDao.findByVehicleNumWithLock(vehicle.getVehicleNum());
            if (temp != null) {
                Dbutils.rollback();
                throw new RuntimeException("车牌号码以及存在!");
            }
            vehicle.createDate = vehicle.updateDate = new Date();
            //保存车辆信息
            count = this.carDao.addCar(vehicle);
            Dbutils.commit();
        } catch (SQLException e) {
            Dbutils.rollback();
            logger.error("录入车辆信息时异常:" + e.getMessage());
        }
        return count > 0;
    }



         ///查询所有车辆信息
    public List<Vehicle> findAllVehicles() {
        List<Vehicle>list=null;
        try {
            list = this.carDao.findAllVehicles();
            Dbutils.commit();
        }catch (SQLException e){
          Dbutils.rollback();
            logger.error("查找所有车辆信息时异常"+e.getMessage());
        }
        return  list;
    }
}
