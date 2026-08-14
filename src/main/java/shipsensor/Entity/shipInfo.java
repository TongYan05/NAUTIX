package shipsensor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 船舶基础信息表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("ship_info")
public class ShipInfo {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String shipName;

    private String imo;

    private String registryPort;

    private String shipType;

    private Integer buildYear;

    private BigDecimal lengthOverall;

    private BigDecimal beam;

    private BigDecimal draft;

    private String dimensionLbd;

    private BigDecimal displacement;

    private String mainEngineModel;

    private BigDecimal sailingSpeed;

    private String shipyardBuilder;

    private String operatingCompany;

    private LocalDate surveyValidDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}