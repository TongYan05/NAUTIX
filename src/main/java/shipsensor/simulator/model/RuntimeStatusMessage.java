package shipsensor.simulator.model;


import lombok.Data;
import shipsensor.simulator.runtime.ShipRuntimeState;


@Data
public class RuntimeStatusMessage {


    private ShipRuntimeState state;


}