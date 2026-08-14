package shipsensor.simulator.task;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.SensorDict;
import shipsensor.entity.ShipInfo;
import shipsensor.simulator.alert.AlertEngine;
import shipsensor.simulator.cache.SensorConfigCache;
import shipsensor.simulator.cache.SensorDictCache;
import shipsensor.simulator.cache.ShipCache;
import shipsensor.simulator.generator.GeneratorManager;
import shipsensor.simulator.generator.SensorGenerator;
import shipsensor.simulator.model.SensorDataMessage;
import shipsensor.simulator.queue.SensorDataQueue;


import java.time.LocalDateTime;
import java.util.List;



@Component
public class SensorDataGeneratorTask {


    @Value("${simulator.sensor-data.enabled:true}")
    private boolean enabled;



    private final SensorConfigCache sensorConfigCache;

    private final SensorDictCache sensorDictCache;

    private final ShipCache shipCache;

    private final GeneratorManager generatorManager;

    private final SensorDataQueue sensorDataQueue;

    private final AlertEngine alertEngine;



    public SensorDataGeneratorTask(
            SensorConfigCache sensorConfigCache,
            SensorDictCache sensorDictCache,
            ShipCache shipCache,
            GeneratorManager generatorManager,
            SensorDataQueue sensorDataQueue,
            AlertEngine alertEngine
    ){

        this.sensorConfigCache = sensorConfigCache;
        this.sensorDictCache = sensorDictCache;
        this.shipCache = shipCache;
        this.generatorManager = generatorManager;
        this.sensorDataQueue = sensorDataQueue;
        this.alertEngine = alertEngine;

    }



    @Scheduled(
            fixedRateString = "${simulator.sensor-data.interval:1000}"
    )
    public void generate(){


        if(!enabled){

            return;

        }



        List<SensorConfig> configs =
                sensorConfigCache.getAll();



        if(configs.isEmpty()){

            return;

        }



        LocalDateTime now =
                LocalDateTime.now();



        for(SensorConfig config : configs){



            ShipInfo ship =
                    shipCache.get(
                            config.getShipId()
                    );



            if(ship == null){

                continue;

            }



            SensorDict dict =
                    sensorDictCache.get(
                            config.getTypeId()
                    );



            if(dict == null){

                continue;

            }



            SensorGenerator generator;


            try{


                generator =
                        generatorManager.getGenerator(
                                dict.getTypeCode()
                        );


            }catch(Exception e){

                continue;

            }




            Double value =
                    generator.generate(
                            ship,
                            config
                    );



            /*
             * 触发报警检测
             */
            alertEngine.check(
                    ship.getId(),
                    config.getTypeId(),
                    value
            );



            SensorDataMessage message =
                    new SensorDataMessage();



            message.setConfigId(
                    config.getId()
            );


            message.setShipId(
                    ship.getId()
            );


            message.setValue(
                    value
            );


            message.setRecordedAt(
                    now
            );



            sensorDataQueue.offer(message);


        }


    }


}