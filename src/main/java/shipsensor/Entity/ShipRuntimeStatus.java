package shipsensor.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@TableName("ship_runtime_status")
public class ShipRuntimeStatus {


    /**
     * 船舶ID
     */
    @TableId
    private Integer shipId;



    /**
     * 航行状态
     */
    private String sailingStatus;



    /**
     * 纬度
     */
    private Double latitude;



    /**
     * 经度
     */
    private Double longitude;



    /**
     * 航速
     */
    private Double speed;



    /**
     * 航向
     */
    private Double heading;



    /**
     * 发动机负载
     */
    private Double engineLoad;



    /**
     * 剩余燃油
     */
    private Double fuelPercent;



    /**
     * 天气区域ID
     */
    private Long weatherId;



    /**
     * 更新时间
     */
    private LocalDateTime lastUpdate;


}