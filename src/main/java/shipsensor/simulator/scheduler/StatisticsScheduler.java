package shipsensor.simulator.scheduler;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shipsensor.inter.AlertEventMapper;
import shipsensor.inter.AlertRecordMapper;
import shipsensor.inter.SysOperationLogMapper;
import shipsensor.simulator.cache.RouteCache;
import shipsensor.simulator.cache.WeatherCache;
import shipsensor.simulator.queue.SensorDataQueue;
import shipsensor.simulator.runtime.RuntimeStateManager;



@Component
@RequiredArgsConstructor
public class StatisticsScheduler {


    private final RuntimeStateManager runtimeStateManager;


    private final SensorDataQueue sensorDataQueue;


    private final WeatherCache weatherCache;


    private final RouteCache routeCache;


    private final AlertEventMapper alertEventMapper;


    private final AlertRecordMapper alertRecordMapper;


    private final SysOperationLogMapper sysOperationLogMapper;





    @Scheduled(fixedRate = 5000)
    public void statistics(){



        int shipCount =
                runtimeStateManager
                        .size();



        int queueSize =
                sensorDataQueue
                        .size();



        long generated =
                sensorDataQueue
                        .getGeneratedCount();



        long inserted =
                sensorDataQueue
                        .getInsertedCount();



        int weatherCount =
                weatherCache
                        .getWeatherList()
                        .size();



        int routeCount =
                routeCache
                        .getRoutes()
                        .size();



        long alertRecordCount =
                alertRecordMapper
                        .selectCount(null);



        long alertEventCount =
                alertEventMapper
                        .selectCount(null);



        long logCount =
                sysOperationLogMapper
                        .selectCount(null);





        System.out.println(
                """
                ==============================
                Simulator Statistics


                Ships Runtime       : %d

                Sensor Generated    : %d
                Sensor Inserted     : %d
                Sensor Queue        : %d


                Weather Region      : %d
                Routes              : %d


                Alert Record        : %d
                Alert Event         : %d

                Operation Log       : %d


                ==============================
                """
                        .formatted(
                                shipCount,
                                generated,
                                inserted,
                                queueSize,
                                weatherCount,
                                routeCount,
                                alertRecordCount,
                                alertEventCount,
                                logCount
                        )
        );


    }


}