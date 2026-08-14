package shipsensor.simulator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * 模拟器内部传递的数据消息
 *
 * Generator 生成
 * Queue 缓存
 * Writer 入库
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorDataMessage {


    /**
     * 传感器配置ID
     */
    private Long configId;


    /**
     * 船舶ID
     */
    private Integer shipId;


    /**
     * 数据值
     */
    private Double value;


    /**
     * 数据采集时间
     */
    private LocalDateTime recordedAt;


}