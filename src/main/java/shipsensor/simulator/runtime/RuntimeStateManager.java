package shipsensor.simulator.runtime;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shipsensor.entity.ShipRuntimeStatus;
import shipsensor.inter.ShipRuntimeStatusMapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@RequiredArgsConstructor
public class RuntimeStateManager {


    private final ShipRuntimeStatusMapper runtimeStatusMapper;



    private final Map<Integer, ShipRuntimeState> stateMap =
            new ConcurrentHashMap<>();



    @PostConstruct
    public void init() {


        var list =
                runtimeStatusMapper.selectList(null);



        if(list == null || list.isEmpty()){

            System.out.println(
                    "RuntimeState loaded: 0"
            );

            return;

        }



        for(ShipRuntimeStatus status : list){


            ShipRuntimeState state =
                    new ShipRuntimeState();



            state.setShipId(
                    status.getShipId()
            );


            state.setLatitude(
                    status.getLatitude()
            );


            state.setLongitude(
                    status.getLongitude()
            );


            state.setSpeed(
                    status.getSpeed()
            );


            state.setHeading(
                    status.getHeading()
            );


            state.setEngineLoad(
                    status.getEngineLoad()
            );


            state.setFuelPercent(
                    status.getFuelPercent()
            );


            state.setWeatherId(
                    status.getWeatherId()
            );


            state.setCurrentPoint(0);



            stateMap.put(
                    state.getShipId(),
                    state
            );

        }



        System.out.println(
                "RuntimeState loaded : "
                        +
                        stateMap.size()
        );


    }




    public Collection<ShipRuntimeState> getAll(){

        return stateMap.values();

    }





    public ShipRuntimeState get(
            Integer shipId
    ){

        return stateMap.get(shipId);

    }





    public void put(
            ShipRuntimeState state
    ){

        if(state == null ||
                state.getShipId() == null){

            return;

        }


        stateMap.put(
                state.getShipId(),
                state
        );

    }





    public void save(
            ShipRuntimeState state
    ){


        if(state == null){

            return;

        }



        ShipRuntimeStatus status =
                new ShipRuntimeStatus();



        status.setShipId(
                state.getShipId()
        );


        status.setSailingStatus(
                state.getMode() == null ?
                        "UNKNOWN" :
                        state.getMode().name()
        );


        status.setLatitude(
                state.getLatitude()
        );


        status.setLongitude(
                state.getLongitude()
        );


        status.setSpeed(
                state.getSpeed()
        );


        status.setHeading(
                state.getHeading()
        );


        status.setEngineLoad(
                state.getEngineLoad()
        );


        status.setFuelPercent(
                state.getFuelPercent()
        );


        status.setWeatherId(
                state.getWeatherId()
        );


        status.setLastUpdate(
                LocalDateTime.now()
        );



        ShipRuntimeStatus old =
                runtimeStatusMapper.selectByShipId(
                        state.getShipId()
                );



        if(old == null){

            runtimeStatusMapper.insert(status);

        }else{

            runtimeStatusMapper.update(status);

        }


    }





    public void saveAll(){


        for(ShipRuntimeState state :
                stateMap.values()){


            save(state);

        }

    }





    public int size(){

        return stateMap.size();

    }


}