package shipsensor.simulator.scheduler;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shipsensor.simulator.route.RouteNavigator;
import shipsensor.simulator.runtime.RuntimeStateManager;
import shipsensor.simulator.runtime.ShipRuntimeState;
import shipsensor.simulator.runtime.ShipStateMachine;


@Component
@RequiredArgsConstructor
public class RuntimeStateScheduler {


    private final RuntimeStateManager runtimeStateManager;

    private final ShipStateMachine shipStateMachine;

    private final RouteNavigator routeNavigator;



    /**
     * 每秒更新运行状态
     */
    @Scheduled(fixedRate = 1000)
    public void update(){


        for(ShipRuntimeState state :
                runtimeStateManager.getAll()){


            shipStateMachine.update(state);


            routeNavigator.update(state);


            runtimeStateManager.put(state);

        }


        /*
         * 批量保存数据库
         *
         * ship_runtime_status
         */
        runtimeStateManager.saveAll();


    }


}