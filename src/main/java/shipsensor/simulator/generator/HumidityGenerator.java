package shipsensor.simulator.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.ShipInfo;
import shipsensor.entity.WeatherRegion;
import shipsensor.simulator.cache.WeatherCache;
import shipsensor.simulator.engine.SensorCorrelationEngine;
import shipsensor.simulator.runtime.RuntimeStateManager;
import shipsensor.simulator.runtime.ShipRuntimeState;

@Component
@RequiredArgsConstructor
public class HumidityGenerator implements SensorGenerator {

    private final RuntimeStateManager runtimeStateManager;

    private final WeatherCache weatherCache;

    private final SensorCorrelationEngine correlationEngine;

    @Override
    public String getTypeCode() {
        return "HUMIDITY";
    }

    @Override
    public Double generate(ShipInfo shipInfo,
                           SensorConfig sensorConfig) {

        ShipRuntimeState state =
                runtimeStateManager.get(shipInfo.getId());

        if (state == null) {
            return 60D;
        }

        WeatherRegion weather =
                weatherCache.get(state.getWeatherId());

        if (weather == null) {
            return 60D;
        }

        return correlationEngine.calculateHumidity(weather);

    }

}