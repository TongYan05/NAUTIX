package shipsensor.simulator.writer;


import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shipsensor.entity.SensorData;
import shipsensor.inter.SensorDataMapper;
import shipsensor.simulator.model.SensorDataMessage;

import java.util.ArrayList;
import java.util.List;



@Component
public class BatchWriter {


    private final SensorDataMapper sensorDataMapper;



    public BatchWriter(
            SensorDataMapper sensorDataMapper
    ){

        this.sensorDataMapper = sensorDataMapper;

    }





    @Transactional
    public void write(
            List<SensorDataMessage> messages
    ){


        if(messages == null || messages.isEmpty()){

            return;

        }



        List<SensorData> dataList =
                new ArrayList<>(messages.size());



        for(SensorDataMessage message : messages){


            SensorData data =
                    new SensorData();



            data.setConfigId(
                    message.getConfigId()
            );


            data.setShipId(
                    message.getShipId()
            );


            data.setDataValue(
                    message.getValue()
            );


            data.setRecordedAt(
                    message.getRecordedAt()
            );


            dataList.add(data);

        }



        sensorDataMapper.insertBatch(
                dataList
        );


    }


}