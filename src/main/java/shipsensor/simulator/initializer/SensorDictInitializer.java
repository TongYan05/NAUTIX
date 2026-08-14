package shipsensor.simulator.initializer;

import jakarta.annotation.PostConstruct;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import shipsensor.entity.SensorDict;
import shipsensor.inter.SensorDictMapper;


@Component
@Order(4)
public class SensorDictInitializer {


    private final SensorDictMapper sensorDictMapper;


    public SensorDictInitializer(
            SensorDictMapper sensorDictMapper
    ){

        this.sensorDictMapper = sensorDictMapper;

    }



    @PostConstruct
    public void init(){


        if(sensorDictMapper.selectCount(null) > 0){

            System.out.println(
                    "sensor_dict already exists"
            );

            return;

        }



        insertSensor(
                "SPEED",
                "Ship Speed",
                "kn",
                "Ship sailing speed sensor"
        );



        insertSensor(
                "GPS_LAT",
                "GPS Latitude",
                "degree",
                "Ship latitude position sensor"
        );



        insertSensor(
                "GPS_LON",
                "GPS Longitude",
                "degree",
                "Ship longitude position sensor"
        );



        insertSensor(
                "HEADING",
                "Ship Heading",
                "degree",
                "Ship heading direction sensor"
        );



        insertSensor(
                "FUEL_FLOW",
                "Fuel Consumption",
                "L/h",
                "Main engine fuel consumption sensor"
        );



        insertSensor(
                "ENG_TEMP",
                "Engine Temperature",
                "℃",
                "Main engine temperature sensor"
        );



        insertSensor(
                "PRESSURE",
                "Pressure",
                "bar",
                "Engine pipeline pressure sensor"
        );



        insertSensor(
                "HUMIDITY",
                "Humidity",
                "%",
                "Weather humidity sensor"
        );



        insertSensor(
                "ENGINE_RPM",
                "Engine RPM",
                "rpm",
                "Main engine rotation speed sensor"
        );



        System.out.println(
                "sensor_dict initialization finished"
        );

    }





    private void insertSensor(
            String typeCode,
            String typeName,
            String defaultUnit,
            String description
    ){


        SensorDict sensor =
                new SensorDict();



        sensor.setTypeCode(
                typeCode
        );



        sensor.setTypeName(
                typeName
        );



        sensor.setDefaultUnit(
                defaultUnit
        );



        sensor.setDescription(
                description
        );



        sensorDictMapper.insert(sensor);

    }

}