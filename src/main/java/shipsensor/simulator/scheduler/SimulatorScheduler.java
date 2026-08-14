package shipsensor.simulator.scheduler;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.SensorDict;
import shipsensor.entity.ShipInfo;
import shipsensor.simulator.alert.AlertEngine;
import shipsensor.simulator.cache.SimulatorCacheManager;
import shipsensor.simulator.generator.GeneratorManager;
import shipsensor.simulator.generator.SensorGenerator;
import shipsensor.simulator.model.SensorDataMessage;
import shipsensor.simulator.queue.SensorDataQueue;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;



@Component
public class SimulatorScheduler {


    private final SimulatorCacheManager cacheManager;


    private final GeneratorManager generatorManager;


    private final SensorDataQueue queue;


    private final ExecutorService executor;


    private final AlertEngine alertEngine;



    private final int threadCount =
            Runtime.getRuntime()
                    .availableProcessors();





    public SimulatorScheduler(
            SimulatorCacheManager cacheManager,
            GeneratorManager generatorManager,
            SensorDataQueue queue,
            ExecutorService executor,
            AlertEngine alertEngine
    ){

        this.cacheManager = cacheManager;
        this.generatorManager = generatorManager;
        this.queue = queue;
        this.executor = executor;
        this.alertEngine = alertEngine;

    }






    @Scheduled(fixedRate = 1000)
    public void generateSensorData(){


        List<ShipInfo> ships =
                new ArrayList<>(
                        cacheManager.getAllShips()
                );



        int size =
                ships.size();



        int batch =
                (size + threadCount - 1)
                        /
                        threadCount;



        LocalDateTime now =
                LocalDateTime.now();





        for(int i = 0; i < threadCount; i++){


            int start =
                    i * batch;


            int end =
                    Math.min(
                            start + batch,
                            size
                    );



            if(start >= end){

                continue;

            }



            executor.submit(
                    () ->
                            generate(
                                    ships,
                                    start,
                                    end,
                                    now
                            )
            );


        }


    }







    private void generate(
            List<ShipInfo> ships,
            int start,
            int end,
            LocalDateTime time
    ){



        for(int i = start; i < end; i++){


            ShipInfo ship =
                    ships.get(i);



            List<SensorConfig> configs =
                    cacheManager.getSensorConfigs(
                            ship.getId()
                    );



            for(SensorConfig config : configs){



                SensorDict dict =
                        cacheManager.getSensorDict(
                                config.getTypeId()
                        );



                if(dict == null){

                    continue;

                }



                SensorGenerator generator =
                        generatorManager.getGenerator(
                                dict.getTypeCode()
                        );



                Double value =
                        generator.generate(
                                ship,
                                config
                        );



                /*
                 * 报警检测
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
                        time
                );


                queue.offer(message);


            }


        }


    }


}