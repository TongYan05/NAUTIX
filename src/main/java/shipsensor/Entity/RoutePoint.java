package shipsensor.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("route_point")
public class RoutePoint {

    @TableId
    private Long id;

    private Long routeId;

    private Integer pointOrder;

    private Double latitude;

    private Double longitude;

}