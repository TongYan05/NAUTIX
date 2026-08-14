package shipsensor.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 船舶传感器配置表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sensor_config")
public class SensorConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer shipId;

    private Integer typeId;

    private String sensorName;

    private LocalDate installDate;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}