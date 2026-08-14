package shipsensor.simulator.generator;


import org.springframework.stereotype.Component;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.ShipInfo;

import java.util.concurrent.ThreadLocalRandom;


/**
 * 船舶功率模拟器
 *
 * type_code:
 * POWER
 *
 * 单位:
 * kW
 */
@Component
public class PowerGenerator implements SensorGenerator {


    @Override
    public String getTypeCode() {

        return "POWER";

    }



    @Override
    public Double generate(
            ShipInfo shipInfo,
            SensorConfig sensorConfig) {


        /*
         * 商船主机正常输出功率
         *
         * 基准:
         * 8000 kW
         */
        double basePower = 8000;



        /*
         * 模拟航行状态变化
         *
         * -2000 ~ +2000 kW
         */
        double fluctuation =
                ThreadLocalRandom.current()
                        .nextDouble(-2000, 2000);



        double power =
                basePower + fluctuation;



        /*
         * 限制合理范围
         */
        if(power < 1000){

            power = 1000;

        }


        if(power > 20000){

            power = 20000;

        }



        /*
         * 保留两位小数
         */
        return Math.round(power * 100)
                / 100.0;

    }

}