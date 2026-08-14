package shipsensor.simulator.engine;


import org.springframework.stereotype.Component;
import shipsensor.entity.WeatherRegion;
import shipsensor.simulator.runtime.ShipRuntimeState;



@Component
public class SensorCorrelationEngine {



    /**
     * 发动机温度
     */
    public double calculateEngineTemp(
            ShipRuntimeState ship,
            WeatherRegion weather
    ){


        double temp = 65;



        // 发动机负载影响
        temp += ship.getEngineLoad()
                *0.5;



        // 环境温度影响
        if(weather!=null){

            temp += weather.getAirTemperature()
                    *0.2;


            // 海浪增加散热压力
            temp += weather.getWaveHeight()
                    *1.5;

        }



        return limit(
                temp,
                60,
                120
        );

    }







    /**
     * 排气温度
     */
    public double calculateExhaustTemp(
            ShipRuntimeState ship,
            WeatherRegion weather
    ){


        double temp = 220;


        temp += ship.getEngineLoad()
                *2.8;



        if(weather!=null){

            temp += weather.getAirTemperature()
                    *0.4;

        }



        return limit(
                temp,
                200,
                550
        );


    }








    /**
     * 主机转速 RPM
     */
    public double calculateRpm(
            ShipRuntimeState ship
    ){


        return ship.getSpeed()
                *
                120;


    }








    /**
     * 油压
     */
    public double calculatePressure(
            ShipRuntimeState ship
    ){


        double pressure =
                0.3
                        +
                        ship.getEngineLoad()
                                *
                                0.007;



        return limit(
                pressure,
                0.3,
                1.5
        );


    }









    /**
     * 燃油流量 L/h
     */
    public double calculateFuelFlow(
            ShipRuntimeState ship
    ){


        double speed =
                ship.getSpeed();



        double load =
                ship.getEngineLoad();



        /*
         * 基础消耗
         */
        double fuel =
                15;



        /*
         * 速度影响
         */
        fuel += speed
                *
                2;



        /*
         * 负载影响
         */
        fuel += load
                *
                1.2;



        return fuel;


    }








    /**
     * 剩余燃油
     */
    public double calculateFuelLevel(
            ShipRuntimeState ship
    ){


        return ship.getFuelPercent();


    }









    /**
     * 振动
     */
    public double calculateVibration(
            ShipRuntimeState ship,
            WeatherRegion weather
    ){


        double vibration =
                0.5;



        vibration +=
                ship.getEngineLoad()
                        *
                        0.05;



        if(weather!=null){

            vibration +=
                    weather.getWaveHeight()
                            *
                            1.2;

        }



        return limit(
                vibration,
                0.5,
                20
        );


    }








    /**
     * 海水温度
     */
    public double calculateSeaTemperature(
            WeatherRegion weather
    ){


        if(weather==null){

            return 20;

        }


        return weather.getSeaTemperature();


    }








    /**
     * 湿度
     */
    public double calculateHumidity(
            WeatherRegion weather
    ){


        if(weather==null){

            return 60;

        }


        return weather.getHumidity();


    }









    /**
     * 管路压力
     */
    public double calculatePipePressure(
            ShipRuntimeState ship
    ){


        return limit(
                0.5
                        +
                        ship.getEngineLoad()
                                *
                                0.005,
                0.5,
                1.3
        );


    }









    /**
     * 功率 kW
     */
    public double calculatePower(
            ShipRuntimeState ship
    ){


        return ship.getEngineLoad()
                *
                15;


    }









    /**
     * 电流 A
     */
    public double calculateCurrent(
            ShipRuntimeState ship
    ){


        return ship.getEngineLoad()
                *
                6;


    }








    /**
     * 电压
     */
    public double calculateVoltage(){


        return 24.0;


    }







    /**
     * 限制范围
     */
    private double limit(
            double value,
            double min,
            double max
    ){


        if(value<min){

            return min;

        }


        if(value>max){

            return max;

        }


        return value;


    }


}