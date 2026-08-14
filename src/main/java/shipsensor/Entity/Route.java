package shipsensor.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("route")
public class Route {


    /**
     * 航线ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;



    /**
     * 航线名称
     */
    private String routeName;



    /**
     * 起始港口ID
     */
    private Long startPortId;



    /**
     * 目的港口ID
     */
    private Long endPortId;



    /**
     * 航程(海里)
     */
    private Double distanceNm;



    /**
     * 描述
     */
    private String description;


}