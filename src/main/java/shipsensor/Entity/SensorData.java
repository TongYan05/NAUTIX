package shipsensor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 传感器实时监测数据表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sensor_data")
public class SensorData {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long configId;

    private Integer shipId;

    // 注意：SQL中你改成了DOUBLE，这里对应Double；如果是DECIMAL则用BigDecimal
    private Double dataValue;

    private LocalDateTime recordedAt;

    private LocalDateTime createTime;


}