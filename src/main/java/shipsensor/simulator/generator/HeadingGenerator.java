package shipsensor.simulator.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.ShipInfo;
import shipsensor.simulator.runtime.RuntimeStateManager;
import shipsensor.simulator.runtime.ShipRuntimeState;

@Component
@RequiredArgsConstructor
public class HeadingGenerator implements SensorGenerator {

    private final RuntimeStateManager runtimeStateManager;

    @Override
    public String getTypeCode() {
        return "HEADING";
    }

    @Override
    public Double generate(ShipInfo shipInfo,
                           SensorConfig sensorConfig) {

        ShipRuntimeState state =
                runtimeStateManager.get(shipInfo.getId());

        if (state == null) {
            return 0D;
        }

        return state.getHeading();
    }

}