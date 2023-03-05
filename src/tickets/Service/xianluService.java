package tickets.Service;

import org.apache.log4j.Logger;
import tickets.Common.Dbutils;
import tickets.Dao.xianluDao;
import tickets.VO.Route;
import tickets.VO.ThroughStation;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class xianluService {
    xianluDao xianludao=new xianluDao();
    Logger logger=Logger.getLogger(xianluService.class);
    int count=0;
    public boolean addxianlu(Route route) {
        //先查询此线路是否存在
           try{
               Route temp=this.xianludao.findByNameWithLock(route.getRouteName());
                if(temp!=null){
                    Dbutils.rollback();
                   throw new RuntimeException("线路已经存在，无法录入");
                }
                route.createDate=route.updateDate=new Date();
                logger.debug("temp="+temp);
               count=this.xianludao.addxianlu(route);

               //路线保存完以后判断是否成功  成功的话 将途经站从传入的route中get到list中，如果list不为空
               //则遍历list  给每一个途径站中设置route属性，值为传入的route,然后将list存入数据库
              if(count>0){
                  List<ThroughStation> list=route.getThroughStations();
                  if(list!=null&&list.size()>0){
                  for(ThroughStation t:list ){
                     t.setRoute(route);
                  }
                  count=this.xianludao.saveThroughStations(list);
                  }
              }
                      Dbutils.commit();
          }catch (SQLException e){
               Dbutils.rollback();
               logger.error("录入新路线时错误"+e.getMessage());
           }
           return count>0;
    }
    /**查询所有路线的方法   servlet层在common中 用于班次中选择路线信息**/
    public List<Route> findAllRoutes() {
        List<Route> list=null;
        try {
            list = this.xianludao.findAllRoutes();
            Dbutils.commit();
        }catch (Exception e){
             logger.error("查询所有路线时异常"+e.getMessage());
             Dbutils.rollback();
        }
        return list;
    }
}
