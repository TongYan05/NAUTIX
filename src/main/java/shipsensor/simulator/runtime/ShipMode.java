package shipsensor.simulator.runtime;

/**
 * 船舶航行状态
 */
public enum ShipMode {

    /**
     * 停泊
     */
    STOP,

    /**
     * 离港
     */
    LEAVING_PORT,

    /**
     * 正常航行
     */
    CRUISING,

    /**
     * 转向
     */
    TURNING,

    /**
     * 到港
     */
    ARRIVING,

    /**
     * 抛锚
     */
    ANCHORING

}