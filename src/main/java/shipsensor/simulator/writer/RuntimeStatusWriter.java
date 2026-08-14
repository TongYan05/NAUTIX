package shipsensor.simulator.writer;


import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import shipsensor.entity.ShipRuntimeStatus;
import shipsensor.inter.ShipRuntimeStatusMapper;
import shipsensor.simulator.model.RuntimeStatusMessage;
import shipsensor.simulator.queue.RuntimeStatusQueue;

import java.time.LocalDateTime;
import java.util.List;



@Component
public class RuntimeStatusWriter implements Runnable {


    private final RuntimeStatusQueue queue;

    private final ShipRuntimeStatusMapper mapper;



    public RuntimeStatusWriter(
            RuntimeStatusQueue queue,
            ShipRuntimeStatusMapper mapper
    ){

        this.queue=queue;
        this.mapper=mapper;

    }



    @PostConstruct
    public void start(){

        Thread thread =
                new Thread(
                        this,
                        "runtime-status-writer"
                );


        thread.start();

    }




    @Override
    public void run(){


        while(true){


            List<RuntimeStatusMessage> list =
                    queue.pollBatch(10000);



            for(RuntimeStatusMessage message:list){


                var state =
                        message.getState();



                ShipRuntimeStatus status =
                        new ShipRuntimeStatus();



                status.setShipId(
                        state.getShipId()
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



                mapper.insert(status);


            }


        }


    }


}