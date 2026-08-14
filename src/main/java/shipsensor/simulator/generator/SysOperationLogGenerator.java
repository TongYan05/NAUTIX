package shipsensor.simulator.generator;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shipsensor.entity.SysOperationLog;
import shipsensor.inter.SysOperationLogMapper;


import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;



@Component
@RequiredArgsConstructor
public class SysOperationLogGenerator {


    private final SysOperationLogMapper sysOperationLogMapper;



    private static final String[] OPERATIONS =
            {
                    "INSERT",
                    "UPDATE",
                    "DELETE",
                    "QUERY",
                    "SYNC"
            };



    private static final String[] TABLES =
            {
                    "ship_runtime_status",
                    "sensor_data",
                    "alert_record",
                    "alert_event",
                    "ship_info"
            };





    /**
     * 每5秒生成500条系统操作日志
     */
    @Scheduled(fixedRate = 5000)
    public void generate(){


        int batchSize = 500;



        for(int i = 0; i < batchSize; i++){



            SysOperationLog log =
                    new SysOperationLog();



            log.setUserName(
                    "simulator"
            );



            log.setOperationType(
                    randomOperation()
            );



            log.setTargetTable(
                    randomTable()
            );



            log.setTargetId(
                    ThreadLocalRandom.current()
                            .nextLong(
                                    1,
                                    1000000
                            )
            );



            log.setOldValue(
                    "{\"status\":\"old\"}"
            );



            log.setNewValue(
                    "{\"status\":\"new\"}"
            );



            log.setCreateTime(
                    LocalDateTime.now()
            );



            sysOperationLogMapper.insert(
                    log
            );


        }


    }






    private String randomOperation(){


        return OPERATIONS[
                ThreadLocalRandom.current()
                        .nextInt(
                                OPERATIONS.length
                        )
                ];

    }






    private String randomTable(){


        return TABLES[
                ThreadLocalRandom.current()
                        .nextInt(
                                TABLES.length
                        )
                ];

    }


}