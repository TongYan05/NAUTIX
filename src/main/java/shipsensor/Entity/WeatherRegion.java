package shipsensor.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("weather_region")
public class WeatherRegion {

    @TableId
    private Long id;

    private String regionName;

    private Double airTemperature;

    private Double seaTemperature;

    private Double humidity;

    private Double pressure;

    private Double windSpeed;

    private Double windDirection;

    private Double waveHeight;

    private String weatherType;

    private LocalDateTime updateTime;

}