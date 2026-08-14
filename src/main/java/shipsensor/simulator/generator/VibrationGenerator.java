package shipsensor.simulator.generator;


import org.springframework.stereotype.Component;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.ShipInfo;
import shipsensor.simulator.runtime.RuntimeStateManager;
import shipsensor.simulator.runtime.ShipRuntimeState;

import java.util.concurrent.ThreadLocalRandom;


/**
 * 船舶机械振动模拟器
 *
 * type_code:
 * VIBRATION
 *
 * 单位:
 * mm/s
 */
@Component
public class VibrationGenerator
        implements SensorGenerator {


    private final RuntimeStateManager runtimeStateManager;



    public VibrationGenerator(
            RuntimeStateManager runtimeStateManager) {

        this.runtimeStateManager =
                runtimeStateManager;

    }



    @Override
    public String getTypeCode() {

        return "VIBRATION";

    }



    @Override
    public Double generate(
            ShipInfo shipInfo,
            SensorConfig sensorConfig) {


        ShipRuntimeState state =
                runtimeStateManager.get(
                        shipInfo.getId()
                );


        /*
         * 没有运行状态
         */
        if(state == null){

            return 0.5;

        }



        /*
         * 基础振动
         *
         * 正常船舶:
         * 约 1~2 mm/s
         */
        double baseVibration = 1.2;



        /*
         * 模拟发动机运行影响
         *
         * 随机变化
         */
        double engineEffect =
                ThreadLocalRandom.current()
                        .nextDouble(0,1.5);



        /*
         * 环境和机械微小变化
         */
        double randomEffect =
                ThreadLocalRandom.current()
                        .nextDouble(-0.3,0.3);



        double vibration =
                baseVibration
                        + engineEffect
                        + randomEffect;



        /*
         * 限制合理范围
         */
        if(vibration < 0){

            vibration = 0;

        }


        if(vibration > 8){

            vibration = 8;

        }



        return Math.round(vibration * 100)
                / 100.0;

    }

}