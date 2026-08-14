package shipsensor.simulator.generator;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shipsensor.entity.AlertEvent;
import shipsensor.entity.AlertRecord;
import shipsensor.entity.AlertRule;
import shipsensor.entity.ShipRuntimeStatus;
import shipsensor.inter.AlertEventMapper;
import shipsensor.inter.AlertRecordMapper;
import shipsensor.inter.AlertRuleMapper;
import shipsensor.inter.ShipRuntimeStatusMapper;
import shipsensor.simulator.writer.OperationLogWriter;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;



@Component
@RequiredArgsConstructor
public class AlertDataGenerator {


    private final AlertRuleMapper alertRuleMapper;


    private final AlertRecordMapper alertRecordMapper;


    private final AlertEventMapper alertEventMapper;


    private final ShipRuntimeStatusMapper runtimeStatusMapper;


    private final OperationLogWriter operationLogWriter;




    /**
     * 每5秒生成报警数据
     */
    @Scheduled(fixedRate = 5000)
    public void generate(){


        List<ShipRuntimeStatus> ships =
                runtimeStatusMapper.selectList(null);



        if(ships.isEmpty()){

            return;

        }



        List<AlertRule> rules =
                alertRuleMapper.selectList(null);



        if(rules.isEmpty()){

            return;

        }



        ThreadLocalRandom random =
                ThreadLocalRandom.current();




        for(ShipRuntimeStatus ship : ships){



            /*
             * 5%概率产生报警
             */
            if(random.nextInt(100) > 5){

                continue;

            }




            AlertRule rule =
                    rules.get(
                            random.nextInt(
                                    rules.size()
                            )
                    );



            double value =
                    generateValue(rule);





            /*
             * 生成 alert_record
             */
            AlertRecord record =
                    new AlertRecord();



            record.setShipId(
                    ship.getShipId()
            );


            record.setRuleId(
                    rule.getId()
            );


            record.setTriggerValue(
                    BigDecimal.valueOf(value)
            );


            record.setAlertTime(
                    LocalDateTime.now()
            );


            record.setHandleStatus(
                    0
            );



            alertRecordMapper.insert(record);



            /*
             * 写入系统操作日志
             */
            operationLogWriter.write(
                    "INSERT",
                    "alert_record",
                    record.getId()
            );






            /*
             * 生成 alert_event
             */
            AlertEvent event =
                    new AlertEvent();



            event.setShipId(
                    ship.getShipId()
            );


            event.setRuleId(
                    rule.getId()
            );


            event.setStartTime(
                    LocalDateTime.now()
            );


            event.setMaxValue(
                    value
            );


            event.setStatus(
                    1
            );



            alertEventMapper.insert(event);



            /*
             * 写入系统操作日志
             */
            operationLogWriter.write(
                    "INSERT",
                    "alert_event",
                    event.getId()
            );



        }


    }







    private double generateValue(
            AlertRule rule
    ){


        double threshold =
                rule.getThresholdValue()
                        .doubleValue();



        return switch(rule.getOperator()){


            case ">" ->

                    threshold +
                            ThreadLocalRandom.current()
                                    .nextDouble(
                                            1,
                                            20
                                    );



            case "<" ->

                    threshold -
                            ThreadLocalRandom.current()
                                    .nextDouble(
                                            1,
                                            20
                                    );



            default ->

                    threshold;


        };


    }


}