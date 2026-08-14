package shipsensor.simulator.factory;


import org.springframework.stereotype.Component;
import shipsensor.entity.ShipInfo;
import shipsensor.simulator.runtime.ShipMode;
import shipsensor.simulator.runtime.ShipRuntimeState;

import java.util.concurrent.ThreadLocalRandom;



@Component
public class RuntimeStateFactory {


    public ShipRuntimeState create(
            ShipInfo ship,
            Long weatherId
    ){


        ShipRuntimeState state =
                new ShipRuntimeState();



        state.setShipId(
                ship.getId()
        );



        ShipMode mode =
                randomMode();



        state.setMode(
                mode
        );



        double speed =
                generateSpeed(
                        ship,
                        mode
                );



        state.setSpeed(
                speed
        );



        state.setLatitude(
                random(
                        -60,
                        60
                )
        );



        state.setLongitude(
                random(
                        -180,
                        180
                )
        );



        state.setHeading(
                random(
                        0,
                        360
                )
        );



        state.setEngineLoad(
                calculateEngineLoad(speed)
        );



        state.setFuelPercent(
                random(
                        20,
                        100
                )
        );



        state.setWeatherId(
                weatherId
        );



        state.setCurrentPoint(
                0
        );



        return state;

    }







    private ShipMode randomMode(){


        int value =
                ThreadLocalRandom.current()
                        .nextInt(100);



        if(value < 5){

            return ShipMode.STOP;

        }



        if(value < 12){

            return ShipMode.ANCHORING;

        }



        if(value < 25){

            return ShipMode.LEAVING_PORT;

        }



        if(value < 80){

            return ShipMode.CRUISING;

        }



        if(value < 92){

            return ShipMode.TURNING;

        }



        return ShipMode.ARRIVING;

    }









    private double generateSpeed(
            ShipInfo ship,
            ShipMode mode
    ){



        if(mode == ShipMode.STOP ||
                mode == ShipMode.ANCHORING){

            return random(
                    0,
                    1
            );

        }



        if(mode == ShipMode.LEAVING_PORT){

            return random(
                    3,
                    8
            );

        }



        if(mode == ShipMode.ARRIVING){

            return random(
                    2,
                    10
            );

        }



        if(mode == ShipMode.TURNING){

            return random(
                    5,
                    15
            );

        }





        if(ship.getShipType()==null){

            return random(
                    10,
                    18
            );

        }





        return switch(ship.getShipType()){


            case "Container" ->
                    random(
                            15,
                            25
                    );



            case "Tanker" ->
                    random(
                            10,
                            18
                    );



            case "Bulk Carrier" ->
                    random(
                            10,
                            16
                    );



            case "Fishing" ->
                    random(
                            5,
                            12
                    );



            default ->
                    random(
                            10,
                            18
                    );

        };


    }









    private double calculateEngineLoad(
            double speed
    ){


        double load =
                20 +
                        speed / 25 * 80;



        if(load > 100){

            load = 100;

        }



        return Math.round(
                load * 100
        ) / 100.0;


    }








    private double random(
            double min,
            double max
    ){


        return ThreadLocalRandom.current()
                .nextDouble(
                        min,
                        max
                );

    }


}