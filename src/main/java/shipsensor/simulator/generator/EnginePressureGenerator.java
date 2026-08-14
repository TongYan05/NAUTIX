package shipsensor.simulator.generator;


import org.springframework.stereotype.Component;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.ShipInfo;

import java.util.concurrent.ThreadLocalRandom;


@Component
public class EnginePressureGenerator implements SensorGenerator {


    @Override
    public String getTypeCode() {

        return "ENG_PRESSURE";

    }



    @Override
    public Double generate(
            ShipInfo shipInfo,
            SensorConfig sensorConfig) {


        /*
         * 发动机油压模拟
         *
         * 正常范围:
         * 3.0 - 6.0 bar
         */


        return ThreadLocalRandom.current()
                .nextDouble(
                        3.0,
                        6.0
                );


    }

}