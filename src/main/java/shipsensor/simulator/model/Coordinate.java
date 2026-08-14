package shipsensor.simulator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GPS坐标对象
 *
 * 不属于数据库实体，仅在模拟器运行时使用。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordinate {

    /**
     * 纬度
     */
    private double latitude;

    /**
     * 经度
     */
    private double longitude;
}