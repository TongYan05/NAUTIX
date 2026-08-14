package shipsensor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 传感器类型字典表
 */
@Data
@TableName("sensor_dict")
public class SensorDict {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String typeCode;

    private String typeName;

    private String defaultUnit;

    private String description;
}