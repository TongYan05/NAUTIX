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
 * 告警记录流水表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("alert_record")
public class AlertRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer shipId;

    private Long ruleId;

    private BigDecimal triggerValue;

    private LocalDateTime alertTime;

    private Integer handleStatus;
}
