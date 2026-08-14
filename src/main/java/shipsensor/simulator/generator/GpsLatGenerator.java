package shipsensor.simulator.generator;


import org.springframework.stereotype.Component;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.ShipInfo;
import shipsensor.simulator.runtime.RuntimeStateManager;
import shipsensor.simulator.runtime.ShipRuntimeState;



@Component
public class GpsLatGenerator implements SensorGenerator {


    private final RuntimeStateManager runtimeStateManager;



    public GpsLatGenerator(
            RuntimeStateManager runtimeStateManager
    ){

        this.runtimeStateManager = runtimeStateManager;

    }



    @Override
    public String getTypeCode() {

        return "GPS_LAT";

    }



    @Override
    public Double generate(
            ShipInfo shipInfo,
            SensorConfig sensorConfig
    ) {


        ShipRuntimeState state =
                runtimeStateManager.get(
                        shipInfo.getId()
                );


        if(state == null){

            return 0.0;

        }



        return state.getLatitude();

    }


}