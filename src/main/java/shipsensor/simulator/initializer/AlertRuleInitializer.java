package shipsensor.simulator.initializer;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import shipsensor.entity.AlertRule;
import shipsensor.entity.SensorDict;
import shipsensor.inter.AlertRuleMapper;
import shipsensor.inter.SensorDictMapper;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Component
@RequiredArgsConstructor
@Order(7)
public class AlertRuleInitializer {


    private final AlertRuleMapper alertRuleMapper;


    private final SensorDictMapper sensorDictMapper;



    @PostConstruct
    public void init(){


        if(alertRuleMapper.selectCount(null)>0){

            System.out.println(
                    "Alert rules already exist"
            );

            return;
        }



        List<SensorDict> sensors =
                sensorDictMapper.selectList(null);



        Map<String,Integer> sensorMap =
                sensors.stream()
                        .collect(
                                Collectors.toMap(
                                        SensorDict::getTypeCode,
                                        SensorDict::getId
                                )
                        );



        List<AlertRule> rules =
                new ArrayList<>();



        /*
         *
         * name
         * typeCode
         * threshold
         * operator
         * duration
         * level
         *
         */
        String[][] ruleData = {


                // =========================
                // Engine Temperature
                // =========================

                {"Engine Temperature Critical High","ENG_TEMP","120",">","5","4"},
                {"Engine Temperature High","ENG_TEMP","90",">","10","3"},
                {"Engine Temperature Warning High","ENG_TEMP","80",">","10","2"},
                {"Engine Temperature Normal","ENG_TEMP","60",">","0","1"},
                {"Engine Temperature Low","ENG_TEMP","40","<","10","2"},
                {"Engine Temperature Critical Low","ENG_TEMP","20","<","5","4"},


                // =========================
                // Pressure
                // =========================

                {"Pressure Critical High","PRESSURE","150",">","5","4"},
                {"Pressure High","PRESSURE","120",">","10","3"},
                {"Pressure Warning High","PRESSURE","100",">","10","2"},
                {"Pressure Normal","PRESSURE","80",">","0","1"},
                {"Pressure Low","PRESSURE","30","<","10","3"},
                {"Pressure Critical Low","PRESSURE","15","<","5","4"},



                // =========================
                // RPM
                // =========================

                {"RPM Critical High","ENGINE_RPM","3500",">","5","4"},
                {"RPM High","ENGINE_RPM","2500",">","10","3"},
                {"RPM Warning High","ENGINE_RPM","2200",">","10","2"},
                {"RPM Normal","ENGINE_RPM","1500",">","0","1"},
                {"RPM Low","ENGINE_RPM","500","<","10","2"},
                {"RPM Critical Low","ENGINE_RPM","200","<","5","4"},



                // =========================
                // Fuel Level
                // =========================

                {"Fuel Critical Low","FUEL_LEVEL","10","<","10","4"},
                {"Fuel Low","FUEL_LEVEL","20","<","60","3"},
                {"Fuel Warning Low","FUEL_LEVEL","35","<","30","2"},
                {"Fuel Normal","FUEL_LEVEL","50",">","0","1"},
                {"Fuel High","FUEL_LEVEL","95",">","10","2"},
                {"Fuel Overflow","FUEL_LEVEL","100",">","5","4"},



                // =========================
                // Fuel Flow
                // =========================

                {"Fuel Flow Critical High","FUEL_FLOW","800",">","5","4"},
                {"Fuel Flow High","FUEL_FLOW","500",">","10","3"},
                {"Fuel Flow Warning High","FUEL_FLOW","400",">","10","2"},
                {"Fuel Flow Normal","FUEL_FLOW","200",">","0","1"},
                {"Fuel Flow Low","FUEL_FLOW","20","<","10","2"},
                {"Fuel Flow Critical Low","FUEL_FLOW","5","<","5","4"},



                // =========================
                // Vibration
                // =========================

                {"Vibration Critical High","VIBRATION","12",">","5","4"},
                {"Vibration High","VIBRATION","8",">","5","3"},
                {"Vibration Warning High","VIBRATION","6",">","10","2"},
                {"Vibration Normal","VIBRATION","3",">","0","1"},
                {"Vibration Low","VIBRATION","1","<","5","2"},
                {"Vibration Critical Low","VIBRATION","0.2","<","5","4"},



                // =========================
                // Speed
                // =========================

                {"Speed Critical High","SPEED","35",">","5","4"},
                {"Speed High","SPEED","25",">","10","3"},
                {"Speed Warning High","SPEED","20",">","10","2"},
                {"Speed Normal","SPEED","10",">","0","1"},
                {"Speed Low","SPEED","2","<","60","2"},
                {"Speed Critical Low","SPEED","0.5","<","30","4"},



                // =========================
                // Humidity
                // =========================

                {"Humidity Critical High","HUMIDITY","98",">","5","4"},
                {"Humidity High","HUMIDITY","90",">","10","3"},
                {"Humidity Warning High","HUMIDITY","80",">","10","2"},
                {"Humidity Normal","HUMIDITY","50",">","0","1"},
                {"Humidity Low","HUMIDITY","20","<","10","2"},
                {"Humidity Critical Low","HUMIDITY","5","<","5","4"},



                // =========================
                // Battery Voltage
                // =========================

                {"Battery Critical High","BATTERY_VOLT","35",">","10","4"},
                {"Battery High","BATTERY_VOLT","30",">","10","3"},
                {"Battery Warning High","BATTERY_VOLT","28",">","10","2"},
                {"Battery Normal","BATTERY_VOLT","24",">","0","1"},
                {"Battery Low","BATTERY_VOLT","20","<","30","3"},
                {"Battery Critical Low","BATTERY_VOLT","15","<","10","4"},



                // =========================
                // Sea Water Temperature
                // =========================

                {"Sea Water Temp Critical High","SEA_WATER_TEMP","40",">","5","4"},
                {"Sea Water Temp High","SEA_WATER_TEMP","35",">","10","3"},
                {"Sea Water Temp Warning High","SEA_WATER_TEMP","30",">","10","2"},
                {"Sea Water Temp Normal","SEA_WATER_TEMP","20",">","0","1"},
                {"Sea Water Temp Low","SEA_WATER_TEMP","5","<","10","2"},



                // =========================
                // Power
                // =========================

                {"Power Critical High","POWER","10000",">","5","4"},
                {"Power High","POWER","8000",">","10","3"},
                {"Power Warning High","POWER","6000",">","10","2"},
                {"Power Normal","POWER","3000",">","0","1"},
                {"Power Low","POWER","500","<","10","2"},



                // =========================
                // Current
                // =========================

                {"Current Critical High","CURRENT","500",">","5","4"},
                {"Current High","CURRENT","300",">","10","3"},
                {"Current Warning High","CURRENT","200",">","10","2"},
                {"Current Normal","CURRENT","100",">","0","1"},
                {"Current Low","CURRENT","10","<","10","2"},



                // =========================
                // Exhaust Temperature
                // =========================

                {"Exhaust Temp Critical High","EXHAUST_TEMP","700",">","5","4"},
                {"Exhaust Temp High","EXHAUST_TEMP","500",">","10","3"},
                {"Exhaust Temp Warning High","EXHAUST_TEMP","400",">","10","2"},
                {"Exhaust Temp Normal","EXHAUST_TEMP","200",">","0","1"},
                {"Exhaust Temp Low","EXHAUST_TEMP","50","<","10","2"},



                // =========================
                // GPS
                // =========================

                {"GPS Latitude Abnormal","GPS_LAT","90",">","5","3"},
                {"GPS Latitude Negative","GPS_LAT","-90","<","5","3"},
                {"GPS Longitude Abnormal","GPS_LON","180",">","5","3"},
                {"GPS Longitude Negative","GPS_LON","-180","<","5","3"},



                // =========================
                // Heading
                // =========================

                {"Heading Invalid High","HEADING","360",">","5","3"},
                {"Heading Invalid Low","HEADING","0","<","5","3"},


        };



        for(String[] data : ruleData){


            add(
                    rules,
                    sensorMap,
                    data[0],
                    data[1],
                    Double.parseDouble(data[2]),
                    data[3],
                    Integer.parseInt(data[4]),
                    Integer.parseInt(data[5])
            );

        }



        alertRuleMapper.insertBatch(
                rules
        );


        System.out.println(
                "Alert rules initialized : "
                        +
                        rules.size()
        );

    }





    private void add(
            List<AlertRule> list,
            Map<String,Integer> map,
            String name,
            String type,
            double threshold,
            String operator,
            int duration,
            int level
    ){


        Integer typeId =
                map.get(type);



        if(typeId==null){

            return;

        }



        AlertRule rule =
                new AlertRule();



        rule.setRuleName(name);


        rule.setTypeId(typeId);


        rule.setThresholdValue(
                BigDecimal.valueOf(threshold)
        );


        rule.setOperator(operator);


        rule.setDurationSeconds(duration);


        rule.setAlertLevel(level);



        list.add(rule);

    }

}