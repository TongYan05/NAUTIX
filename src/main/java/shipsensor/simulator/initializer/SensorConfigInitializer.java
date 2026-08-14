package shipsensor.simulator.initializer;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.SensorDict;
import shipsensor.entity.ShipInfo;
import shipsensor.inter.SensorConfigMapper;
import shipsensor.inter.SensorDictMapper;
import shipsensor.inter.ShipInfoMapper;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Component
@Order(5)
public class SensorConfigInitializer {


    private final SensorConfigMapper sensorConfigMapper;

    private final ShipInfoMapper shipInfoMapper;

    private final SensorDictMapper sensorDictMapper;



    @Value("${simulator.sensor-config.enabled:false}")
    private boolean enabled;



    public SensorConfigInitializer(
            SensorConfigMapper sensorConfigMapper,
            ShipInfoMapper shipInfoMapper,
            SensorDictMapper sensorDictMapper
    ){

        this.sensorConfigMapper = sensorConfigMapper;

        this.shipInfoMapper = shipInfoMapper;

        this.sensorDictMapper = sensorDictMapper;

    }




    @PostConstruct
    public void init(){


        if(!enabled){

            System.out.println(
                    "sensor-config initializer disabled"
            );

            return;

        }




        if(sensorConfigMapper.selectCount(null)>0){

            System.out.println(
                    "sensor_config already exists"
            );

            return;

        }





        List<ShipInfo> ships =
                shipInfoMapper.selectList(null);



        List<SensorDict> sensors =
                sensorDictMapper.selectList(null);




        if(ships.isEmpty()){

            throw new RuntimeException(
                    "ship_info is empty"
            );

        }



        if(sensors.isEmpty()){

            throw new RuntimeException(
                    "sensor_dict is empty"
            );

        }






        Map<String,Integer> sensorMap =
                new HashMap<>();


        for(SensorDict sensor:sensors){


            sensorMap.put(
                    sensor.getTypeCode(),
                    sensor.getId()
            );


        }





        List<String> sensorTypes =
                List.of(

                        "SPEED",
                        "GPS_LAT",
                        "GPS_LON",
                        "HEADING",

                        "ENGINE_RPM",
                        "ENGINE_TEMP",
                        "ENGINE_PRESSURE",

                        "FUEL_FLOW",
                        "FUEL_LEVEL",

                        "OIL_PRESSURE",
                        "OIL_TEMP",

                        "VIBRATION",

                        "BATTERY_VOLTAGE",

                        "POWER",

                        "SEA_WATER_TEMP",

                        "HUMIDITY",

                        "AIR_PRESSURE",

                        "WIND_SPEED",

                        "WIND_DIRECTION"

                );







        List<SensorConfig> configs =
                new ArrayList<>();





        for(ShipInfo ship:ships){



            for(String type:sensorTypes){



                Integer typeId =
                        sensorMap.get(type);



                if(typeId==null){

                    continue;

                }





                SensorConfig config =
                        new SensorConfig();




                config.setShipId(
                        ship.getId()
                );



                config.setTypeId(
                        typeId
                );



                config.setSensorName(
                        ship.getShipName()
                                +
                                "-"
                                +
                                type
                );



                config.setInstallDate(
                        LocalDate.now()
                );



                config.setStatus(1);




                configs.add(config);





                if(configs.size()>=500){


                    insertBatch(configs);


                    configs.clear();


                }


            }


        }






        if(!configs.isEmpty()){


            insertBatch(configs);


        }




        System.out.println(
                "sensor_config initialization finished"
        );


    }






    private void insertBatch(
            List<SensorConfig> configs
    ){


        for(SensorConfig config:configs){


            sensorConfigMapper.insert(config);


        }


    }



}