package shipsensor.simulator.runtime;

import lombok.Data;

@Data
public class ShipRuntimeState {

    /**
     * 船舶ID
     */
    private Integer shipId;

    /**
     * 当前航行状态
     */
    private ShipMode mode;

    /**
     * 纬度
     */
    private double latitude;

    /**
     * 经度
     */
    private double longitude;

    /**
     * 航速(kn)
     */
    private double speed;

    /**
     * 航向
     */
    private double heading;

    /**
     * 发动机负载
     */
    private double engineLoad;

    /**
     * 剩余燃油
     */
    private double fuelPercent;

    /**
     * 当前天气ID
     */
    private Long weatherId;

    /**
     * 当前航线ID
     */
    private Long routeId;

    /**
     * 当前航线点
     */
    private Integer currentPoint;

}