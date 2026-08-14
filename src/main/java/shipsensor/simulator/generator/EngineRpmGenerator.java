package shipsensor.simulator.generator;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.ShipInfo;
import shipsensor.simulator.runtime.RuntimeStateManager;
import shipsensor.simulator.runtime.ShipRuntimeState;


@Component
@RequiredArgsConstructor
public class EngineRpmGenerator implements SensorGenerator {


    private final RuntimeStateManager runtimeStateManager;


    @Override
    public String getTypeCode() {

        return "ENG_RPM";

    }


    @Override
    public Double generate(
            ShipInfo shipInfo,
            SensorConfig sensorConfig) {


        ShipRuntimeState state =
                runtimeStateManager.get(
                        shipInfo.getId()
                );


        if(state == null){

            return 80.0;

        }


        /*
         * 模拟发动机转速
         *
         * 商船:
         * 低速柴油机一般 60~120 rpm
         *
         */

        double base = 85;


        double fluctuation =
                Math.random() * 20 - 10;


        return base + fluctuation;


    }

}