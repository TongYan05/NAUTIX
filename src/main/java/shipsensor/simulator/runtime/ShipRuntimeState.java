package shipsensor.simulator.runtime;

import lombok.Data;


/**
 * 船舶实时运行状态
 *
 * 用于 Simulator 内存中的船舶状态维护
 *
 * 不对应数据库表
 */
@Data
public class ShipRuntimeState {


    /**
     * 船舶ID
     */
    private Integer shipId;



    /**
     * 当前航行模式
     *
     * STOP
     * LEAVING_PORT
     * CRUISING
     * TURNING
     * ARRIVING
     * ANCHORING
     */
    private ShipMode mode;



    /**
     * 当前纬度
     */
    private double latitude;



    /**
     * 当前经度
     */
    private double longitude;



    /**
     * 当前航速(kn)
     */
    private double speed;



    /**
     * 当前航向(0-360)
     */
    private double heading;



    /**
     * 发动机负载(%)
     */
    private double engineLoad;



    /**
     * 剩余燃油(%)
     */
    private double fuelPercent;



    /**
     * 当前天气区域ID
     */
    private Long weatherId;



    /**
     * 当前执行航线ID
     */
    private Long routeId;



    /**
     * 当前航线点索引
     */
    private Integer currentPoint;



    /**
     * 默认构造
     */
    public ShipRuntimeState() {

    }


    /**
     * 初始化船舶状态
     */
    public ShipRuntimeState(Integer shipId) {

        this.shipId = shipId;

        this.mode = ShipMode.STOP;

        this.speed = 0.0;

        this.heading = 0.0;

        this.engineLoad = 0.0;

        this.fuelPercent = 100.0;

        this.currentPoint = 0;
    }

}