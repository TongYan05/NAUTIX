package shipsensor.simulator.generator;


import org.springframework.stereotype.Component;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.ShipInfo;
import shipsensor.simulator.runtime.RuntimeStateManager;
import shipsensor.simulator.runtime.ShipRuntimeState;

import java.util.concurrent.ThreadLocalRandom;


/**
 * 船舶燃油流量模拟器
 *
 * type_code:
 * FUEL_FLOW
 *
 * 单位:
 * L/h
 *
 */
@Component
public class FuelFlowGenerator
        implements SensorGenerator {


    private final RuntimeStateManager runtimeStateManager;



    public FuelFlowGenerator(
            RuntimeStateManager runtimeStateManager) {

        this.runtimeStateManager =
                runtimeStateManager;

    }



    @Override
    public String getTypeCode() {

        return "FUEL_FLOW";

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
         * 默认正常燃油消耗
         */
        double baseFlow = 400;



        /*
         * 如果存在运行状态
         * 可以根据航行状态调整
         */
        if(state != null){


            /*
             * 当前版本没有直接读取RPM
             *
             * 所以根据运行状态增加小变化
             */
            baseFlow += 50;

        }



        /*
         * 模拟负载变化
         *
         * -100 ~ +100 L/h
         */
        double fluctuation =
                ThreadLocalRandom.current()
                        .nextDouble(-100,100);



        double flow =
                baseFlow + fluctuation;



        /*
         * 限制范围
         */
        if(flow < 100){

            flow = 100;

        }


        if(flow > 1200){

            flow = 1200;

        }



        return Math.round(flow * 100.0) / 100.0;

    }

}