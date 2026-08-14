package shipsensor.simulator.generator;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.ShipInfo;
import shipsensor.entity.WeatherRegion;
import shipsensor.simulator.cache.WeatherCache;
import shipsensor.simulator.runtime.RuntimeStateManager;
import shipsensor.simulator.runtime.ShipRuntimeState;

import java.util.concurrent.ThreadLocalRandom;


/**
 * 海水温度模拟器
 *
 * type_code:
 * SEA_WATER_TEMP
 *
 * 单位:
 * ℃
 */
@Component
@RequiredArgsConstructor
public class SeaWaterTempGenerator
        implements SensorGenerator {


    private final RuntimeStateManager runtimeStateManager;

    private final WeatherCache weatherCache;



    @Override
    public String getTypeCode() {

        return "SEA_WATER_TEMP";

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

            return 18.0;

        }



        WeatherRegion weather =
                weatherCache.get(
                        state.getWeatherId()
                );


        /*
         * 没有天气区域
         */
        if(weather == null){

            return 18.0;

        }



        /*
         * 基础海温
         *
         * 全球海水:
         * 5℃ ~ 30℃
         *
         * 默认:
         * 18℃
         */
        double baseTemp = 18.0;



        /*
         * 模拟区域影响
         *
         * 不同区域:
         * -3 ~ +5℃
         */
        double regionEffect =
                ThreadLocalRandom.current()
                        .nextDouble(-3,5);



        /*
         * 短时间波动
         */
        double fluctuation =
                ThreadLocalRandom.current()
                        .nextDouble(-0.5,0.5);



        double temperature =
                baseTemp
                        + regionEffect
                        + fluctuation;



        /*
         * 合理范围限制
         */
        if(temperature < 2){

            temperature = 2;

        }


        if(temperature > 32){

            temperature = 32;

        }



        return Math.round(temperature * 100)
                / 100.0;

    }

}