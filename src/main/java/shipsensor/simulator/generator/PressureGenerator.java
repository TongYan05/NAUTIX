package shipsensor.simulator.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.ShipInfo;
import shipsensor.simulator.engine.SensorCorrelationEngine;
import shipsensor.simulator.runtime.RuntimeStateManager;
import shipsensor.simulator.runtime.ShipRuntimeState;

@Component
@RequiredArgsConstructor
public class PressureGenerator implements SensorGenerator {

    private final RuntimeStateManager runtimeStateManager;

    private final SensorCorrelationEngine correlationEngine;

    @Override
    public String getTypeCode() {
        return "PRESSURE";
    }

    @Override
    public Double generate(ShipInfo shipInfo,
                           SensorConfig sensorConfig) {

        ShipRuntimeState state =
                runtimeStateManager.get(shipInfo.getId());

        if (state == null) {
            return 0.5;
        }

        return correlationEngine.calculatePipePressure(state);

    }

}