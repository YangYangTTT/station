package tickets.VO;

public class ThroughStation extends Station {

    //编号
    private Integer serialId;

    private Route route;

    private Integer orderValue;

    private Float price, baggageFee;

    public Integer getSerialId() {
        return serialId;
    }

    public void setSerialId(Integer serialId) {
        this.serialId = serialId;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Integer getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(Integer orderValue) {
        this.orderValue = orderValue;
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
}
