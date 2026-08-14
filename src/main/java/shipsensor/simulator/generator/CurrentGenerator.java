package shipsensor.simulator.generator;


import org.springframework.stereotype.Component;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.ShipInfo;

import java.util.concurrent.ThreadLocalRandom;


/**
 * 船舶电流模拟器
 *
 * type_code:
 * CURRENT
 *
 * 单位:
 * Ampere(A)
 *
 * 正常范围:
 * 100A ~ 800A
 */
@Component
public class CurrentGenerator
        implements SensorGenerator {


    @Override
    public String getTypeCode() {

        return "CURRENT";

    }



    @Override
    public Double generate(
            ShipInfo shipInfo,
            SensorConfig sensorConfig) {


        /*
         * 基础电流
         *
         * 假设普通商船
         */
        double baseCurrent = 350;



        /*
         * 模拟设备负载变化
         *
         * -100A ~ +100A
         */
        double fluctuation =
                ThreadLocalRandom.current()
                        .nextDouble(-100, 100);



        double current =
                baseCurrent + fluctuation;



        /*
         * 限制合理范围
         */
        if(current < 100){

            current = 100;

        }


        if(current > 800){

            current = 800;

        }



        /*
         * 保留两位小数
         */
        return Math.round(current * 100.0) / 100.0;

    }

}