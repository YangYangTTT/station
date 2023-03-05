package tickets.VO;

import java.util.List;

public class Route extends CommonVO {
    private Integer routeId;
    private String routeName;
    private Station startStation,terminalStation;
    private Float price,baggageFee;
    //途经站点
    private List<ThroughStation> throughStations;

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Station getStartStation() {
        return startStation;
    }

    public void setStartStation(Station startStation) {
        this.startStation = startStation;
    }

    public Station getTerminalStation() {
        return terminalStation;
    }

    public void setTerminalStation(Station terminalStation) {
        this.terminalStation = terminalStation;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getBaggageFee() {
        return baggageFee;
    }

    public void setBaggageFee(Float baggageFee) {
        this.baggageFee = baggageFee;
    }

    public List<ThroughStation> getThroughStations() {
        return throughStations;
    }

    public void setThroughStations(List<ThroughStation> throughStations) {
        this.throughStations = throughStations;
    }
}
