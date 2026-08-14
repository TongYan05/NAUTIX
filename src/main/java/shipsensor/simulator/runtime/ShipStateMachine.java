package shipsensor.simulator.runtime;


import org.springframework.stereotype.Component;


@Component
public class ShipStateMachine {


    /**
     * 更新船舶运行状态
     */
    public void update(ShipRuntimeState state) {


        switch (state.getMode()) {


            /**
             * 停泊
             */
            case STOP -> {

                state.setSpeed(0);

                state.setEngineLoad(0);

                reduceFuel(state, 0.001);

            }


            /**
             * 离港
             */
            case LEAVING_PORT -> {


                double speed =
                        Math.min(
                                state.getSpeed() + 0.3,
                                8
                        );


                state.setSpeed(speed);


                updateEngineLoad(state);


                consumeFuel(state);

            }


            /**
             * 正常航行
             */
            case CRUISING -> {


                double speed =
                        state.getSpeed();


                if (speed < 15) {

                    speed += 0.1;

                }


                state.setSpeed(speed);


                updateEngineLoad(state);


                consumeFuel(state);


            }


            /**
             * 转向
             */
            case TURNING -> {


                state.setHeading(
                        (state.getHeading() + 5) % 360
                );


                updateEngineLoad(state);


                consumeFuel(state);


            }


            /**
             * 到港
             */
            case ARRIVING -> {


                double speed =
                        Math.max(
                                0,
                                state.getSpeed() - 0.2
                        );


                state.setSpeed(speed);


                updateEngineLoad(state);


                consumeFuel(state);


            }


            /**
             * 抛锚
             */
            case ANCHORING -> {


                state.setSpeed(0);


                state.setEngineLoad(5);


                reduceFuel(state, 0.0005);


            }


        }


    }


    /**
     * 根据速度计算发动机负载
     */
    private void updateEngineLoad(
            ShipRuntimeState state
    ) {


        double load =
                state.getSpeed()
                        / 15.0
                        * 80;


        if (load > 100) {

            load = 100;

        }


        if (load < 0) {

            load = 0;

        }


        state.setEngineLoad(load);


    }


    /**
     * 航行油耗
     */
    private void consumeFuel(
            ShipRuntimeState state
    ) {


        double consumption =
                state.getEngineLoad()
                        * 0.0005;


        reduceFuel(
                state,
                consumption
        );


    }


    /**
     * 减少燃油
     */
    private void reduceFuel(
            ShipRuntimeState state,
            double amount
    ) {


        double fuel =
                state.getFuelPercent()
                        - amount;


        if (fuel < 0) {

            fuel = 0;

        }


        state.setFuelPercent(fuel);


    }


}