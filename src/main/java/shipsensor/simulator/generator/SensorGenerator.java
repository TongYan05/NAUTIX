package shipsensor.simulator.generator;

import shipsensor.entity.SensorConfig;
import shipsensor.entity.ShipInfo;

/**
 * 所有传感器生成器统一接口
 */
public interface SensorGenerator {

    /**
     * 当前生成器支持的传感器类型(type_code)
     */
    String getTypeCode();

    /**
     * 生成传感器数值
     */
    Double generate(ShipInfo shipInfo, SensorConfig sensorConfig);

}