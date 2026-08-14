package shipsensor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 告警规则配置表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("alert_rule")
public class AlertRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String ruleName;

    private Integer typeId;

    private BigDecimal thresholdValue;

    private String operator;

    private Integer durationSeconds;

    private Integer alertLevel;
}