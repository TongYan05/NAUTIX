package shipsensor.simulator.generator;


import org.springframework.stereotype.Component;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.ShipInfo;


/**
 * 电池电压模拟器
 *
 * 商船常见:
 * 24V / 48V 系统
 *
 * 正常范围:
 * 22V ~ 28V
 */
@Component
public class BatteryVoltGenerator
        implements SensorGenerator {


    @Override
    public String getTypeCode() {

        return "BATTERY_VOLT";

    }



    @Override
    public Double generate(
            ShipInfo shipInfo,
            SensorConfig sensorConfig) {


        /*
         * 基准电压
         *
         * 船舶蓄电池系统
         * 一般约 24V
         */
        double baseVoltage = 24.5;



        /*
         * 模拟轻微波动
         *
         * -0.8 ~ +0.8
         */
        double fluctuation =
                Math.random() * 1.6 - 0.8;



        double voltage =
                baseVoltage + fluctuation;



        return Math.round(voltage * 100) / 100.0;

    }

}