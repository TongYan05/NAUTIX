package shipsensor.simulator.generator;


import org.springframework.stereotype.Component;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.ShipInfo;

import java.util.concurrent.ThreadLocalRandom;


@Component
public class ExhaustTempGenerator
        implements SensorGenerator {


    @Override
    public String getTypeCode() {

        return "EXHAUST_TEMP";

    }



    @Override
    public Double generate(
            ShipInfo shipInfo,
            SensorConfig sensorConfig) {


        /*
         * 船舶柴油机排气温度
         *
         * 正常:
         * 300 - 500 ℃
         */


        return ThreadLocalRandom.current()
                .nextDouble(320, 450);

    }

}