package shipsensor.simulator.util;


import java.util.concurrent.ThreadLocalRandom;


/**
 * 所有传感器计算公式
 */
public final class SensorMath {



    private SensorMath(){

    }






    /**
     * 下一时刻速度
     *
     * @param current 当前速度
     * @param target 目标速度
     */
    public static double nextSpeed(
            double current,
            double target
    ){


        double diff =
                target-current;



        /*
         * 每次最多变化0.3节
         */
        if(diff>0.3){

            diff=0.3;

        }


        if(diff<-0.3){

            diff=-0.3;

        }



        return current+diff;


    }








    /**
     * 发动机负载
     */
    public static double calculateEngineLoad(
            double speed,
            double maxSpeed
    ){


        if(maxSpeed<=0){

            return 0;

        }



        double load =
                speed/maxSpeed*100;



        return limit(
                load,
                0,
                100
        );


    }









    /**
     * 油耗计算
     */
    public static double calculateFuelConsumption(
            double speed,
            double load
    ){


        /*
         * 基础油耗
         */
        double fuel =
                10;



        /*
         * 航速影响
         */
        fuel += speed*1.5;



        /*
         * 负载影响
         */
        fuel += load*0.8;



        return fuel;


    }









    /**
     * 温度变化
     */
    public static double nextTemperature(
            double current,
            double target
    ){


        double diff =
                target-current;



        if(diff>1){

            diff=1;

        }


        if(diff<-1){

            diff=-1;

        }



        return current
                +
                diff
                +
                noise(0.3);


    }









    /**
     * GPS微小误差
     *
     * 模拟真实GPS漂移
     */
    public static double gpsNoise(
            double value
    ){


        return value+
                noise(0.0001);


    }









    /**
     * 传感器随机误差
     */
    public static double addNoise(
            double value,
            double range
    ){


        return value+
                noise(range);


    }









    /**
     * 随机扰动
     */
    private static double noise(
            double range
    ){


        return ThreadLocalRandom.current()
                .nextDouble(
                        -range,
                        range
                );


    }









    /**
     * 限制范围
     */
    private static double limit(
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