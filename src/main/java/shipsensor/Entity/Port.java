package shipsensor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("port")
public class Port {


    private Long id;


    private String wpiId;


    private String portName;


    private String country;


    private String countryCode;


    private String portCode;


    private String portType;


    private Double latitude;


    private Double longitude;


    private Double maxShipLength;


    private Double maxDraft;


    private LocalDateTime createTime;


    private LocalDateTime updateTime;

}